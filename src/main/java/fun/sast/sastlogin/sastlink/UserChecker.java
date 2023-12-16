package fun.sast.sastlogin.sastlink;

import fun.sast.sastlogin.browser.ListenServer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static fun.sast.sastlogin.browser.Browser.openBrowse;


public class UserChecker {
    private final static String redirect_uri = "http://localhost:6060";
    private final static String server_path = "";
    private final static String client_id = "0651a647-d179-4a6a-bd5e-9bf63e9eec5f";
    private final static String code_challenge = "Qn3Kywp0OiU4NK_AFzGPlmrcYJDJ13Abj_jdL08Ahg8%3D";
    private final static String code_challenge_method = "S256";

    public static void checkBySastLink(String uuid) {
        try {
            //listen for redirect form sast-link website
            ListenServer server = ListenServer.create((bufferedReader, bufferedWriter) -> {
                try {
                    String res = bufferedReader.readLine();
                    String code = res.substring(res.indexOf("?code=") + 6, res.indexOf("&"));
                    //send code to server
                    HttpClient.newHttpClient().send(HttpRequest.newBuilder().uri(new URI(server_path + "?code=" + code+"&uuid="+uuid)).GET().build(), HttpResponse.BodyHandlers.ofString());
                    bufferedWriter.write("HTTP/1.1 200 OK");
                } catch (IOException | URISyntaxException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).openServer(6001);
            //open sast-link website
            openBrowse("https://link.sast.fun/auth" +
                    "?client_id=" + client_id +
                    "&code_challenge=" + code_challenge +
                    "&code_challenge_method=" + code_challenge_method +
                    "&redirect_uri=" + redirect_uri +
                    "&response_type=code&scope=all&state=xyz");
            //close server when receive a msg
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
