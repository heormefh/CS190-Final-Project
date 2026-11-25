import java.net.URI;
import java.awt.Desktop;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Base64;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SpotifyAuth {
    private static final String CLIENT_ID = "acb9de788e404f42934c89b29de2b613";
    private static final String REDIRECT_URI = "http://127.0.0.1:8888/callback";
    private static String codeVerifier;

    private static String generateCodeVerifier() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private static String generateCodeChallenge(String verifier) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(
            verifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
    public static String authorize() throws Exception {
        codeVerifier = generateCodeVerifier();
        String challenge = generateCodeChallenge(codeVerifier);
        String scope = "playlist-read-private "
                + "playlist-modify-private "
                + "playlist-modify-public";
        String url = "https://accounts.spotify.com/authorize"
                + "?client_id=" + CLIENT_ID
                + "&response_type=code"
                + "&redirect_uri=" + java.net.URLEncoder.encode(
                        REDIRECT_URI, "UTF-8")
                + "&code_challenge_method=S256"
                + "&code_challenge=" + challenge
                + "&scope=" + java.net.URLEncoder.encode(scope, "UTF-8");
        return url;
    }
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(8888);
        System.out.println("Waiting for Spotify...");
        String authorizationURL = authorize();
        Desktop.getDesktop().browse(URI.create(authorizationURL));
        Socket socket = server.accept();

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        String request = in.readLine();
        System.out.println("Request: " + request);
        String code = "";
        if (request != null) {
            String path = request.split(" ")[1];
            String query = path.split("\\?", 2)[1];
            for (String parameter : query.split("&")) {
                String[] pair = parameter.split("=", 2);
                if (pair.length != 2) {
                    continue;
                }
                if (pair[0].equals("code")) {
                    code = URLDecoder.decode(
                            pair[1], StandardCharsets.UTF_8
                    );
                }
                
            }
            
        }
        if (code.equals("")) {
            throw new Exception("Authorization code not received");
        }
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println("Connection: close");
        out.println();
        out.println("<h1>Spotify authorization received!</h1>");
        out.flush();
        socket.close();
        server.close();
        System.out.println("Authorization Code: " + code);
        System.out.println("Code Verifier: " + codeVerifier);
        HttpClient client = HttpClient.newHttpClient();
        String body = "grant_type=authorization_code" + "&client_id=" + CLIENT_ID + "&code=" + code
            + "&redirect_uri=" + java.net.URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8)
            + "&code_verifier=" + java.net.URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(
            URI.create("https://accounts.spotify.com/api/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
        String json = response.body();
        String[] parts = json.split("\"access_token\":\"");
        if (parts.length < 2) {
            throw new Exception("Access Token not found: " + json);
        }
        String accessToken = parts[1].split("\"")[0];
        System.out.println("Access Token: " + accessToken);
        HttpRequest playlistRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://api.spotify.com/v1/me/playlists?limit=50"))
            .header("Authorization", "Bearer " + accessToken)
            .GET()
            .build();
        HttpResponse<String> playlistResponse = client.send(playlistRequest, 
            HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + playlistResponse.statusCode());
        System.out.println("Playlists: " + playlistResponse.body());
    }
}

