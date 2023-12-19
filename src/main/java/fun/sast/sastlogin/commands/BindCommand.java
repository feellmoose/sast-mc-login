package fun.sast.sastlogin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import fun.sast.sastlogin.model.User;
import fun.sast.sastlogin.sastlink.SastLinkServiceAdapter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BindCommand {
    public static void bindRegister(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("bind")
                .then(argument("sast-link-id", StringArgumentType.word())
                        .executes(context -> {
                            String linkId = StringArgumentType.getString(context, "sast-link-id");
                            ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
                            SastLinkServiceAdapter.linkIdPlayers.put(linkId, player);
                            context.getSource().sendFeedback(() -> Text.literal("§aPlease use sast-link auth-url below."), false);
                            context.getSource().sendFeedback(() -> Text.literal(SastLinkServiceAdapter.getAuthUrl())
                                    .setStyle(Style.EMPTY
                                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, SastLinkServiceAdapter.getAuthUrl()))
                                            .withUnderline(true)), false);
                            context.getSource().sendFeedback(() -> Text.literal("§bBind expire time: 60s."), false);
                            SastLinkServiceAdapter.delayFailedBindTask(linkId);
                            return 1;
                        })
                        .then(argument("password", StringArgumentType.word())
                                .executes(context -> {
                                    String linkId = StringArgumentType.getString(context, "sast-link-id");
                                    String email = linkId + "@njupt.edu.cn";
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
                .then(argument("sast-link-id", StringArgumentType.word())
                        .executes(context -> {
                            String linkId = StringArgumentType.getString(context, "sast-link-id");
                            ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
                            boolean success = SastLinkServiceAdapter.unBindAccount(player.getUuidAsString(), linkId);
                            if (success) {
                                player.setInvulnerable(true);
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
                    context.getSource().sendFeedback(() -> Text.literal("§a %10s %15s %25s ".formatted("username", "sast-link-id", "email")), false);
                    users.forEach(user -> context.getSource().sendFeedback(() -> Text.literal("§a %10s %15s %25s ".formatted(user.getName(), user.getLinkId(), user.getEmail())), false));

                    List<User.Player> players = SastLinkServiceAdapter.getMCAccounts(player.getUuidAsString());
                    context.getSource().sendFeedback(() -> Text.literal("§aBOUND PLAYERS:"), false);
                    context.getSource().sendFeedback(() -> Text.literal("§a %10s ".formatted("username")), false);
                    players.forEach(p -> context.getSource().sendFeedback(() -> Text.literal("§a %10s ".formatted(p.getName())), false));
                    return 1;
                }));
    }
}
