package fun.sast.sastlogin.sastlink;

import fun.sast.sastlogin.model.Player;
import fun.sast.sastlogin.model.PlayerAdapter;

public class UserBinder {
    public static boolean bindLinkId(String uuid, String linkId, String name) {
        Player p = PlayerAdapter.getPlayerByLinkId(linkId);
        if (p == null) {
            PlayerAdapter.addPlayer(new Player(uuid, linkId, name, false, false));
            return true;
        }
        return false;
    }
}
