package fun.sast.sastlogin.listeners;


import fun.sast.sastlogin.model.UserAdapter;
import fun.sast.sastlogin.sastlink.SastLinkServiceAdapter;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class OnPlayerConnect {
    public static void listen(ServerPlayerEntity player) {
        player.setInvulnerable(true);
        UserAdapter.logout(player.getUuidAsString());
        player.sendMessage(Text.literal("§9Welcome to the server, in order to play, you must log in.\n§eLog in and bind sast link using /login and bind sast-link account using /bind \n§eCheck account use /account"), false);
    }
}