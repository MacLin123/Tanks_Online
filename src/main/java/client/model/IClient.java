package client.model;

import java.io.IOException;
import java.net.Socket;

public interface IClient {
    void connectAndLogin(String Ip, int port, int posX, int posY,String username,String password) throws IOException;

    void sendToServer(String msg);

    Socket getSocket();

    boolean register(String Ip, int port, String username, String password) throws IOException;
    boolean login(String Ip, int port, String username, String password) throws IOException;
}
