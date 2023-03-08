package songlib;

import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

/*
 * @Author  Alex Varjabedian
 * @Author  Nima Fallah
 */

public class Controller {

    @FXML private TextField name;
    @FXML private TextField artist;
    @FXML private TextField album;
    @FXML private TextField year;
    @FXML private ListView<String> detail_list, edit_list;
    @FXML private TextField add_name;
    @FXML private TextField add_artist;
    @FXML private TextField add_album;
    @FXML private TextField add_year;
    @FXML private Button addButton;
    @FXML private TextField edit_name;
    @FXML private TextField edit_artist;
    @FXML private TextField edit_album;
    @FXML private TextField edit_year;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ObservableList<String> obsList;
    private static String directory;
    public static boolean startup = true;

    public void initialize() {
        directory = "./songlib/Data/songs.txt"; // Main directory set
        try {
            loadSongs();
        } catch (Exception e) {
            System.out.println("songs.txt does not exist. It will be created once a song has been added.");
        }
        showInfo();
        startup = false;
    }

    public void loadSongs() throws FileNotFoundException {
        File f = new File(directory);
        Scanner scanner = new Scanner(f);
        while (scanner.hasNextLine()) {
            String name = scanner.nextLine();
            String artist = scanner.nextLine();
            String album = scanner.nextLine();
            String year = scanner.nextLine();

            if (album.equals("|||") && year.equals("|||")) {
                SongCollection.addSong(name, artist);
            } else if (album.equals("|||")) {
                SongCollection.addSong(name, artist, Integer.parseInt(year));
            } else if (year.equals("|||")) {
                SongCollection.addSong(name, artist, album);
            } else {
                SongCollection.addSong(name, artist, album, Integer.parseInt(year));
            }
        }
        scanner.close();
    }

    public static void writeToFile(String name, String artist, String album, Integer year) throws IOException {
        File f = new File(directory);
        PrintWriter writer = new PrintWriter(new FileWriter(f, true));
        writer.println(name);
        writer.println(artist);

        if (album == null) {
            writer.println("|||");
        } else {
            writer.println(album);
        }
        if (year == null) {
            writer.println("|||");
        } else {
            writer.println(year);
        }
        writer.close();
    }

    public void updateEdit()
    {
        int index = edit_list.getSelectionModel().getSelectedIndex();

        if (index == -1) {
            edit_name.setText("");
            edit_artist.setText("");
            edit_album.setText("");
            edit_year.setText("");
            return;
        }

        Song s = SongCollection.get(index);
        edit_name.setText(s.getName());
        edit_artist.setText(s.getArtist());
        
        if (s.getAlbum() == null) {
            edit_album.setText("");
        } else {
            edit_album.setText(s.getAlbum());
        }

        if (s.getYear() == -1) {
            edit_year.setText("");
        } else {
            edit_year.setText(Integer.toString(s.getYear()));
        }
    }

    public void showInfo() {
        obsList = FXCollections.observableArrayList();
        for (int i = 0; i < SongCollection.getNumSongs(); i++) {
            obsList.add(SongCollection.getSongs().get(i).getName() + " (" + SongCollection.getSongs().get(i).getArtist() + ")");
        }
        detail_list.setItems(obsList);
        edit_list.setItems(obsList);
        if (SongCollection.getNumSongs() > 0 && startup) {
            detail_list.getSelectionModel().clearAndSelect(0);
            edit_list.getSelectionModel().clearAndSelect(0);
            updateDetails();
        }
    }
    
