package fun.sast.sastlogin.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import fun.sast.sastlogin.client.sastlink.UserChecker;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.io.IOException;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class LoginCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("login").executes(context -> {
                    context.getSource().sendFeedback(Text.literal("§aChecking"));
                    try {
                        UserChecker.openSastLink();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return 1;
                }
        ));
    }
}
