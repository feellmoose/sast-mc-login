package fun.sast.sastlogin.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class User {
    private String name;
    private String linkId;
    private List<Player> players;
    private String email;


    public static class Player{
        private String uuid;
        private String name;
        private Boolean ban;
        private Boolean login;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Player() {
        }

        public Boolean isBan() {
            return ban != null||ban;
        }

        public void setBan(Boolean ban) {
            this.ban = ban;
        }

        public Boolean isLogin() {
            return login==null||login;
        }

        public void setLogin(Boolean login) {
            this.login = login;
        }

        public Player(String uuid, String name,Boolean ban ,boolean login) {
            this.uuid = uuid;
            this.name = name;
            this.ban = ban;
            this.login = login;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Player player = (Player) o;
            return Objects.equals(uuid, player.uuid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uuid);
        }
    }

    public User(List<Player> players, String linkId, String name,String email) {
        this.players = players;
        this.linkId = linkId;
        this.name = name;
        this.email = email;
    }

    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(linkId, user.linkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkId);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUuids(){
        return this.getPlayers().stream().map(Player::getUuid).collect(Collectors.toList());
    }

}