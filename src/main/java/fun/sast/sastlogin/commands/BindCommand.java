package fun.sast.sastlogin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import fun.sast.sastlogin.sastlink.UserBinder;
import fun.sast.sastlogin.sastlink.UserChecker;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BindCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("bind").then(argument("link", StringArgumentType.word())
                .executes(context -> {
                    String linkId = StringArgumentType.getString(context, "link");
                    ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
                    boolean success = UserBinder.bindLinkId(player.getUuidAsString(),linkId,player.getName().getString());
                    if(success){
                        context.getSource().sendFeedback(() -> Text.literal("§aBind success."),false);
                    }else {
                        context.getSource().sendFeedback(() -> Text.literal("§cBind failed linkId has been bound."),false);
                    }
                    return 1;
                })));
    }
}