    public void pressAddButton() throws IOException {
        // confirmation
        if (!confirmAdd()) return;
        // catching errors
        if (add_year.getText().trim().length() > 0) {
            try {
                Integer.parseInt(add_year.getText().trim());
            } catch (Exception e) {
                handleStringYear(); // also handles non-integer values for year
                add_year.setText("");
                return;
            }
        }
        if (add_year.getText().trim().length() > 0 && (Integer.parseInt(add_year.getText().trim()) < 0 || Integer.parseInt(add_year.getText().trim()) > 9999)) {
            handleInvalidYear();
            add_year.setText("");
            return;
        }
        if (add_name.getText().contains("|") || add_artist.getText().contains("|") || add_album.getText().contains("|")) {
            handlePipe();
            add_name.setText("");
            add_artist.setText("");
            add_album.setText("");
            return;
        }
        // adding songs
        int status;
        if (add_name.getText().trim().length() != 0 && add_artist.getText().trim().length() != 0) {
            if (add_album.getText().length() == 0 && add_year.getText().length() == 0) {
                status = SongCollection.addSong(add_name.getText().trim(), add_artist.getText().trim());
            } else if (add_year.getText().length() == 0) {
                status = SongCollection.addSong(add_name.getText().trim(), add_artist.getText().trim(), add_album.getText().trim());
            } else if (add_album.getText().length() == 0) {
                status = SongCollection.addSong(add_name.getText().trim(), add_artist.getText().trim(), Integer.parseInt(add_year.getText().trim()));
            } else {
                status = SongCollection.addSong(add_name.getText().trim(), add_artist.getText().trim(), add_album.getText().trim(), Integer.parseInt(add_year.getText().trim()));
            }
        } else {
            handleInsufficientAddInformation();
            status = -1;
        }
        int index = SongCollection.getIndex(add_name.getText().trim(), add_artist.getText().trim());
        // clear input fields
        add_name.setText("");
        add_artist.setText("");
        add_album.setText("");
        add_year.setText("");
        if (status != -1 && index != -1) {
            if (obsList.size() != 0) {
                obsList.add(index, SongCollection.get(index).getName() + " (" + SongCollection.get(index).getArtist() + ")");
                detail_list.getSelectionModel().clearAndSelect(index);
            } else {
                obsList.add(SongCollection.get(index).getName() + " (" + SongCollection.get(index).getArtist() + ")");
                detail_list.getSelectionModel().clearAndSelect(0);
            }
            if (obsList.size() == 1) {
                edit_list.getSelectionModel().clearAndSelect(0);
            }
        }
        updateDetails();
    }
    
    /*
    * THIS METHOD FIRST SAVES INFORMATION ABOUT THE SONG, AND THEN DELETES IT, AND THEN ADDS A NEW SONG WITH THE NEW INFORMATION.
    */
    public void pressEditButton() throws FileNotFoundException, IOException {
        String old_name;
        String old_artist;
        String old_album = null;
        int old_year = -1;
        int index = edit_list.getSelectionModel().getSelectedIndex();
        if (index == -1) return;
        // confirmation
        if (!confirmEdit()) return;
        // STEP 1: GET INFORMATION
        Song s = SongCollection.get(index);
        
        // catching errors
        if (edit_year.getText().trim().length() > 0) {
            try {
                Integer.parseInt(edit_year.getText().trim());
            } catch (Exception e) {
                handleStringYear(); // also handles non-integer values for year
                updateDetails();
                return;
            }
        }
        if (edit_year.getText().trim().length() > 0 && (Integer.parseInt(edit_year.getText().trim()) < 0 || Integer.parseInt(edit_year.getText().trim()) > 9999)) {
            handleInvalidYear();
            updateDetails();
            return;
        }
        if (edit_name.getText().contains("|") || edit_artist.getText().contains("|") || edit_album.getText().contains("|")) {
            handlePipe();
            updateDetails();
            return;
        }
        if (edit_name.getText().trim().length() == 0 || edit_artist.getText().trim().length() == 0) {
            handleInsufficientAddInformation();
            updateDetails();
            return;
        }
        old_name = edit_name.getText().trim();
        old_artist = edit_artist.getText().trim();
        if (SongCollection.getIndex(old_name, old_artist) != -1 && SongCollection.getIndex(old_name, old_artist) != index) {
            handleDuplicateSong();
            updateDetails();
            return;
        }
        
        if (edit_album.getText().trim().length() != 0) old_album = edit_album.getText().trim();
        if (edit_year.getText().trim().length() != 0) old_year = Integer.parseInt(edit_year.getText().trim());
        
        // Since there are no errors, it is safe to delete the song and replace it with a new one.
        // STEP 2: DELETE SONG
        String nameTarget = s.getName();
        String artistTarget = s.getArtist();
        
        File f = new File(directory);
        File temp = new File("./songlib/Data/temp.txt");
        Scanner scanner = new Scanner(f);
        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
        
        while (scanner.hasNextLine()) {
            String name = scanner.nextLine();
            String artist = scanner.nextLine();
            
            if (name.equals(nameTarget) && artist.equals(artistTarget)) {
                scanner.nextLine();
                scanner.nextLine();
                continue;
            }

            writer.write(name);
            writer.newLine();
            writer.write(artist);
            writer.newLine();
            writer.write(scanner.nextLine());
            writer.newLine();
            writer.write(scanner.nextLine());
            writer.newLine();
        }

        scanner.close();
        writer.close();
        scanner = new Scanner(temp);
        writer = new BufferedWriter(new FileWriter(f));

        while (scanner.hasNextLine()) {
            writer.write(scanner.nextLine());
            writer.newLine();
        }

        obsList.remove(index);
        SongCollection.delete(index);
        scanner.close();
        writer.close();

        // STEP 3: ADD NEW SONG
        // adding songs
        if (old_album == null && old_year == -1) {
            SongCollection.addSong(old_name, old_artist);
        } else if (old_year == -1) {
            SongCollection.addSong(old_name, old_artist, old_album);
        } else if (old_album == null) {
            SongCollection.addSong(old_name, old_artist, old_year);
        } else {
            SongCollection.addSong(old_name, old_artist, old_album, old_year);
        }

        index = SongCollection.getIndex(old_name, old_artist);
        showInfo();
        edit_list.getSelectionModel().clearAndSelect(index);
        updateDetails();
    }

