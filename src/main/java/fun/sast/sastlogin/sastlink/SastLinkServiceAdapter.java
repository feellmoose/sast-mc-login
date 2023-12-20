package fun.sast.sastlogin.sastlink;

import fun.sast.sastlogin.model.User;
import fun.sast.sastlogin.model.UserAdapter;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import sast.sastlink.sdk.exception.SastLinkException;
import sast.sastlink.sdk.model.response.data.AccessToken;
import sast.sastlink.sdk.test.TestSastLinkServiceAdapter;
import sast.sastlink.sdk.test.data.Token;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static fun.sast.sastlogin.config.SastLinkConfig.sastLinkConfig;

public class SastLinkServiceAdapter {
    public final static TestSastLinkServiceAdapter sastLinkService = new TestSastLinkServiceAdapter.Builder()
            .setClientId(sastLinkConfig.getClientId())
            .setClientSecret(sastLinkConfig.getClientSecret())
            .setCodeVerifier(sastLinkConfig.getCodeVerifier())
            .setHostName(sastLinkConfig.getHostName())
            .setRedirectUri(sastLinkConfig.getRedirectUri())
            .build();

    public static String getAuthUrl() {
        return "https://link.sast.fun/auth" +
                "?client_id=" + sastLinkConfig.getClientId() +
                "&code_challenge=" + sastLinkConfig.getCodeChallenge() +
                "&code_challenge_method=" + sastLinkConfig.getCodeChallengeMethod() +
                "&redirect_uri=" + sastLinkConfig.getRedirectUri() +
                "&response_type=code&scope=all" +
                "&state=xyz";
    }

    public static boolean login(String uuid, String password) {
        List<User> users = UserAdapter.getUsersByUuid(uuid);
        if (users.isEmpty()) return false;
        User user = users.stream().filter(u -> checkSastLinkAccount(u.getEmail(), password) != null).findAny().orElse(null);
        if (user == null) return false;
        User.Player p = user.getPlayers().stream().filter(player -> player.getUuid().equals(uuid)).findAny().orElse(null);
        if (p == null) return false;
        p.setLogin(true);
        UserAdapter.updateUser(user);
        return true;
    }

    public static final ConcurrentHashMap<String, ServerPlayerEntity> uuidPlayers = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    public static final ConcurrentHashMap<String, ServerPlayerEntity> linkIdPlayers = new ConcurrentHashMap<>();

    public static void delayFailedLoginTask(String uuid) {
        scheduledExecutorService.schedule(() -> {
            ServerPlayerEntity player = uuidPlayers.get(uuid);
            if (player != null) {
                player.sendMessage(Text.literal("§cLogin failed: expired please try again or bind first."), false);
                uuidPlayers.remove(uuid);
            }
        }, 60, TimeUnit.SECONDS);
    }

    public static void delayFailedBindTask(String linkId) {
        scheduledExecutorService.schedule(() -> {
            ServerPlayerEntity player = linkIdPlayers.get(linkId);
            if (player != null) {
                player.sendMessage(Text.literal("§cBind failed: expired please try again."), false);
                linkIdPlayers.remove(linkId);
            }
        }, 60, TimeUnit.SECONDS);
    }

    public static void Auth(String code) {
        System.out.println("[sast-link client-server:" + sastLinkConfig.getPort() + "]: receive code:" + code);
        sast.sastlink.sdk.model.response.data.User linkUser;
        try {
            AccessToken accessToken = SastLinkServiceAdapter.sastLinkService.accessToken(code);
            System.out.println("[sast-link client-server:" + sastLinkConfig.getPort() + "]: get access-token success.");
            linkUser = SastLinkServiceAdapter.sastLinkService.user(accessToken.getAccessToken());
            System.out.println("[sast-link client-server:" + sastLinkConfig.getPort() + "]: get link-user-info success.");
        } catch (SastLinkException sastLinkException) {
            System.err.println("[sast-link client-server:" + sastLinkConfig.getPort() + "]: sast-link error Msg:" + sastLinkException.getMessage());
            throw sastLinkException;
        }
        if (linkIdPlayers.get(linkUser.getUserId()) != null) {
            System.out.println("[sast-link client-server:" + sastLinkConfig.getPort() + "]: user try to bind: sast-link-id" + linkUser.getUserId());
            AuthBind(linkUser);
        } else {
            System.out.println("[sast-link client-server:" + sastLinkConfig.getPort() + "]: user try to login: sast-link-id" + linkUser.getUserId());
            User user = UserAdapter.getUser(linkUser.getUserId());
            AuthLogin(user);
        }
    }

