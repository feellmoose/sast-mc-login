package fun.sast.sastlogin;

import fun.sast.sastlogin.commands.BindCommand;
import fun.sast.sastlogin.commands.LoginCommand;
import fun.sast.sastlogin.config.SastLinkConfig;
import fun.sast.sastlogin.model.UserAdapter;
import fun.sast.sastlogin.sastlink.SastLinkServiceAdapter;
import fun.sast.sastlogin.server.ListenServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import sast.sastlink.sdk.exception.SastLinkException;
import sast.sastlink.sdk.model.response.data.AccessToken;
import sast.sastlink.sdk.model.response.data.User;

import java.io.IOException;

import static fun.sast.sastlogin.config.SastLinkConfig.sastLinkConfig;

public class Sast_login implements ModInitializer {


    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            BindCommand.bindRegister(dispatcher);
            BindCommand.unBindRegister(dispatcher);
            BindCommand.accountRegister(dispatcher);
            LoginCommand.register(dispatcher);
        });
        openServer();
        UserAdapter.init();
    }

    public static void openServer() {
        ListenServer.create((bufferedReader, bufferedWriter) -> {
            try {
                String request = bufferedReader.readLine();
                if(request.contains("?code=")){
                    String code = request.substring(request.indexOf("?code=") + 6, request.indexOf("&"));
                    try {
                        SastLinkServiceAdapter.Auth(code);
                    }catch (SastLinkException sastLinkException){
                        String OUTPUT = "<html><head><title>login result</title></head><body><p>Login failed, Msg:  "+ sastLinkException.getMessage()+"</p></body></html>";
                        bufferedWriter.write("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nContent-Length: " + OUTPUT.length() + "\r\n\r\n" + OUTPUT);
                    }
                    String OUTPUT = "<html><head><title>login result</title></head><body><p>Login success, Welcome and have a good time!</p></body></html>";
                    bufferedWriter.write("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nContent-Length: " + OUTPUT.length() + "\r\n\r\n" + OUTPUT);
                }
                bufferedWriter.write("HTTP/1.1 200 OK\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).openServer(sastLinkConfig.getPort());
    }

}
