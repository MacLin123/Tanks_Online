package server.model;

import java.io.DataOutputStream;
import java.io.IOException;

public interface IServerHandler extends Runnable {
    void sendToClient(String msg);

    void broadcastMsg(String msg) throws IOException;

    void sendAllClientsToSoc(DataOutputStream dos);

    void stopServer();

}
