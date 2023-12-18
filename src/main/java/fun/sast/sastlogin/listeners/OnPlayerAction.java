package fun.sast.sastlogin.listeners;


import fun.sast.sastlogin.model.UserAdapter;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnPlayerAction {
    public static boolean canInteract(ServerPlayNetworkHandler networkHandler) {
        ServerPlayerEntity player = networkHandler.player;
        return UserAdapter.anyLogin(player.getUuidAsString());
    }
}