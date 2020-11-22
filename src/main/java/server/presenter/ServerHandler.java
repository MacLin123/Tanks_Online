package server.presenter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import config.Config;
import server.model.JsonUtils;
import server.model.MsgProtocol;
import server.model.Server;
import server.model.login.LoginUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ServerHandler implements IServerHandler {
    public Socket clientSoc;
    public boolean isRunning;
    private DataInputStream clientDis;
    private DataOutputStream clientDos;
    private List<Server.ClientData> clientDataList;
    private String username;
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
                loadLeaderBoard();
//                handleAddScorePacket(msgStr,false);
            } else if (typeMsg.equals(Config.typesClientMsg.UPDATE.getType())) {
                handleUpdatePacket(msgStr);
            } else if (typeMsg.equals(Config.typesClientMsg.SHOT.getType())) {
                handleShotPacket(msgStr);
            } else if (typeMsg.equals(Config.typesClientMsg.REMOVE.getType())) {
                handleRemovePacket(msgStr);
            } else if (typeMsg.equals(Config.typesClientMsg.EXIT.getType())) {
                handleExitPacket(msgStr);
                break;
            } else if (typeMsg.equals(Config.typesClientMsg.ADDSCORE.getType())) {
                handleAddScorePacket(msgStr);
            } else if (typeMsg.equals(Config.typesClientMsg.REGISTER.getType())) {
                handleRegisterPacket(msgStr);
            }
//            else if (typeMsg.equals(Config.typesClientMsg.LOGIN.getType())) {
//                handleLoginPacket(msgStr);
//            }
        }
        try { //stop client thread
            clientDis.close();
            clientDos.close();
            clientSoc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void handleLoginPacket(String msgStr) {
//        JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
//        String username = msgJson.getAsJsonPrimitive("username").getAsString();
//        String password = msgJson.getAsJsonPrimitive("password").getAsString();
//        if (LoginUtils.login(username, password)) {
//            sendToClient(new MsgProtocol().loginResponsePacket(true, "login was successful"));
//
//        } else {
//            sendToClient(new MsgProtocol().loginResponsePacket(false, "wrong username or password"));
//        }
//    }

    private void handleRegisterPacket(String msgStr) {
        JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
        String username = msgJson.getAsJsonPrimitive("username").getAsString();
        String password = msgJson.getAsJsonPrimitive("password").getAsString();
        if (LoginUtils.register(username, password)) {
            sendToClient(new MsgProtocol().registerResponsePacket(true, "register was successful"));
        } else {
            sendToClient(new MsgProtocol().registerResponsePacket(false, "username already exists"));
        }
    }

    private void handleAddScorePacket(String msgStr) {
        JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
        int id = msgJson.getAsJsonPrimitive("id").getAsInt();
        int score = clientDataList.get(id).addAndGetScore(1);
        //thread
        new Thread(() -> {
            String leadersJson = handleScoreJson(score);
            try {
                broadcastMsg(new MsgProtocol().leadersUpdatePacket(leadersJson));
                sendToClient(new MsgProtocol().getScoreAddedPacket(score));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }

    private void loadLeaderBoard() {
        Gson gson = new Gson();
        Reader reader = null;
        try {
            reader = new FileReader(Config.USERS_FILE);
        } catch (FileNotFoundException e) {
            System.out.println(e.getCause());
        }

        JsonArray users = gson.fromJson(reader, JsonArray.class);

        List<LoginUtils.UserForAchievements> leaders = getLeadersList(users);
//        sendToClient(new MsgProtocol().getScoreAddedPacket(0, gson.toJson(leaders)));
        try {
//            broadcastMsg(new MsgProtocol().getScoreAddedPacket(0, gson.toJson(leaders)));
            broadcastMsg(new MsgProtocol().leadersUpdatePacket(gson.toJson(leaders)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String handleScoreJson(int score) {
        Gson gson = new Gson();
        Reader reader = null;
        try {
            reader = new FileReader(Config.USERS_FILE);
        } catch (FileNotFoundException e) {
            System.out.println(e.getCause());
        }

        JsonArray users = gson.fromJson(reader, JsonArray.class);
        boolean needToWrite = false;
        for (int i = 0; i < users.size(); i++) {
            JsonObject curUser = users.get(i).getAsJsonObject();
            if (curUser.getAsJsonPrimitive("username").getAsString().equals(username) &&
                    curUser.getAsJsonPrimitive("score").getAsInt() < score) {
                curUser.addProperty("score", score);
                needToWrite = true;
                break;
            }
        }
        if (needToWrite) {
            JsonUtils.writeJsonElementToFile(users);
        }
        List<LoginUtils.UserForAchievements> leaders = getLeadersList(users);
        return gson.toJson(leaders);
    }

    private List<LoginUtils.UserForAchievements> getLeadersList(JsonArray users) {
        Type listType = new TypeToken<List<LoginUtils.UserForAchievements>>() {
        }.getType();
        List<LoginUtils.UserForAchievements> userList = new Gson().fromJson(users.toString(), listType);
        Collections.sort(userList, new Comparator<LoginUtils.UserForAchievements>() {
            @Override
            public int compare(LoginUtils.UserForAchievements o1, LoginUtils.UserForAchievements o2) {
                return Integer.compare(o2.getScore(), o1.getScore());
            }
        });
        List<LoginUtils.UserForAchievements> leaders = new ArrayList<>();
        for (int i = 0; i < Math.min(userList.size(), Config.AMOUNT_LEADERS); i++) {
            leaders.add(userList.get(i));
        }
        return leaders;
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
        String password = msgJson.getAsJsonPrimitive("password").getAsString();
        username = msgJson.getAsJsonPrimitive("username").getAsString();
        //check login;
        for (Server.ClientData elem : clientDataList) {
            if (elem != null && elem.getUsername().equals(username)) {
                sendToClient(new MsgProtocol().loginResponsePacket(false, "user with this username in game"));
                return;
            }
        }
        if (LoginUtils.login(username, password)) {
            sendToClient(new MsgProtocol().loginResponsePacket(true, "login was successful"));

        } else {
            sendToClient(new MsgProtocol().loginResponsePacket(false, "wrong username or password"));
            return;
        }
        try {
            int id = clientDataList.indexOf(null);
            System.out.println("new client id = " + id);
            sendToClient(msgProtocol.getIDJsonPacket(id));
            broadcastMsg(msgProtocol.getNewClientJsonPacket(x, y, 1, id));
            sendAllClientsToSoc(clientDos);
            clientDataList.set(id, new Server.ClientData(clientDos, x, y, 1, username));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

    }
//    private void handleConnectPacket(String msgStr) {
//        JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
//        int x = msgJson.getAsJsonPrimitive("x").getAsInt();
//        int y = msgJson.getAsJsonPrimitive("y").getAsInt();
//
//        try {
//            int id = clientDataList.indexOf(null);
//            System.out.println("new client id = " + id);
//            sendToClient(msgProtocol.getIDJsonPacket(id));
//            broadcastMsg(msgProtocol.getNewClientJsonPacket(x, y, 1, id));
//            sendAllClientsToSoc(clientDos);
//            clientDataList.set(id, new Server.ClientData(clientDos, x, y, 1,"1"));
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        } catch (IllegalStateException e) {
//            System.out.println(e.getMessage());
//        }
//
//    }

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
