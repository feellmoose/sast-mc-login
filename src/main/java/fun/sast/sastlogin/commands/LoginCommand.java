package fun.sast.sastlogin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import fun.sast.sastlogin.sastlink.SastLinkServiceAdapter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class LoginCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("login").executes(context -> {
            ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
            SastLinkServiceAdapter.uuidPlayers.put(player.getUuidAsString(), player);
            context.getSource().sendFeedback(() -> Text.literal("§aPlease use sast-link auth-url below."), false);
            context.getSource().sendFeedback(() -> Text.literal(SastLinkServiceAdapter.getAuthUrl())
                    .setStyle(Style.EMPTY
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, SastLinkServiceAdapter.getAuthUrl()))
                            .withUnderline(true)), false);
            context.getSource().sendFeedback(() -> Text.literal("§bLogin expire time: 60s."), false);
            SastLinkServiceAdapter.delayFailedLoginTask(player.getUuidAsString());
            return 1;
        }).then(argument("password", StringArgumentType.word()).executes(context -> {
            String password = StringArgumentType.getString(context, "password");
            ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
            boolean success = SastLinkServiceAdapter.login(player.getUuidAsString(), password);
            if (success) {
                if (!player.isCreative()) player.setInvulnerable(false);
                context.getSource().sendFeedback(() -> Text.literal("§aLogin success."), false);
            } else {
                context.getSource().sendFeedback(() -> Text.literal("§cLogin failed."), false);
            }
            return 1;
        })));
    }
}
