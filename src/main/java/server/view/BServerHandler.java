package server.view;

import server.Server;

import java.net.Socket;
import java.util.List;

public class BServerHandler {
    public static IServerHandler build(Socket clientSoc, boolean isRunning, List<Server.ClientData> clientDataList) {
        return new ServerHandler(clientSoc, isRunning, clientDataList);
    }
}
