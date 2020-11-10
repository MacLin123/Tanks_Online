package server;

import config.Config;
import config.Config.*;
import server.view.BServerHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private List<ClientData> clientDataList;
    private ServerSocket serverSocket;
    private int serverPort = 10000;

//    private DataInputStream clientDis;
//    private DataOutputStream clientDos;

    private MsgProtocol msgProtocol;
    private boolean isRunning = true;

    public Server() {
        clientDataList = new ArrayList<>(Config.MAX_PLAYERS + 1);
        for (int i = 0; i < Config.MAX_PLAYERS + 1; i++) {
            clientDataList.add(null);
        }
        msgProtocol = new MsgProtocol();
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        Socket clientSoc = null;
        String msgStr = null; //from client
        while (isRunning) {
            try {
                clientSoc = serverSocket.accept();
                System.out.println("client has been connected");

                System.out.println("SIZE = " + clientDataList.size());
                Thread handlerThread = new Thread(BServerHandler.build(clientSoc, isRunning, clientDataList));
                handlerThread.start();
//                clientDis = new DataInputStream(clientSoc.getInputStream());
//                msgStr = clientDis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            System.out.println(msgStr);
//            if (msgStr.startsWith(typesClientMsg.CONNECT.getType())) { // connect packet
//                int pos = msgStr.indexOf(',');
//                int x = Integer.parseInt(msgStr.substring(
//                        typesClientMsg.CONNECT.getType().length(), pos));
//                int y = Integer.parseInt(msgStr.substring(pos + 1));
//
//                try {
//                    clientDos = new DataOutputStream(clientSoc.getOutputStream());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                sendToClient(msgProtocol.getIDPacket(clientDataList.size() + 1));
//                try {
//                    broadcastMsg(msgProtocol.getNewClientPacket(x, y, 1,
//                            clientDataList.size() + 1));
//                    sendAllClientsToSoc(clientDos);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                clientDataList.add(new ClientData(clientDos, x, y, 1));
//            } else if(msgStr.startsWith(Config.typesClientMsg.EXIT.getType())) {
//                int id=Integer.parseInt(msgStr.substring(s));
//
//                try {
//                    broadcastMsg(msgStr);
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//                if(clientDataList.get(id-1)!=null)
//                    clientDataList.set(id-1,null);
//            }
        }

        try { // when stop server
//            clientDis.close();
//            clientDos.close();
            serverSocket.close();
//            clientSoc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    public void sendToClient(String msg) {
//        if (msg.equals("exit"))
//            System.exit(0);
//        else {
//            try {
//                clientDos.writeUTF(msg);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void broadcastMsg(String msg) throws IOException {
//        for (int i = 0; i < clientDataList.size(); i++) {
//            if (clientDataList.get(i) != null) {
//                clientDataList.get(i).getWriterStream().writeUTF(msg);
//            }
//        }
//    }
//
//    public void sendAllClientsToSoc(DataOutputStream dos) {
//        int x, y, dir;
//        for (int i = 0; i < clientDataList.size(); i++) {
//            if (clientDataList.get(i) != null) {
//                x = clientDataList.get(i).getX();
//                y = clientDataList.get(i).getY();
//                dir = clientDataList.get(i).getDir();
//                try {
//                    dos.writeUTF(msgProtocol.getNewClientPacket(x, y, dir, i + 1));
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public void stopServer() {
//        isRunning = false;
//    }
//
    public static class ClientData {
        DataOutputStream dos;
        int posX, posY, direction;

        public ClientData(DataOutputStream writer, int posX, int posY, int direction) {
            this.dos = writer;
            this.posX = posX;
            this.posY = posY;
            this.direction = direction;
        }

        public void setPosX(int x) {
            posX = x;
        }

        public void setPosY(int y) {
            posY = y;
        }

        public void setDirection(int dir) {
            direction = dir;
        }

        public DataOutputStream getWriterStream() {
            return dos;
        }

        public int getX() {
            return posX;
        }

        public int getY() {
            return posY;
        }

        public int getDir() {
            return direction;
        }
    }
}