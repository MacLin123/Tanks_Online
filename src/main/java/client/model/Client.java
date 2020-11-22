package client.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import config.Config;
import config.Config.*;

public class Client implements IClient {
    private int score = 0;
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

    private void initConnection(String ip, int port) throws IOException {
        this.serverPort = port;
        this.Ip = ip;
        cs = new Socket(ip, port);
        dos = new DataOutputStream(cs.getOutputStream());
        dis = new DataInputStream(cs.getInputStream());
    }

    private void handlePermitConnection(String receivedMsg) throws IOException {
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
    }

    @Override
    public void connectAndLogin(String ip, int port, int posX, int posY, String username, String password) throws IOException {
        initConnection(ip, port);

        sendToServer(new MsgProtocol().connectJsonPacket(posX, posY, username, password));

        String receivedMsg;
        receivedMsg = dis.readUTF();
        handlePermitConnection(receivedMsg);

        JsonObject msgJson = null;
        String typeMsg = "";

        receivedMsg = dis.readUTF();

        try {
            msgJson = gson.fromJson(receivedMsg, JsonObject.class);
            typeMsg = msgJson.getAsJsonPrimitive("type").getAsString();
        } catch (JsonSyntaxException exception) {
            System.out.println("syntax exception: " + exception.getMessage());
        }

        if (typeMsg.equals(typesServerMsg.LOGIN.getType())) {
            String info = msgJson.getAsJsonPrimitive("info").getAsString();
            boolean status = msgJson.getAsJsonPrimitive("success").getAsBoolean();
            if (!status) {
                throw new IllegalStateException(info);
            }
        }
    }
    public boolean register(String ip, int port, String username, String password) throws IOException {
        initConnection(ip, port);

        sendToServer(msgProtocol.registerPacket(username, password));

        String receivedMsg;

        receivedMsg = dis.readUTF();
        handlePermitConnection(receivedMsg);

        JsonObject msgJson = null;
        String typeMsg = "";
        receivedMsg = dis.readUTF(); //reg packet
        try {
            msgJson = gson.fromJson(receivedMsg, JsonObject.class);
            typeMsg = msgJson.getAsJsonPrimitive("type").getAsString();
        } catch (JsonSyntaxException exception) {
            System.out.println("syntax exception: " + exception.getMessage());
        }

        if (typeMsg.equals(Config.typesServerMsg.REGISTER.getType())) {
            System.out.println(msgJson.getAsJsonPrimitive("info").getAsString());
            return msgJson.getAsJsonPrimitive("success").getAsBoolean();
        }
        return false;
    }

    @Override
    public boolean login(String ip, int port, String username, String password) throws IOException {

        initConnection(ip, port);


        sendToServer(msgProtocol.logInPacket(username, password));

        String receivedMsg;
        receivedMsg = dis.readUTF();
        handlePermitConnection(receivedMsg);

        JsonObject msgJson = null;
        String typeMsg = "";

        receivedMsg = dis.readUTF(); //reg packet

        try {
            msgJson = gson.fromJson(receivedMsg, JsonObject.class);
            typeMsg = msgJson.getAsJsonPrimitive("type").getAsString();
        } catch (JsonSyntaxException exception) {
            System.out.println("syntax exception: " + exception.getMessage());
        }

        if (typeMsg.equals(typesServerMsg.LOGIN.getType())) {
            System.out.println(msgJson.getAsJsonPrimitive("info").getAsString());
            return msgJson.getAsJsonPrimitive("success").getAsBoolean();
        }
        return false;
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