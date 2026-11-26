import java.util.ArrayList;
public class Playlist {

    // Fields
    private String name;
    private int size;
    private ArrayList<Song> songs;

    // Constructor
    public Playlist(String name, ArrayList<Song> songs) {
        this.name = name;
        this.songs = songs;
	this.size = this.songs.size();
    }

    // Getters
    public String getName() {
        return name;
    }
    public int getSize() {
        return size;
    }
    public ArrayList<Song> getSongs() {
	return songs;
    }
    // Add & remove
    public String addSong(Song s) {
        songs.add(s);
        size = songs.size();
        return "Song added: " + s.name + ". ID: " + s.id;
    }
    public String removeSong(Song s) {
        Song removed = null;
        if (size == 0) {
            return "Empty Playlist";
        }
        for (int i = 0; i < size; i++) {
            if (songs.get(i).equals(s)) {
                removed = songs.remove(i);
                size = songs.size();
                return  "Song removed: " + removed.name
                    + ". ID: " + removed.id;
            }
        }
        
        return "Song not found";
    }

    // Backup
    public Playlist backup() {
        Playlist backup = new Playlist(name + " (backup)", songs);
        return backup;
    }
    
    // Compare two playlists
    public String compareWith(Playlist p) {
        ArrayList<Song> removed = new ArrayList<>();
        ArrayList<Song> added = new ArrayList<>();

        for (Song song : this.songs) {
            if (!p.songs.contains(song)) {
                added.add(song);
            }
        }
        for (Song song : p.songs) {
            if (!this.songs.contains(song)) {
                removed.add(song);
            }
        }

        String inThis = "";
        String inP= "";

        for (Song song : added) {
            inThis += song.name + ", ";
        }
        for (Song song : removed) {
            inP += song.name + ", ";
        }
        return ("Songs not in " + p.name + ": \n" + inThis + "\nSongs not in " + this.name + ": \n" + inP);
    }
}
