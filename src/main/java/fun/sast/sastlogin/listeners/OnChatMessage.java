package fun.sast.sastlogin.listeners;


import fun.sast.sastlogin.model.Player;
import fun.sast.sastlogin.model.PlayerAdapter;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnChatMessage {
    public static boolean canSendMessage(ServerPlayNetworkHandler networkHandler, ChatMessageC2SPacket packet) {
        ServerPlayerEntity player = networkHandler.player;
        Player p = PlayerAdapter.getPlayer(player.getUuidAsString());
        String message = packet.chatMessage();
        if (p == null) return false;
        if (!p.isLogin() && (message.startsWith("/login") || message.startsWith("/bind"))) {
            return true;
        }
        return p.isLogin();
    }
}