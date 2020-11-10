package server.view;

import config.Config;
import server.MsgProtocol;
import server.Server;

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
                e.printStackTrace();
            }
            System.out.println(msgStr);
            if (msgStr.startsWith(Config.typesClientMsg.CONNECT.getType())) { // connect packet
                handleConnectPacket(msgStr);
            } else if (msgStr.startsWith(Config.typesClientMsg.UPDATE.getType())) {
                handleUpdatePacket(msgStr);
            } else if (msgStr.startsWith(Config.typesClientMsg.SHOT.getType())) {
                handleShotPacket(msgStr);
            } else if (msgStr.startsWith(Config.typesClientMsg.REMOVE.getType())) {
                handleRemovePacket(msgStr);
            } else if (msgStr.startsWith(Config.typesClientMsg.EXIT.getType())) {
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
        int id = Integer.parseInt(msgStr.substring(4));

        try {
            broadcastMsg(msgStr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (clientDataList.get(id) != null)
            clientDataList.set(id, null);
    }

    private void handleRemovePacket(String msgStr) {
        int id = Integer.parseInt(msgStr.substring(6));

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
        int pos1 = msgStr.indexOf(',');
        int pos2 = msgStr.indexOf('-');
        int pos3 = msgStr.indexOf('|');
        int x = Integer.parseInt(msgStr.substring(6, pos1));
        int y = Integer.parseInt(msgStr.substring(pos1 + 1, pos2));
        int dir = Integer.parseInt(msgStr.substring(pos2 + 1, pos3));
        int id = Integer.parseInt(msgStr.substring(pos3 + 1, msgStr.length()));
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

//    private int getIndEmptySlot() {
//        int slot = -1;
//        for (int i = 0; i < clientDataList.size(); i++) {
//            if (clientDataList.get(i) == null) {
//                slot = i;
//                break;
//            }
//        }
//        if (slot == -1) {
//            throw new IllegalStateException("Server has max amount of players");
//        }
//        return slot;
//    }

    private void handleConnectPacket(String msgStr) {
        int pos = msgStr.indexOf(',');
        int x = Integer.parseInt(msgStr.substring(
                Config.typesClientMsg.CONNECT.getType().length(), pos));
        int y = Integer.parseInt(msgStr.substring(pos + 1));

//        try {
//            clientDos = new DataOutputStream(clientSoc.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            int id = clientDataList.indexOf(null);
//            int id = clientDataList.size() + 1;
            System.out.println("new client id = " + id);
            sendToClient(msgProtocol.getIDPacket(id));
            broadcastMsg(msgProtocol.getNewClientPacket(x, y, 1, id));
            sendAllClientsToSoc(clientDos);
            clientDataList.set(id, new Server.ClientData(clientDos, x, y, 1));
//            clientDataList.add(new Server.ClientData(clientDos, x, y, 1));
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
                    dos.writeUTF(msgProtocol.getNewClientPacket(x, y, dir, i));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void stopServer() {
        isRunning = false;
    }

//    public class ClientData {
//        DataOutputStream dos;
//        int posX, posY, direction;
//
//        public ClientData(DataOutputStream writer, int posX, int posY, int direction) {
//            this.dos = writer;
//            this.posX = posX;
//            this.posY = posY;
//            this.direction = direction;
//        }
//
//        public void setPosX(int x) {
//            posX = x;
//        }
//
//        public void setPosY(int y) {
//            posY = y;
//        }
//
//        public void setDirection(int dir) {
//            direction = dir;
//        }
//
//        public DataOutputStream getWriterStream() {
//            return dos;
//        }
//
//        public int getX() {
//            return posX;
//        }
//
//        public int getY() {
//            return posY;
//        }
//
//        public int getDir() {
//            return direction;
//        }
//    }
}