    public static void AuthLogin(User user) {
        if (user != null) {
            List<User.Player> players = user.getPlayers();
            for (User.Player player : players) {
                ServerPlayerEntity serverPlayer = uuidPlayers.get(player.getUuid());
                if (serverPlayer != null) {
                    serverPlayer.sendMessage(Text.literal("§bLogin..."), false);
                    if (player.isLogin()) {
                        serverPlayer.sendMessage(Text.literal("§cUser:" + user.getName() + "&" + player.getName() + " already login."), false);
                    } else {
                        player.setLogin(true);
                        if (!serverPlayer.isCreative()) serverPlayer.setInvulnerable(false);
                        serverPlayer.sendMessage(Text.literal("§aLogin success."), false);
                    }
                    uuidPlayers.remove(player.getUuid());
                    break;
                }
            }
            UserAdapter.updateUser(user);
        }
    }

    public static void AuthBind(sast.sastlink.sdk.model.response.data.User linkUser) {
        ServerPlayerEntity serverPlayer = linkIdPlayers.get(linkUser.getUserId());
        if (serverPlayer != null) {
            serverPlayer.sendMessage(Text.literal("§bBinding..."), false);
            User user = UserAdapter.getUser(linkUser.getUserId());
            if (user == null) {
                User.Player p = new User.Player(serverPlayer.getUuidAsString(), serverPlayer.getName().getString(), null, true);
                user = new User(Arrays.asList(p), linkUser.getUserId(), linkUser.getNickname(), linkUser.getEmail());
                UserAdapter.addUser(user);
                if (!serverPlayer.isCreative()) serverPlayer.setInvulnerable(false);
                serverPlayer.sendMessage(Text.literal("§aBind success, account " + user.getName() + " create."), false);
            } else if (!user.getUuids().contains(serverPlayer.getUuidAsString())) {
                User.Player p = new User.Player(serverPlayer.getUuidAsString(), serverPlayer.getName().getString(), null, true);
                user.getPlayers().add(p);
                UserAdapter.updateUser(user);
                if (!serverPlayer.isCreative()) serverPlayer.setInvulnerable(false);
                serverPlayer.sendMessage(Text.literal("§aBind success."), false);
            } else {
                serverPlayer.sendMessage(Text.literal("§cBind account already exist, please login."), false);
            }
            linkIdPlayers.remove(linkUser.getUserId());
        }
    }

    public static boolean bindAccount(String uuid, String name, String email, String password) {
        AccessToken accessToken = checkSastLinkAccount(email, password);
        sast.sastlink.sdk.model.response.data.User linkUser;
        try {
            linkUser = sastLinkService.user(accessToken.getAccessToken());
        } catch (SastLinkException sastLinkException) {
            return false;
        }
        User user = UserAdapter.getUser(linkUser.getUserId());
        User.Player p = new User.Player(uuid, name, null, false);
        if (user == null) {
            user = new User(Arrays.asList(p), linkUser.getUserId(), linkUser.getNickname(), email);
            UserAdapter.addUser(user);
        } else {
            List<User.Player> players = user.getPlayers();
            if (players.stream().noneMatch(player -> player.getUuid().equals(uuid))) players.add(p);
            UserAdapter.updateUser(user);
        }
        return true;
    }

    public static boolean unBindAccount(String uuid, String linkId) {
        UserAdapter.removePlayer(uuid, linkId);
        return true;
    }

    public static boolean deleteAccount(String email, String password) {
        AccessToken accessToken = checkSastLinkAccount(email, password);
        sast.sastlink.sdk.model.response.data.User linkUser;
        try {
            linkUser = sastLinkService.user(accessToken.getAccessToken());
        } catch (SastLinkException sastLinkException) {
            return false;
        }
        UserAdapter.removeUser(linkUser.getUserId());
        return true;
    }

    public static List<User> getLinkAccounts(String uuid) {
        return UserAdapter.getUsersByUuid(uuid);
    }

    public static List<User.Player> getMCAccounts(String uuid) {
        //TODO just return the login account
        return UserAdapter.getUsersByUuid(uuid).stream()
                .flatMap(user -> user.getPlayers().stream())
                .collect(Collectors.toList());
    }

    private static AccessToken checkSastLinkAccount(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        try {
            Token token = sastLinkService.login(email, password);
            String code = sastLinkService.authorize(token.getLoginToken(), "YillThSRrGTj6mXqFfDPinX7G35qEQ1QEyWV6PDSEuc%3D", "S256");
            return sastLinkService.accessToken(code);
        } catch (SastLinkException sastLinkException) {
            return null;
        }
    }

}
