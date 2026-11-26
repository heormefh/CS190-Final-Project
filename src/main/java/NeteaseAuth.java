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

    private static final String COOKIE =
        "MUSIC_U=003BF6B4AB440004EB65FBCCB194F28B6327259903AC101ECAD009777C863C5D7DEE0B7228F0177B5955D9BB7C9F7BAD802D77D9FBC3D8D542EDEFC90525CE543D64C7935553F73AD99541D0AED8D7D7D69A75702EFB9F69874F330B63B52830B0041C2A34AEC011807D172E132AA5220EC92C8E592BA009428E5B62E482EDFB18F6737555F77F0F25C64D34AF98D9042B46395768E2843D89E6D5B6191ECA0796FEA2D0CE3E0D9C6129CA0DAF0F5355BB356C9BB33A65FDE5E7B94E2860CACFB1B2EA5B1CB778D3F02B73D87B69B72E35A90A9CD7B4034EDD8CBB208E870F69C645A77FED187B483D566E6088437BDFDE9D56604385DDC5222B9E1D04261F2DE02F9DC13FF5B2541584EE67F2FD4970648B95AE5F8F1804640782E2019B4912BDFF511FFB7198DA1DCBEEE792C2F2642C8EA92F05775FCBAA6C3448DADC155E331456E4C30272733A9952E5CDE61CC9C6D9765AF158546F0D2082815CD6B6085B01912FDB3F7A6735220767C3590D7E59; "
            + "__csrf=37c8e90c20d20b4a986cadf998391b45; NMTID=00OcAjc6x2_eHPOiUqEjdFQl5h9J_gAAAGiEMGmsw";

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
