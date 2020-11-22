package client.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import config.Config;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Login {
    private static MsgProtocol msgProtocol = new MsgProtocol();

    public static boolean authenticate(String username, String password) {
        if (username.equals("bob") && password.equals("secret")) {
            return true;
        }
        return false;
    }

//    public static boolean register(String username, String password) {
//        Gson gson = new Gson();
//        IClient iClient = BClient.getInstance();
//        iClient.sendToServer(msgProtocol.registerPacket(username, password));
//        Socket clientSoc = iClient.getSocket();
//        DataInputStream dataInputStream;
//        String msgStr = "";
//        try {
//            dataInputStream = new DataInputStream(clientSoc.getInputStream());
//            msgStr = dataInputStream.readUTF();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        JsonObject msgJson = null;
//        String typeMsg = "";
//        try {
//            msgJson = gson.fromJson(msgStr, JsonObject.class);
//            typeMsg = msgJson.getAsJsonPrimitive("type").getAsString();
//        } catch (JsonSyntaxException exception) {
//            System.out.println("syntax exception: " + exception.getMessage());
//        }
//        if (typeMsg.equals(Config.typesServerMsg.REGISTER.getType())) {
//            System.out.println(msgJson.getAsJsonPrimitive("info").getAsString());
//            return msgJson.getAsJsonPrimitive("success").getAsBoolean();
//        }
//        return false;
//    }
}