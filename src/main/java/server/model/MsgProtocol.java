package server.model;

import com.google.gson.JsonObject;
import config.Config.*;

public class MsgProtocol {

    public String getIDJsonPacket(int id) {
        JsonObject idPack = new JsonObject();
        idPack.addProperty("id", id);
        idPack.addProperty("type", typesServerMsg.ID.getType());
        return idPack.toString();
    }

    public String getNewClientJsonPacket(int x, int y, int dir, int id) {
        JsonObject newClientPack = new JsonObject();
        newClientPack.addProperty("x", x);
        newClientPack.addProperty("y", y);
        newClientPack.addProperty("id", id);
        newClientPack.addProperty("dir", dir);
        newClientPack.addProperty("type", typesServerMsg.NEWCLIENT.getType());
        return newClientPack.toString();
    }

    public String getRefuseConnPacket(String cause) {
        JsonObject refuseConPack = new JsonObject();
        refuseConPack.addProperty("cause", cause);
        refuseConPack.addProperty("type", typesServerMsg.REFUSE_CONNECT.getType());
        return refuseConPack.toString();
    }

    public String getOkConPacket() {
        JsonObject okConPacket = new JsonObject();
        okConPacket.addProperty("type", typesServerMsg.OK_CONNECT.getType());
        return okConPacket.toString();
    }
    public String getScoreAddedPacket(int score) {
        JsonObject scoreAdded = new JsonObject();
        scoreAdded.addProperty("score", score);
        scoreAdded.addProperty("type", typesServerMsg.SCORE_ADDED.getType());
        return scoreAdded.toString();
    }
    public String leadersUpdatePacket(String leadersJson) {
        JsonObject scoreAdded = new JsonObject();
        scoreAdded.addProperty("leadersJson", leadersJson);
        scoreAdded.addProperty("type", typesServerMsg.LEADERS_UPDATE.getType());
        return scoreAdded.toString();
    }
    public String registerResponsePacket(boolean success,String info) {
        JsonObject regPack = new JsonObject();
        regPack.addProperty("success", success);
        regPack.addProperty("info", info);
        regPack.addProperty("type", typesServerMsg.REGISTER.getType());
        return regPack.toString();
    }
    public String loginResponsePacket(boolean success,String info) {
        JsonObject logPack = new JsonObject();
        logPack.addProperty("success", success);
        logPack.addProperty("info", info);
        logPack.addProperty("type", typesServerMsg.LOGIN.getType());
        return logPack.toString();
    }
}
