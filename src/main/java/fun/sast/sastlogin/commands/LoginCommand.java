package fun.sast.sastlogin.commands;

import com.mojang.brigadier.CommandDispatcher;
import fun.sast.sastlogin.sastlink.UserChecker;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;

public class LoginCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("login")
                .executes(context -> {
                    ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
                    UserChecker.checkBySastLink(player.getUuidAsString());
                    return 1;
                }));
    }
}
