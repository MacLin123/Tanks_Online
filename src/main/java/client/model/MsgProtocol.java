package client.model;

import config.Config.*;

public class MsgProtocol {
    public String connectPacket(int x, int y) {
        return String.format(typesClientMsg.CONNECT.getType() + "%s,%s", x, y);
    }

    public String updatePacket(int x, int y, int id, int dir) {
        return String.format(typesClientMsg.UPDATE.getType() + "%s,%s-%s|%s"
                , x, y, dir, id);
    }

    public String shotPacket(int id) {
        return typesClientMsg.SHOT.getType() + id;
    }

    public String removeClientPacket(int id) {
        return typesClientMsg.REMOVE.getType() + id;
    }

    public String exitMessagePacket(int id) {
        return typesClientMsg.EXIT.getType() + id;
    }
}
