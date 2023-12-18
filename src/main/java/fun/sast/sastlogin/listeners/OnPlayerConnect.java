package fun.sast.sastlogin.listeners;


import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class OnPlayerConnect {
    public static void listen(ServerPlayerEntity player) {
        player.setInvulnerable(true);
        player.sendMessage(Text.literal("§9Welcome to the server, in order to play, you must log in.\n§eLog in and bind sast link using /login and bind sast-link account using /bind \n§eCheck account use /account"), false);
    }
}