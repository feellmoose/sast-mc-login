package fun.sast.sastlogin.model;

import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static fun.sast.sastlogin.config.Config.PlAYERS;
import static fun.sast.sastlogin.config.Config.gson;

public class PlayerAdapter {
    private static final ConcurrentHashMap<String, Player> map = new ConcurrentHashMap<>();
    private static List<Player> players;
    private static final Type type = new TypeToken<ArrayList<Player>>() {
    }.getType();

    private static void read() {
        try (BufferedReader bufferedReader = Files.newReader(PlAYERS, StandardCharsets.UTF_8)) {
            players = gson.fromJson(bufferedReader, type);
            map.clear();
            if(players != null){
                map.putAll(players.stream().collect(Collectors.toMap(Player::getUuid, player -> player)));
            }else {
                players = new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write() {
        try (BufferedWriter bufferedWriter = Files.newWriter(PlAYERS, StandardCharsets.UTF_8)) {
            players.clear();
            players.addAll(map.values().stream().toList());
            bufferedWriter.write(gson.toJson(players));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        read();
    }

    public static void addPlayer(Player player) {
        map.put(player.getUuid(), player);
        write();
    }

    public static void removePlayer(String uuid) {
        map.remove(uuid);
        write();
    }

    public static Player getPlayer(String uuid) {
        return map.get(uuid);
    }

    public static Player getPlayerByLinkId(String linkId){
        return map.values().stream()
                .filter(player -> linkId.equals(player.getLinkId()))
                .findAny()
                .orElse(null);
    }

    public static void updatePlayer(Player player) {
        Player local = map.get(player.getUuid());
        local.setLinkId(player.getLinkId());
        local.setName(player.getName());
        local.setBan(player.isBan());
        local.setLogin(player.isLogin());
        write();
    }


}
