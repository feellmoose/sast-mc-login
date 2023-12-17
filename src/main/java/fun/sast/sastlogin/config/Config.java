package fun.sast.sastlogin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public interface Config {
    File PlAYERS = new File("config/player-state.json");
    File SAST_LINK = new File("config/sast-link.json");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
}
