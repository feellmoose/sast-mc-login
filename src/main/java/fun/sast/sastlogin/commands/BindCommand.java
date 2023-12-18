package fun.sast.sastlogin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import fun.sast.sastlogin.model.User;
import fun.sast.sastlogin.sastlink.SastLinkServiceAdapter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BindCommand {
    public static void bindRegister(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("bind")
                .then(argument("account", StringArgumentType.word())
                        .then(argument("password", StringArgumentType.word())
                                .executes(context -> {
                                    String email = StringArgumentType.getString(context, "account") + "@njupt.edu.cn";
                                    String password = StringArgumentType.getString(context, "password");
                                    ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
                                    boolean success = SastLinkServiceAdapter.bindAccount(player.getUuidAsString(), player.getName().getString(), email, password);
                                    if (success) {
                                        context.getSource().sendFeedback(() -> Text.literal("§aBind success."), false);
                                    } else {
                                        context.getSource().sendFeedback(() -> Text.literal("§cBind failed."), false);
                                    }
                                    return 1;
                                }))));
    }

    public static void unBindRegister(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("unbind")
                .then(argument("account", StringArgumentType.word())
                        .executes(context -> {
                            String linkId = StringArgumentType.getString(context, "account");
                            ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
                            boolean success = SastLinkServiceAdapter.unBindAccount(player.getUuidAsString(), linkId);
                            if (success) {
                                player.setInvulnerable(false);
                                context.getSource().sendFeedback(() -> Text.literal("§aUnBind success."), false);
                            } else {
                                context.getSource().sendFeedback(() -> Text.literal("§cUnBind failed."), false);
                            }
                            return 1;
                        })));
    }

    public static void accountRegister(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("account")
                .executes(context -> {
                    ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());

                    List<User> users = SastLinkServiceAdapter.getLinkAccounts(player.getUuidAsString());
                    context.getSource().sendFeedback(() -> Text.literal("§aBOUND SAST-LINK ACCOUNTS:"), false);
                    context.getSource().sendFeedback(() -> Text.literal(
                            "§a " + String.format("%10s", "username")
                                    + String.format("%15s", "account")
                                    + String.format("%25s", "email")), false);
                    users.forEach(user -> {
                        String username = String.format("%10s", user.getName());
                        String linkId = String.format("%15s", user.getLinkId());
                        String email = String.format("%25s", user.getEmail());
                        context.getSource().sendFeedback(() -> Text.literal("§a" + username + linkId + email), false);
                    });

                    List<User.Player> players = SastLinkServiceAdapter.getMCAccounts(player.getUuidAsString());
                    context.getSource().sendFeedback(() -> Text.literal("§aBOUND PLAYERS:"), false);
                    context.getSource().sendFeedback(() -> Text.literal(
                            "§a " + String.format("%10s", "username")), false);
                    players.forEach(p -> {
                        String username = String.format("%10s", p.getName());
                        context.getSource().sendFeedback(() -> Text.literal("§a" + username), false);
                    });
                    return 1;
                }));
    }
}
