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

public class UserAdapter {
    private static final ConcurrentHashMap<String, User> map = new ConcurrentHashMap<>();
    private static List<User> users;
    private static final Type type = new TypeToken<ArrayList<User>>() {
    }.getType();

    private static void read() {
        try (BufferedReader bufferedReader = Files.newReader(PlAYERS, StandardCharsets.UTF_8)) {
            users = gson.fromJson(bufferedReader, type);
            map.clear();
            if(users != null){
                map.putAll(users.stream().collect(Collectors.toMap(User::getLinkId, player -> player)));
            }else {
                users = new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write() {
        try (BufferedWriter bufferedWriter = Files.newWriter(PlAYERS, StandardCharsets.UTF_8)) {
            users.clear();
            users.addAll(map.values().stream().toList());
            bufferedWriter.write(gson.toJson(users));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        read();
    }

    public static void addUser(User user) {
        map.put(user.getLinkId(), user);
        write();
    }

    public static void removeUser(String linkId) {
        map.remove(linkId);
        write();
    }

    public static void removePlayer(String uuid,String linkId) {
        User u = map.get(linkId);
        User.Player p = u.getPlayers().stream()
                .filter(user -> user.getUuid().equals(uuid))
                .findAny().orElse(null);
        if(p != null) {
            u.getPlayers().remove(p);
            write();
        }
    }

    public static User getUser(String linkId) {
        return map.get(linkId);
    }

    public static boolean anyLogin(String uuid){
        return map.values().stream()
                .filter(player -> player.getUuids().contains(uuid))
                .flatMap(user -> user.getPlayers().stream())
                .anyMatch(User.Player::isLogin);
    }

    public static List<User> getUsersByUuid(String uuid){
        return map.values().stream()
                .filter(user -> user.getUuids().contains(uuid))
                .collect(Collectors.toList());
    }

    public static void logout(String uuid){
        map.values().stream()
                .flatMap(user -> user.getPlayers().stream())
                .filter(player -> player.getUuid().equals(uuid))
                .forEach(player -> player.setLogin(false));
    }

    public static void updateUser(User user) {
        User local = map.get(user.getLinkId());
        local.setLinkId(user.getLinkId());
        local.setEmail(user.getEmail());
        local.setName(user.getName());
        local.setPlayers(user.getPlayers());
        write();
    }


}
