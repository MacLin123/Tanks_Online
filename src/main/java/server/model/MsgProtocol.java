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
}
