import java.util.ArrayList;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class NeteaseAuth {

    private static final String BASE_URL = "http://localhost:3000";

    private static final String COOKIE = System.getenv("NCM_COOKIE");
    
    static {
        if (COOKIE == null || COOKIE.isBlank()) {
            throw new IllegalStateException("NCM_COOKIE is not configured!");
        }
    }    
    private final HttpClient client = HttpClient.newHttpClient();

    public void checkLogin() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/login/status"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"cookie\":\"" + COOKIE + "\"}"
            ))
            .build();

        HttpResponse<String> response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        System.out.println(response.body());
    }
    public Playlist getPlaylist(long playlistId)
        throws IOException, InterruptedException {
        String detailJson = getPlaylistDetail(playlistId);
        JsonObject detailRoot =
            JsonParser.parseString(detailJson).getAsJsonObject();

        JsonObject playlistJson =
            detailRoot.getAsJsonObject("playlist");

        String playlistName =
            playlistJson.get("name").getAsString();

        String tracksJson = getAllTracks(playlistId);

        JsonObject tracksRoot =
            JsonParser.parseString(tracksJson).getAsJsonObject();

        // Debug information
        System.out.println("API response code: "
            + tracksRoot.get("code"));

        System.out.println("API response: "
            + tracksRoot);

        if (!tracksRoot.has("songs")
                || !tracksRoot.get("songs").isJsonArray()) {
            throw new IOException(
                "API response does not contain a songs array."
            );
        }

        JsonArray tracks = tracksRoot.getAsJsonArray("songs");

        System.out.println("API tracks count: " + tracks.size());

        Gson gson = new Gson();
        ArrayList<Song> songs = new ArrayList<>();

        for (int i = 0; i < tracks.size(); i++) {
            Song song = gson.fromJson(tracks.get(i), Song.class);
            songs.add(song);
        }

        System.out.println("Java songs count: " + songs.size());

        return new Playlist(playlistId, playlistName, songs);
    }
    private String getPlaylistDetail(long id)
        throws IOException, InterruptedException {
        return postRequest("/playlist/detail", id);
    }

    private String getAllTracks(long id)
            throws IOException, InterruptedException {
        return postRequest("/playlist/track/all", id);
    }
    private String postRequest(String endpoint, long id)
        throws IOException, InterruptedException {

        JsonObject body = new JsonObject();
        body.addProperty("id", id);
        body.addProperty("cookie", COOKIE);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint
                        + "?timestamp=" + System.currentTimeMillis()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }
    public String sendPlaylistOperation(
            String operation, long playlistId, ArrayList<Song> songs)
            throws IOException, InterruptedException {

        //if (songs.isEmpty()) {
          //  return "Empty Playlist";
        //}

        StringBuilder ids = new StringBuilder();

        for (Song song : songs) {
            if (ids.length() > 0) {
                ids.append(",");
            }
            ids.append(song.getId());
        }
        System.out.println("Operation: " + operation);
        System.out.println("Playlist ID: " + playlistId);
        System.out.println("Tracks: " + ids);
        System.out.println("Song count: " + songs.size());
        JsonObject body = new JsonObject();
        body.addProperty("op", operation);
        body.addProperty("pid", playlistId);
        body.addProperty("tracks", ids.toString());
        body.addProperty("cookie", COOKIE);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/playlist/tracks"
                        + "?timestamp=" + System.currentTimeMillis()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );
        return response.body();
    }
    public static void main(String[] args) {
        NeteaseAuth auth = new NeteaseAuth();
        try {
            Playlist playlist = auth.getPlaylist(18400972350L);
            System.out.println("Playlist: " + playlist.getName());
            System.out.println("Songs: " + playlist.getSize());

            for (Song song : playlist.getSongs()) {
                System.out.println(song.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
