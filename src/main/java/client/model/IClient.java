package client.model;
import java.io.IOException;
import java.net.Socket;

public interface IClient {
    void connect(String Ip, int port, int posX, int posY) throws IOException;

    void sendToServer(String msg);

    Socket getSocket();
}
