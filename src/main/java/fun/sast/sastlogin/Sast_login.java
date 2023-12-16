package fun.sast.sastlogin;

import fun.sast.sastlogin.browser.ListenServer;
import fun.sast.sastlogin.model.Player;
import fun.sast.sastlogin.model.PlayerAdapter;
import net.fabricmc.api.ModInitializer;
import sast.sastlink.sdk.model.response.data.AccessToken;
import sast.sastlink.sdk.model.response.data.User;
import sast.sastlink.sdk.service.SastLinkService;
import sast.sastlink.sdk.service.impl.HttpClientSastLinkService;

import java.io.IOException;

import static fun.sast.sastlogin.config.SastLinkConfig.sastLinkConfig;

public class Sast_login implements ModInitializer {

    public final static SastLinkService sastLinkService = new HttpClientSastLinkService.Builder()
            .setClientId(sastLinkConfig.getClientId())
            .setClientSecret(sastLinkConfig.getClientSecret())
            .setCodeVerifier(sastLinkConfig.getCodeVerifier())
            .setHostName(sastLinkConfig.getHostName())
            .setRedirectUri(sastLinkConfig.getRedirectUri())
            .build();

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        PlayerAdapter.init();
        ListenServer.create((bufferedReader, bufferedWriter) -> {
            try {
                String res = bufferedReader.readLine();
                String code = res.substring(res.indexOf("?code=") + 6, res.indexOf("&"));
                String uuid = res.substring(res.indexOf("&uuid=") + 7);
                AccessToken accessToken = sastLinkService.accessToken(code);
                User user = sastLinkService.user(accessToken.getAccessToken());
                login(user,uuid);
                bufferedWriter.write("HTTP/1.1 200 OK");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).openServer(6060);
    }

    private static void login(User user,String uuid){
        Player p = PlayerAdapter.getPlayer(uuid);
        if(p == null) {
            PlayerAdapter.addPlayer(new Player(uuid,user.getUserId(),user.getNickname(),false,true));
        } else if (!p.isBan()&&!p.isLogin()) {
            p.setLogin(true);
            PlayerAdapter.updatePlayer(p);
        }
    }

}
