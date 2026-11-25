public class Song {
    long id;
    String name;
    int dt;
    @Override 
    public boolean equals(Object obj) {
        if (obj instanceof Song other) {
            return this.id == other.id;
        }
        return false;
    }
}
