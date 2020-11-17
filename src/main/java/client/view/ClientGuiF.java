package client.view;

import client.model.GameArena;
import client.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import config.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientGuiF extends JFrame implements ActionListener, WindowListener {
    private JButton connectBtn;
    private static JPanel mainPanel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private static JLabel scoreLabel;
    private JTextField ipText;
    private JTextField portText;
    private Client client;
    private int width = 800, height = 640;
    private int xLoc = 60, yLoc = 100;
    private static int score = 0;
    private boolean isRunning = true;
    private GameArena gameArena;
    private Tank tank;

    public ClientGuiF() {
        setTitle("Tanks_Online");
        setSize(width, height);
        setLocation(xLoc, yLoc);
        getContentPane().setBackground(Color.BLACK);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        addWindowListener(this);

        int mpW = 210, mpH = 180, mpX = 580, mpY = 20;
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.gray);
        mainPanel.setSize(mpW, mpH);
        mainPanel.setBounds(mpX, mpY, mpW, mpH);
        mainPanel.setLayout(null);

        ipLabel = new JLabel("IP address: ");
        ipLabel.setBounds(15, 30, 90, 30);

        portLabel = new JLabel("Port: ");
        portLabel.setBounds(50, 55, 50, 30);

        ipText = new JTextField("localhost");
        ipText.setBounds(90, 30, 100, 30);

        portText = new JTextField("10000");
        portText.setBounds(90, 55, 100, 30);

        scoreLabel = new JLabel("Score : 0");
        scoreLabel.setBounds(90, 130, 100, 20);

        scoreLabel.setFont(new Font(scoreLabel.getFont().getName(),
                Font.PLAIN, scoreLabel.getHeight()));

        connectBtn = new JButton("Connect");
        connectBtn.setBounds(90, 100, 90, 25);
        connectBtn.addActionListener(this);
        connectBtn.setFocusable(true);

        mainPanel.add(ipLabel);
        mainPanel.add(portLabel);
        mainPanel.add(scoreLabel);
        mainPanel.add(ipText);
        mainPanel.add(portText);
        mainPanel.add(connectBtn);

        client = BClient.getInstance();
        tank = new Tank();
        gameArena = new GameArena(tank, client, false);

        getContentPane().add(mainPanel);
        getContentPane().add(gameArena);

        setVisible(true);
    }

    public static void setScore(int scoreArg) {
        score += scoreArg;
        scoreLabel.setText("Score: " + score);
    }

    public static JPanel getMainPanel() {
        return mainPanel;
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == connectBtn) {
            connectBtn.setEnabled(false);
            try {
                client.connect(ipText.getText(), Integer.parseInt(portText.getText()),
                        tank.getPosX(), tank.getPosY());
                gameArena.setGameRunning(true);
                gameArena.repaint();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                new ClientReciever(client.getSocket()).start();
                connectBtn.setFocusable(false);
                gameArena.setFocusable(true);

            } catch (IOException ioException) {
                System.out.println(ioException.getMessage());
                connectBtn.setEnabled(true);
            }
        }
    }

    public void windowOpened(WindowEvent e) {

    }

    public void windowClosing(WindowEvent e) {
        BClient.getInstance().sendToServer(new MsgProtocol().exitJsonPacket(tank.getTankID()));
    }

    public void windowClosed(WindowEvent e) {

    }

    public void windowIconified(WindowEvent e) {

    }

    public void windowDeiconified(WindowEvent e) {

    }

    public void windowActivated(WindowEvent e) {

    }

    public void windowDeactivated(WindowEvent e) {

    }

    public class ClientReciever extends Thread {
        private Socket clientSoc;
        private DataInputStream dis;
        private Gson gson = new Gson();

        public ClientReciever(Socket clientSoc) {
            this.clientSoc = clientSoc;
            try {
                dis = new DataInputStream(clientSoc.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleUpdPacket(String msgStr) {
            JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
            int x = msgJson.getAsJsonPrimitive("x").getAsInt();
            int y = msgJson.getAsJsonPrimitive("y").getAsInt();
            int id = msgJson.getAsJsonPrimitive("id").getAsInt();
            int dir = msgJson.getAsJsonPrimitive("dir").getAsInt();

            if (id != tank.getTankID()) {
                gameArena.getTank(id).setPosX(x);
                gameArena.getTank(id).setPosY(y);
                gameArena.getTank(id).setDirection(dir);
                gameArena.repaint();
            }
        }

        private void handleShotPacket(String msgStr) {
            System.out.println("pui");
            JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
            int id = msgJson.getAsJsonPrimitive("id").getAsInt();

            if (id != tank.getTankID()) {
                gameArena.getTank(id).otherShot();
            }
        }

        private void handleRemovePacket(String msgStr) {
            JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
            int id = msgJson.getAsJsonPrimitive("id").getAsInt();

            if (id == tank.getTankID()) {
                int response = JOptionPane.showConfirmDialog(null, "Sorry, You are loss. Do you want to try again ?", "Tanks 2D Multiplayer Game", JOptionPane.OK_CANCEL_OPTION);
                if (response == JOptionPane.OK_OPTION) {
                    //client.closeAll();
                    setVisible(false);
                    dispose();
                    new ClientGuiF(); //???
                } else {
                    //send exit packet
                    client.sendToServer(new MsgProtocol().exitJsonPacket(tank.getTankID()));
                    System.exit(0);
                }
            } else {
                gameArena.removeTank(id);
            }
        }

        private void handleExitPacket(String msgStr) {
            JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
            int id = msgJson.getAsJsonPrimitive("id").getAsInt();

            if (id != tank.getTankID()) {
                gameArena.removeTank(id);
            }

        }

        private void handleIdPacket(String msgStr) {
            JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
            int id = msgJson.getAsJsonPrimitive("id").getAsInt();
            tank.setTankID(id);
            System.out.println("Tank id = " + id);
        }

        private void handleNewClientPacket(String msgStr) {
            JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
            int id = msgJson.getAsJsonPrimitive("id").getAsInt();
            int x = msgJson.getAsJsonPrimitive("x").getAsInt();
            int y = msgJson.getAsJsonPrimitive("y").getAsInt();
            int direction = msgJson.getAsJsonPrimitive("dir").getAsInt();

            if (id != tank.getTankID())
                gameArena.connetNewTank(new Tank(x, y, direction, id));
        }

        public void run() {
            String msgStr = "";
            while (isRunning) {
                try {
                    msgStr = dis.readUTF();
                } catch (IOException e) {
                    System.out.println(e.getMessage() + " socket problem on client side");
                    System.exit(0);
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

                if (typeMsg.equals(Config.typesServerMsg.ID.getType())) {
                    handleIdPacket(msgStr);
                } else if (typeMsg.equals(Config.typesServerMsg.NEWCLIENT.getType())) { //new tank
                    handleNewClientPacket(msgStr);
                } else if (typeMsg.equals(Config.typesClientMsg.UPDATE.getType())) {
                    handleUpdPacket(msgStr);

                } else if (typeMsg.equals(Config.typesClientMsg.SHOT.getType())) {
                    handleShotPacket(msgStr);

                } else if (typeMsg.equals(Config.typesClientMsg.REMOVE.getType())) {
                    handleRemovePacket(msgStr);
                } else if (typeMsg.equals(Config.typesClientMsg.EXIT.getType())) {
                    handleExitPacket(msgStr);
                }
            }
            try {
                dis.close();
                clientSoc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