    public void pressDeleteButton() throws FileNotFoundException, IOException {
        int index = detail_list.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            return;
        }
        //confirmation
        if (!confirmDelete()) return;

        Song s = SongCollection.get(index);
        String nameTarget = s.getName();
        String artistTarget = s.getArtist();

        File f = new File(directory);
        File temp = new File("./songlib/Data/temp.txt");
        Scanner scanner = new Scanner(f);
        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));

        while (scanner.hasNextLine()) {
            String name = scanner.nextLine();
            String artist = scanner.nextLine();

            if (name.equals(nameTarget) && artist.equals(artistTarget)) {
                scanner.nextLine();
                scanner.nextLine();
                continue;
            }

            writer.write(name);
            writer.newLine();
            writer.write(artist);
            writer.newLine();
            writer.write(scanner.nextLine());
            writer.newLine();
            writer.write(scanner.nextLine());
            writer.newLine();
        }

        scanner.close();
        writer.close();
        scanner = new Scanner(temp);
        writer = new BufferedWriter(new FileWriter(f));

        while (scanner.hasNextLine()) {
            writer.write(scanner.nextLine());
            writer.newLine();
        }

        obsList.remove(index);
        SongCollection.delete(index);
        scanner.close();
        writer.close();

        if (index < obsList.size()) {
            detail_list.getSelectionModel().clearAndSelect(index);
        } else {
            detail_list.getSelectionModel().clearAndSelect(index-1);
        }
        updateDetails();
    }

    public void updateDetails()
    {
        int index = detail_list.getSelectionModel().getSelectedIndex();

        if (index == -1) {// If list is empty or if nothing is selected
            name.setText("");
            artist.setText("");
            album.setText("");
            year.setText("");
            updateEdit();
            return;
        }

        Song s = SongCollection.get(index);
        name.setText("Name: " + s.getName());
        artist.setText("Artist: " + s.getArtist());
        
        if (s.getAlbum() == null) {
            album.setText("Album: ");
        } else {
            album.setText("Album: " + s.getAlbum());
        }

        if (s.getYear() == -1) {
            year.setText("Year: ");
        } else {
            year.setText("Year: " + s.getYear());
        }

        updateEdit();
    }

    // ERROR POP-UPS
    public static void handleInsufficientAddInformation() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Insufficient Information");
        alert.setContentText("Please make sure to type in a song name and artist.");
        alert.showAndWait();
    }    
    public static void handleDuplicateSong() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Duplicate Song");
        alert.setContentText("This song already exists in the library.");
        alert.showAndWait();
    }
    public static void handleStringYear() { // also handles non-integer values for year
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid Input");
        alert.setContentText("Please enter an integer value for 'year'.");
        alert.showAndWait();
    }
    public static void handleInvalidYear() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid Input");
        alert.setContentText("Year must be at least 0 and at most 9999.");
        alert.showAndWait();
    }
    public static void handlePipe() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid Input");
        alert.setContentText("You may not use a '|' in your input.");
        alert.showAndWait();
    }

    // CONFIRMATION POP-UPS
    public static boolean confirmAdd() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Add Song?");
        alert.setContentText("Would you like to add this song?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) return true;
        return false;
    }
    public static boolean confirmEdit() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Save Changes?");
        alert.setContentText("Would you like to save these changes?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) return true;
        return false;
    }
    public static boolean confirmDelete() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Song?");
        alert.setContentText("Are you sure you want to delete this song?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) return true;
        return false;
    }
}