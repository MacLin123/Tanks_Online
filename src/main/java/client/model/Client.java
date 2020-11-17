package client.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import config.Config.*;

public class Client implements IClient {
    private Socket cs;
    private String Ip;
    private int serverPort;
    private DataInputStream dis;
    private DataOutputStream dos;
    private MsgProtocol msgProtocol;
    private Gson gson = new Gson();

    public Client() {
        msgProtocol = new MsgProtocol();
    }

    @Override
    public void connect(String Ip, int port, int posX, int posY) throws IOException {
        this.serverPort = port;
        this.Ip = Ip;
        cs = new Socket(Ip, port);
        dos = new DataOutputStream(cs.getOutputStream());

        dis = new DataInputStream(cs.getInputStream());
        String receivedMsg = dis.readUTF();

        JsonObject msgJson = null;
        String typeMsg = "";
        try {
            msgJson = gson.fromJson(receivedMsg, JsonObject.class);
            typeMsg = msgJson.getAsJsonPrimitive("type").getAsString();
        } catch (JsonSyntaxException exception) {
            System.out.println("syntax exception: " + exception.getMessage());
        }

        if (typeMsg.equals(typesServerMsg.REFUSE_CONNECT.getType())) {
            String cause = msgJson.getAsJsonPrimitive("cause").getAsString();
            throw new IOException(cause);
        }

        dos.writeUTF(msgProtocol.connectJsonPacket(posX, posY));


    }

    @Override
    public void sendToServer(String msg) {
        JsonObject msgJson;
        String typeMsg = "";
        try {
            msgJson = gson.fromJson(msg, JsonObject.class);
            typeMsg = msgJson.getAsJsonPrimitive("type").getAsString();
        } catch (JsonSyntaxException exception) {
            System.out.println("syntax exception: " + exception.getMessage());
        }

        if (typeMsg.equals(typesClientMsg.EXIT.getType())) {
            try {
                if (dos != null) {
                    dos.writeUTF(msg);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            try {
                System.out.println(msg);
                dos.writeUTF(msg);
            } catch (IOException ex) {

            }
        }

    }

    @Override
    public Socket getSocket() {
        return cs;
    }
}