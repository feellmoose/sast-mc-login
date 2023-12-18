package fun.sast.sastlogin.listeners;


import fun.sast.sastlogin.model.User;
import fun.sast.sastlogin.model.UserAdapter;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnPlayerLeave {
    public static void listen(ServerPlayerEntity player) {
        UserAdapter.logout(player.getUuidAsString());
    }
}