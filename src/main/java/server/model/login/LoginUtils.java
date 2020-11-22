package server.model.login;

import com.google.gson.*;
import config.Config;
import server.model.JsonUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoginUtils {
    public static class UserForLogin {
        String password;
        String username;
        int score = 0;

        public int getScore() {
            return score;
        }

        UserForLogin(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
    public static class UserForAchievements {
        String username;
        int score = 0;

        public int getScore() {
            return score;
        }

        UserForAchievements(String username) {
            this.username = username;
        }
    }

    public static boolean register(String username, String password) {
        Gson gson = new Gson();

        String userJson = gson.toJson(new UserForLogin(username,password));
        JsonObject user = gson.fromJson(userJson, JsonObject.class);

        if (Files.exists(Paths.get(Config.USERS_FILE))) {
            Reader reader = null;
            try {
                reader = new FileReader(Config.USERS_FILE);
            } catch (FileNotFoundException e) {
                System.out.println(e.getCause());
            }

//            JsonArray users =  gson.fromJson(reader,JsonArray.class);
            JsonArray users = gson.fromJson(reader, JsonArray.class);
            for (int i = 0; i < users.size(); i++) {
                JsonObject curUser = users.get(i).getAsJsonObject();
                if(curUser.getAsJsonPrimitive("username").getAsString().equals(username)){
                    return false;
                }
            }
            users.add(user);
//            if (users.has(username)) {
//                return false;
//            } else {
//                users.add(username, user);
//            }
            JsonUtils.writeJsonElementToFile(users);

        } else {
            JsonArray users = new JsonArray();
            users.add(user);
            JsonUtils.writeJsonElementToFile(users);
        }
        return true;
    }

    public static boolean login(String username, String password) {
        Gson gson = new Gson();

        if (Files.exists(Paths.get(Config.USERS_FILE))) {
            Reader reader = null;
            try {
                reader = new FileReader(Config.USERS_FILE);
            } catch (FileNotFoundException e) {
                System.out.println(e.getCause());
            }

            JsonArray users = gson.fromJson(reader, JsonArray.class);

            for (int i = 0; i < users.size(); i++) {
                JsonObject curUser = users.get(i).getAsJsonObject();
                if(curUser.getAsJsonPrimitive("username").getAsString().equals(username) &&
                curUser.getAsJsonPrimitive("password").getAsString().equals(password)){
                    System.out.println("user has logged in");
                    return true;
                }
            }
//            if (users.has(username)) {
//                JsonObject curUser = users.getAsJsonObject(username);
//                if (curUser.getAsJsonPrimitive("password").getAsString().equals(password)) {
//                    System.out.println("user has logged in");
//                    return true;
//                } else {
//                    return false;
//                }
//            }

        }
        return false;
    }

}
