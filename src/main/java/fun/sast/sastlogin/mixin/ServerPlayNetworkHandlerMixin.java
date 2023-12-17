package fun.sast.sastlogin.mixin;


import fun.sast.sastlogin.listeners.OnChatMessage;
import fun.sast.sastlogin.listeners.OnPlayerAction;
import fun.sast.sastlogin.listeners.OnPlayerMove;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    public void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (!OnPlayerMove.canMove((net.minecraft.server.network.ServerPlayNetworkHandler) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    public void onPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
        if (!OnPlayerAction.canInteract((net.minecraft.server.network.ServerPlayNetworkHandler) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (!OnChatMessage.canSendMessage((net.minecraft.server.network.ServerPlayNetworkHandler) (Object) this, packet)) {
            ci.cancel();
        }
    }
}