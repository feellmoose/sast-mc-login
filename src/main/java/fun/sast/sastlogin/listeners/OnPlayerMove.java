package fun.sast.sastlogin.listeners;


import fun.sast.sastlogin.model.Player;
import fun.sast.sastlogin.model.PlayerAdapter;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnPlayerMove {
    public static boolean canMove(ServerPlayNetworkHandler networkHandler) {
        ServerPlayerEntity player = networkHandler.player;
        Player p = PlayerAdapter.getPlayer(player.getUuidAsString());
        if (!p.isLogin()) {
            player.teleport(player.getX(), player.getY(), player.getZ()); // teleport to sync client position
        }
        return p.isLogin();
    }
}