package songlib;

/*
 * @Author  Alex Varjabedian
 * @Author  Nima Fallah
 */

public class Song {
    private String name;
    private String artist;
    private String album;
    private int year = -1;

    public Song(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }
    public Song(String name, String artist, String album) {
        this.name = name;
        this.artist = artist;
        this.album = album;
    }
    public Song(String name, String artist, int year) {
        this.name = name;
        this.artist = artist;
        this.year = year;
    }
    public Song(String name, String artist, String album, int year) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.year = year;
    }
    
    public String toString() {
        return "Name: " + name + " | Artist: " + artist + " | Album: " + album + " | Year: " + year + "\n";
    }
    public String getName() {
        return name;
    }
    public String getArtist() {
        return artist;
    }
    public String getAlbum() {
        return album;
    }
    public int getYear() {
        return year;
    }
}