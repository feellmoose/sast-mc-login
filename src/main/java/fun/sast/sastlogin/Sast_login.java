package fun.sast.sastlogin;

import fun.sast.sastlogin.commands.BindCommand;
import fun.sast.sastlogin.model.Player;
import fun.sast.sastlogin.model.PlayerAdapter;
import fun.sast.sastlogin.server.ListenServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            BindCommand.register(dispatcher);
           // LoginCommand.register(dispatcher);
        });

        PlayerAdapter.init();
        ListenServer.create((bufferedReader, bufferedWriter) -> {
            try {
                String res = bufferedReader.readLine();
                String code = res.substring(res.indexOf("?code=") + 6, res.indexOf("&"));
                AccessToken accessToken = sastLinkService.accessToken(code);
                User user = sastLinkService.user(accessToken.getAccessToken());
                login(user);
                bufferedWriter.write("HTTP/1.1 200 OK");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).openServer(6060);
    }

    private static void login(User user) {
        Player p = PlayerAdapter.getPlayerByLinkId(user.getUserId());
        if (!p.isBan() && !p.isLogin()) {
            p.setLogin(true);
            PlayerAdapter.updatePlayer(p);
        }
    }
}
