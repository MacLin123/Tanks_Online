package server.model;

import config.Config;
import server.presenter.BServerHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server extends Thread {
    private List<ClientData> clientDataList;
    private ServerSocket serverSocket;
    private Set<Thread> threads = new HashSet<>();
    private int serverPort = 10000;

    private MsgProtocol msgProtocol;
    private boolean isRunning = true;

    public Server() {
        clientDataList = new ArrayList<>(Config.MAX_PLAYERS);
        for (int i = 0; i < Config.MAX_PLAYERS; i++) {
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

                DataOutputStream respToPlayer = new DataOutputStream(clientSoc.getOutputStream());
                if (clientDataList.indexOf(null) == -1) {
                    respToPlayer.writeUTF(msgProtocol.getRefuseConnPacket("Max players on the server"));
                    respToPlayer.flush();
                    continue;
                } else {
                    respToPlayer.writeUTF(msgProtocol.getOkConPacket());
                    respToPlayer.flush();
                }

                Thread handlerThread = new Thread(BServerHandler.build(clientSoc, isRunning, clientDataList));
                threads.add(handlerThread);
                handlerThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try { // when stop server
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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