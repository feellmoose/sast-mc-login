package fun.sast.sastlogin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import fun.sast.sastlogin.sastlink.SastLinkServiceAdapter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class LoginCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("login")
                .then(argument("password", StringArgumentType.word()).executes(context -> {
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
