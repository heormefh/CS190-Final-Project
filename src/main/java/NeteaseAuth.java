
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

public class NeteaseAuth {

    private static final String BASE_URL = "http://localhost:3000";

    private static final String COOKIE =
        "MUSIC_U=00F0E2130A7DA57ED32571B41B5633C66DE82045200ED9FC3F4975BC60C8F06514FE139105AB7538FF1C3C72ED335F472D24BDFB43E3AFB734F6E967552319D777D9FA5FF8D7442B3039621E9918A25BAB0188AFDA69F1FCE2109696FD5477AA945161D64C122F126EFB93485D5A8B6F140F6188A924930104CB015F77D3F0C743603FD9300DAEACB77624DF75AC253FB7A6A818E184027A6A36E677D9B270AEFFDBF755AF0670BB1519CDC744E220F059FFB670F2AEBACCCF92E696DA0ED06D9AE382051E2298787683BE3F7A3CE762C56FFFBF28303FC407E7E00FCE2537A4241771EFAD24C02B0D69888865A3AF7A713807F9C1FC42C370417C7BD228CC86400F9BA08458AEBB7301F6C8373B929A10BD1E3AB10995913324C9A755A267D2FCDC18834ECC237E6BD1F414742657786E476E5DBE80FD25428FFBBB0D5299115114E8C6A30A2C712C669DC855A646032311EF596291B9B5B4D214088531286D47F437E973CAB372DD0C4C27CD35880B89; "
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
}
