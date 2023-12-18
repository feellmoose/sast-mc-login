package fun.sast.sastlogin.listeners;


import fun.sast.sastlogin.model.UserAdapter;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnPlayerMove {
    public static boolean canMove(ServerPlayNetworkHandler networkHandler) {
        ServerPlayerEntity player = networkHandler.player;
        boolean login = UserAdapter.anyLogin(player.getUuidAsString());
        if (!login) {
            player.teleport(player.getX(), player.getY(), player.getZ()); // teleport to sync client position
        }
        return login;
    }
}