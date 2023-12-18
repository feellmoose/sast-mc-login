package fun.sast.sastlogin.sastlink;

import fun.sast.sastlogin.model.User;
import fun.sast.sastlogin.model.UserAdapter;
import sast.sastlink.sdk.exception.SastLinkException;
import sast.sastlink.sdk.model.response.data.AccessToken;
import sast.sastlink.sdk.test.TestSastLinkServiceAdapter;
import sast.sastlink.sdk.test.data.Token;

import java.util.Arrays;
import java.util.List;
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

    public static boolean bindAccount(String uuid, String name, String email, String password) {
        AccessToken accessToken = checkSastLinkAccount(email,password);
        sast.sastlink.sdk.model.response.data.User linkUser;
        try {
            linkUser = sastLinkService.user(accessToken.getAccessToken());
        }catch (SastLinkException sastLinkException){
            return false;
        }
        User user = UserAdapter.getUser(linkUser.getUserId());
        User.Player p = new User.Player(uuid,name,null,false);
        if(user == null){
            user = new User(Arrays.asList(p),linkUser.getUserId(),linkUser.getNickname(),email);
            UserAdapter.addUser(user);
        }else {
            List<User.Player> players = user.getPlayers();
            if(players.stream().noneMatch(player -> player.getUuid().equals(uuid))) players.add(p);
            UserAdapter.updateUser(user);
        }
        return true;
    }

    public static boolean unBindAccount(String uuid, String linkId){
        UserAdapter.removePlayer(uuid, linkId);
        return true;
    }

    public static boolean deleteAccount(String email, String password){
        AccessToken accessToken = checkSastLinkAccount(email,password);
        sast.sastlink.sdk.model.response.data.User linkUser;
        try {
            linkUser = sastLinkService.user(accessToken.getAccessToken());
        }catch (SastLinkException sastLinkException){
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
