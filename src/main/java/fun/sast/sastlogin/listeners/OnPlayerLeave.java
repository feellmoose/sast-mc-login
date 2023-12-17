package fun.sast.sastlogin.listeners;


import fun.sast.sastlogin.model.Player;
import fun.sast.sastlogin.model.PlayerAdapter;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalDateTime;

public class OnPlayerLeave {
    public static void listen(ServerPlayerEntity player) {
        Player p = PlayerAdapter.getPlayer(player.getUuidAsString());
        if(p == null) return;
        p.setLogin(false);
        PlayerAdapter.updatePlayer(p);
    }
}