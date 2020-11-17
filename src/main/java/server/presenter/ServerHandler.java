package server.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import config.Config;
import server.model.MsgProtocol;
import server.model.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ServerHandler implements IServerHandler {
    public Socket clientSoc;
    public boolean isRunning;
    private DataInputStream clientDis;
    private DataOutputStream clientDos;
    private List<Server.ClientData> clientDataList;
    private MsgProtocol msgProtocol;
    private Gson gson = new Gson();

    public ServerHandler(Socket clientSoc, boolean isRunning, List<Server.ClientData> clientDataList) {
        this.clientSoc = clientSoc;
        this.isRunning = isRunning;
        this.clientDataList = clientDataList;
        msgProtocol = new MsgProtocol();
        try {
            clientDis = new DataInputStream(clientSoc.getInputStream());
            clientDos = new DataOutputStream(clientSoc.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        String msgStr = null; //from client
        while (isRunning) {
            try {
                msgStr = clientDis.readUTF();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                break;
            }
            System.out.println(msgStr);
            JsonObject msgJson;
            String typeMsg = "";
            try {
                msgJson = gson.fromJson(msgStr, JsonObject.class);
                typeMsg = msgJson.getAsJsonPrimitive("type").getAsString();
            } catch (JsonSyntaxException exception) {
                System.out.println("syntax exception: " + exception.getMessage());
            }
            if (typeMsg.equals(Config.typesClientMsg.CONNECT.getType())) {
                handleConnectPacket(msgStr);
            } else if (typeMsg.equals(Config.typesClientMsg.UPDATE.getType())) {
                handleUpdatePacket(msgStr);
            } else if (typeMsg.equals(Config.typesClientMsg.SHOT.getType())) {
                handleShotPacket(msgStr);
            } else if (typeMsg.equals(Config.typesClientMsg.REMOVE.getType())) {
                handleRemovePacket(msgStr);
            } else if (typeMsg.equals(Config.typesClientMsg.EXIT.getType())) {
                handleExitPacket(msgStr);
                break;
            }
        }
        try { //stop client thread
            clientDis.close();
            clientDos.close();
            clientSoc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleExitPacket(String msgStr) {
        JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
        int id = msgJson.getAsJsonPrimitive("id").getAsInt();

        try {
            broadcastMsg(msgStr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (clientDataList.get(id) != null)
            clientDataList.set(id, null);
    }

    private void handleRemovePacket(String msgStr) {
        JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
        int id = msgJson.getAsJsonPrimitive("id").getAsInt();

        try {
            broadcastMsg(msgStr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        clientDataList.set(id, null);
    }

    private void handleShotPacket(String msgStr) {
        try {
            broadcastMsg(msgStr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handleUpdatePacket(String msgStr) {
        JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
        int x = msgJson.getAsJsonPrimitive("x").getAsInt();
        int y = msgJson.getAsJsonPrimitive("y").getAsInt();
        int id = msgJson.getAsJsonPrimitive("id").getAsInt();
        int dir = msgJson.getAsJsonPrimitive("dir").getAsInt();

        if (clientDataList.get(id) != null) {
            clientDataList.get(id).setPosX(x);
            clientDataList.get(id).setPosY(y);
            clientDataList.get(id).setDirection(dir);
            try {
                broadcastMsg(msgStr);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    private void handleConnectPacket(String msgStr) {
        JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
        int x = msgJson.getAsJsonPrimitive("x").getAsInt();
        int y = msgJson.getAsJsonPrimitive("y").getAsInt();

        try {
            int id = clientDataList.indexOf(null);
            System.out.println("new client id = " + id);
            sendToClient(msgProtocol.getIDJsonPacket(id));
            broadcastMsg(msgProtocol.getNewClientJsonPacket(x, y, 1, id));
            sendAllClientsToSoc(clientDos);
            clientDataList.set(id, new Server.ClientData(clientDos, x, y, 1));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void sendToClient(String msg) {
        if (msg.equals("exit"))
            System.exit(0);
        else {
            try {
                clientDos.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void broadcastMsg(String msg) throws IOException {
        for (int i = 0; i < clientDataList.size(); i++) {
            if (clientDataList.get(i) != null) {
                clientDataList.get(i).getWriterStream().writeUTF(msg);
            }
        }
    }

    @Override
    public void sendAllClientsToSoc(DataOutputStream dos) {
        int x, y, dir;
        for (int i = 0; i < clientDataList.size(); i++) {
            if (clientDataList.get(i) != null) {
                x = clientDataList.get(i).getX();
                y = clientDataList.get(i).getY();
                dir = clientDataList.get(i).getDir();
                try {
                    dos.writeUTF(msgProtocol.getNewClientJsonPacket(x, y, dir, i));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
