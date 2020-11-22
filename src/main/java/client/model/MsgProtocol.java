package client.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import config.Config.*;

public class MsgProtocol {
    private Gson gson = new Gson();

    public String connectJsonPacket(int x, int y, String username, String password) {
        JsonObject conPack = new JsonObject();
        conPack.addProperty("x", x);
        conPack.addProperty("y", y);
        conPack.addProperty("username",username);
        conPack.addProperty("password",password);
        conPack.addProperty("type", typesClientMsg.CONNECT.getType());
        return conPack.toString();
    }

    public String updateJsonPacket(int x, int y, int id, int dir) {
        JsonObject updPack = new JsonObject();
        updPack.addProperty("x", x);
        updPack.addProperty("y", y);
        updPack.addProperty("id", id);
        updPack.addProperty("dir", dir);
        updPack.addProperty("type", typesClientMsg.UPDATE.getType());
        return updPack.toString();
    }

    public String shotJsonPacket(int id) {
        JsonObject shotPack = new JsonObject();
        shotPack.addProperty("id", id);
        shotPack.addProperty("type", typesClientMsg.SHOT.getType());
        return shotPack.toString();
    }

    public String removeClientJsonPacket(int id) {
        JsonObject removeClientPack = new JsonObject();
        removeClientPack.addProperty("id", id);
        removeClientPack.addProperty("type", typesClientMsg.REMOVE.getType());
        return removeClientPack.toString();
    }

    public String exitJsonPacket(int id) {
        JsonObject exitPack = new JsonObject();
        exitPack.addProperty("id", id);
        exitPack.addProperty("type", typesClientMsg.EXIT.getType());
        return exitPack.toString();
    }
    public String addScorePacket(int id) {
        JsonObject addScorePack = new JsonObject();
        addScorePack.addProperty("id", id);
        addScorePack.addProperty("type", typesClientMsg.ADDSCORE.getType());
        return addScorePack.toString();
    }
    public String registerPacket(String username, String password) {
        JsonObject addScorePack = new JsonObject();
        addScorePack.addProperty("username", username);
        addScorePack.addProperty("password", password);
        addScorePack.addProperty("type", typesClientMsg.REGISTER.getType());
        return addScorePack.toString();
    }
    public String logInPacket(String username,String password) {
        JsonObject logInPack = new JsonObject();
        logInPack.addProperty("username", username);
        logInPack.addProperty("password", password);
        logInPack.addProperty("type", typesClientMsg.LOGIN.getType());
        return logInPack.toString();
    }
}
