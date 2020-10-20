package client.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import config.Config.*;

public class Client {
    private Socket cs;
    private String Ip;
    private int serverPort;
    private DataInputStream dis;
    private DataOutputStream dos;
    private MsgProtocol msgProtocol;

    public Client() {
        msgProtocol = new MsgProtocol();
    }

    public void connect(String Ip, int port, int posX, int posY) throws IOException {
        this.serverPort = port;
        this.Ip = Ip;
        cs = new Socket(Ip, port);
        dos = new DataOutputStream(cs.getOutputStream());

        dos.writeUTF(msgProtocol.RegisterPacket(posX, posY));


    }

    public void sendToServer(String msg) {
        if (msg.equals(typesClientMsg.EXIT.getType()))
            System.exit(0);
        else {
            try {
                Socket s = new Socket(Ip, serverPort);
                System.out.println(msg);
                dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(msg);
            } catch (IOException ex) {

            }
        }

    }

    public Socket getSocket() {
        return cs;
    }

    public String getIP() {
        return Ip;
    }

    public void closeAll() {
        try {
            dis.close();
            dos.close();
            cs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}