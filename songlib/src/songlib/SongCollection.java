package songlib;

import java.util.*;

/*
 * @Author  Alex Varjabedian
 * @Author  Nima Fallah
 */

public class SongCollection {
    private static ArrayList<Song> songs = new ArrayList<Song>();

    private SongCollection() {
        // KEEP THIS HERE TO PREVENT INSTANTIATION OF THIS CLASS
    }

    public static void sort() {
        for (int i = 0; i < songs.size(); i++) {
            for (int j = 0; j < songs.size() - 1; j++) {
                if (songs.get(j).getName().compareToIgnoreCase(songs.get(j+1).getName()) > 0) {
                    Collections.swap(songs, j, j + 1);
                } else if (songs.get(j).getName().compareToIgnoreCase(songs.get(j+1).getName()) == 0) {
                    if (songs.get(j).getArtist().compareToIgnoreCase(songs.get(j+1).getArtist()) > 0) {
                        Collections.swap(songs, j, j + 1);
                    }
                }
            }
        }
    }
    public static ArrayList<Song> getSongs() {
        return songs;
    }
    
    public static Song get(int index) {
        return songs.get(index);
    }

    public static void delete(int index) {
        songs.remove(index);
    }
    
    public static int getIndex(String name, String artist) {// Returns -1 if there is an error
        int index = 0;
        for (Song s : songs) {
            if (s.getName().equals(name) && s.getArtist().equals(artist)) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    public static ArrayList<String> getSongsString() {
        ArrayList<String> stringList = new ArrayList<String>();
        for (Song s : songs) {
            stringList.add(s.getName());
        }
        return stringList;
    }

    public static int getNumSongs() {
        return songs.size();
    }
    // OVERLOADED METHODS FOR DIFFERENT INPUT POSSIBILITIES
    public static int addSong(String name, String artist) {
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getName().toLowerCase().equals(name.toLowerCase()) && songs.get(i).getArtist().toLowerCase().equals(artist.toLowerCase())) {
                Controller.handleDuplicateSong();
                return -1;
            }
        }
        if (!Controller.startup) {
            try {
                Controller.writeToFile(name, artist, null, null);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        Song song = new Song(name, artist);
        songs.add(song);
        sort();
        return 0;
    }
    public static int addSong(String name, String artist, String album) {
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getName().toLowerCase().equals(name.toLowerCase()) && songs.get(i).getArtist().toLowerCase().equals(artist.toLowerCase())) {
                Controller.handleDuplicateSong();
                return -1;
            }
        }
        if (!Controller.startup) {
            try {
                Controller.writeToFile(name, artist, album, null);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        Song song = new Song(name, artist, album);
        songs.add(song);
        sort();
        return 0;
    }
    public static int addSong(String name, String artist, int year) {
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getName().toLowerCase().equals(name.toLowerCase()) && songs.get(i).getArtist().toLowerCase().equals(artist.toLowerCase())) {
                Controller.handleDuplicateSong();
                return -1;
            }
        }
        if (!Controller.startup) {
            try {
                Controller.writeToFile(name, artist, null, year);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        Song song = new Song(name, artist, year);
        songs.add(song);
        sort();
        return 0;
    }
    public static int addSong(String name, String artist, String album, int year) {
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getName().toLowerCase().equals(name.toLowerCase()) && songs.get(i).getArtist().toLowerCase().equals(artist.toLowerCase())) {
                Controller.handleDuplicateSong();
                return -1;
            }
        }
        if (!Controller.startup) {
            try {
                Controller.writeToFile(name, artist, album, year);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        Song song = new Song(name, artist, album, year);
        songs.add(song);
        sort();
        return 0;
    }
}