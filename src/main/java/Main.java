import com.google.gson.Gson;
import java.util.ArrayList;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class Main {
    public static void main(String[] args) throws Exception {

        // 1. Create HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // 2. Create HTTP request
        String url = "http://localhost:3000/playlist/detail?id=18400972350";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // 3. Send request and receive response
        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // 4. Store JSON response
        String jsonText = response.body();

        // 5. Parse JSON
        JsonObject root = JsonParser.parseString(jsonText)
                .getAsJsonObject();

        JsonObject playlist = root.getAsJsonObject("playlist");

        JsonArray tracks = playlist.getAsJsonArray("tracks");

        // 6. Print number of songs
        System.out.println("Number of songs: " + tracks.size());
	Gson gson = new Gson();

	ArrayList<Song> songs = new ArrayList<>();

        for (int i = 0; i < tracks.size(); i++) {
            JsonObject track = tracks.get(i).getAsJsonObject();
    
            Song song = gson.fromJson(track, Song.class);

            songs.add(song);
        }	
    }
}

