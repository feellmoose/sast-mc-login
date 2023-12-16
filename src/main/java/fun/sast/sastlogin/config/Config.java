package fun.sast.sastlogin.config;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resource.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

public interface Config {
    File PlAYERS = new File( "E:\\my_works\\work_space\\JAVA\\SAST\\sast-login\\src\\main\\resources\\player-state.json");
    File SAST_LINK = new File("E:\\my_works\\work_space\\JAVA\\SAST\\sast-login\\src\\main\\resources\\sast-link.json");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
}
