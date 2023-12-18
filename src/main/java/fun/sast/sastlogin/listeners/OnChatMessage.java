package fun.sast.sastlogin.listeners;


import fun.sast.sastlogin.model.UserAdapter;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnChatMessage {
    public static boolean canSendMessage(ServerPlayNetworkHandler networkHandler, ChatMessageC2SPacket packet) {
        ServerPlayerEntity player = networkHandler.player;
        String message = packet.chatMessage();
        if (message.startsWith("/login") || message.startsWith("/bind")) {
            return true;
        }
        return UserAdapter.anyLogin(player.getUuidAsString());
    }
}