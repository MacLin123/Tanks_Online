package client.model;

import config.Config.*;

public class MsgProtocol {
    public String RegisterPacket(int x, int y) {
        return String.format(typesClientMsg.CONNECT.getType() + "%s,%s", x, y);
    }

    public String UpdatePacket(int x, int y, int id, int dir) {
        return String.format(typesClientMsg.UPDATE.getType() + "%s,%s-%s|%s"
                , x, y, dir, id);
    }

    public String ShotPacket(int id) {
        return typesClientMsg.SHOT.getType() + id;
    }

    public String RemoveClientPacket(int id) {
        return typesClientMsg.REMOVE.getType() + id;
    }

    public String ExitMessagePacket(int id) {
        return typesClientMsg.EXIT.getType() + id;
    }
}
