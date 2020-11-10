package server;

import config.Config.*;

public class MsgProtocol {
    public String getIDPacket(int id) {
        return typesServerMsg.ID.getType() + id;
    }

    public String getNewClientPacket(int x, int y, int direction, int id) {
        return String.format(typesServerMsg.NEWCLIENT.getType()
                + "%s,%s-%s|%s", x, y, direction, id);
    }

    public String getRefuseConnectionPacket(String cause) {
        return String.format(typesServerMsg.REFUSE_CONNECT.getType() + " - %s", cause);
    }
}
