package server.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import config.Config;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class JsonUtils {
    public static void writeJsonElementToFile(JsonElement obj) {
        try (Writer writer = new FileWriter(Config.USERS_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(obj, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
