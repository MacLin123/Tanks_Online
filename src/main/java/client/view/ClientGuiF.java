package client.view;

import client.model.GameArena;
import client.model.*;
import client.view.login.RegisterDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import config.Config;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import client.model.login.UserForAchievements;

public class ClientGuiF extends JFrame implements ActionListener, WindowListener {
    private JButton connectBtn;

    private JButton regBtn;
    private JFrame frame = new JFrame();
    private RegisterDialog regDlg = new RegisterDialog(frame);

    private static JPanel mainPanel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private static JLabel scoreLabel;
    private JTextField ipText;
    private JTextField portText;
    private JTextField usernameText;
    private JPasswordField passwordText;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel leadersLabel;
    private JLabel[] leadersLabels = new JLabel[Config.AMOUNT_LEADERS];
    private IClient client;
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

        int mpW = 210, mpH = 400, mpX = 580, mpY = 20;
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.gray);
        mainPanel.setSize(mpW, mpH);
        mainPanel.setBounds(mpX, mpY, mpW, mpH);
        mainPanel.setLayout(null);

        ipLabel = new JLabel("IP address: ");
        ipLabel.setBounds(15, 30, 90, 25);

        portLabel = new JLabel("Port: ");
        portLabel.setBounds(50, 55, 50, 25);

        ipText = new JTextField("localhost");
        ipText.setBounds(90, 30, 100, 25);

        portText = new JTextField("10000");
        portText.setBounds(90, 55, 100, 25);

        usernameText = new JTextField("");
        usernameText.setBounds(90, 90, 100, 25);

        passwordText = new JPasswordField("");
        passwordText.setBounds(90, 115, 100, 25);

        usernameLabel = new JLabel("Username: ");
        usernameLabel.setBounds(15, 90, 90, 25);

        passwordLabel = new JLabel("Password: ");
        passwordLabel.setBounds(15, 115, 90, 25);

        scoreLabel = new JLabel("Score : 0");
        scoreLabel.setBounds(90, 210, 100, 20);
        scoreLabel.setFont(new Font(scoreLabel.getFont().getName(),
                Font.PLAIN, scoreLabel.getHeight()));

        leadersLabel = new JLabel("Leaders");
        leadersLabel.setBounds(90, 240, 100, 20);
        leadersLabel.setFont(new Font(scoreLabel.getFont().getName(),
                Font.PLAIN, scoreLabel.getHeight()));

        connectBtn = new JButton("Connect");
        connectBtn.setBounds(90, 150, 90, 25);
        connectBtn.addActionListener(this);
        connectBtn.setFocusable(true);

        regBtn = new JButton("Register");
        regBtn.setBounds(90, 180, 90, 25);
        regBtn.addActionListener(this);
        regBtn.setFocusable(true);

        for (int i = 0; i < leadersLabels.length; i++) {
            int offsetY = (i + 1) * 20;
            leadersLabels[i] = new JLabel(String.valueOf(i + 1));
            leadersLabels[i].setBounds(leadersLabel.getX() + 25, leadersLabel.getY() + offsetY, 100, 20);
            mainPanel.add(leadersLabels[i]);
        }


        mainPanel.add(ipLabel);
        mainPanel.add(portLabel);
        mainPanel.add(scoreLabel);
        mainPanel.add(ipText);
        mainPanel.add(portText);
        mainPanel.add(connectBtn);
        mainPanel.add(usernameText);
        mainPanel.add(passwordText);
        mainPanel.add(passwordLabel);
        mainPanel.add(usernameLabel);
        mainPanel.add(leadersLabel);

        mainPanel.add(regBtn);

        client = BClient.getInstance();
        tank = new Tank();
        gameArena = new GameArena(tank, false);

        getContentPane().add(mainPanel);
        getContentPane().add(gameArena);

        setVisible(true);
    }

    public String getIp() {
        return ipText.getText();
    }

    public int getPort() {
        return Integer.parseInt(portText.getText());
    }

    public static void setScore(int scoreArg) {
        score = scoreArg;

        scoreLabel.setText("Score: " + score);
    }

    public void addScoreFromServer() {
        client.sendToServer(new MsgProtocol().addScorePacket(tank.getTankID()));
    }

    public static JPanel getMainPanel() {
        return mainPanel;
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == connectBtn) {
            connectBtn.setEnabled(false);
            try {
                client.connectAndLogin(getIp(), getPort(),
                        tank.getPosX(), tank.getPosY(), usernameText.getText(), new String(passwordText.getPassword()));
                gameArena.setGameRunning(true);
                gameArena.repaint();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                new ClientReceiver(client.getSocket()).start();

                connectBtn.setFocusable(false);
                passwordText.setFocusable(false);
                usernameText.setFocusable(false);
                ipText.setFocusable(false);
                portText.setFocusable(false);
                gameArena.setFocusable(true);
                regBtn.setVisible(false);

            } catch (IOException | IllegalStateException exception) {
                System.out.println(exception.getMessage());
                connectBtn.setEnabled(true);
                connectBtn.setFocusable(true);
                passwordText.setFocusable(true);
                usernameText.setFocusable(true);
                ipText.setFocusable(true);
                portText.setFocusable(true);
            }
        }
        if (obj == regBtn) {
            regDlg.setVisible(true);
            // if logon successfully
            if (regDlg.isSucceeded()) {
                connectBtn.setFocusable(true);
                connectBtn.setEnabled(true);
                regBtn.setVisible(false);
                frame.dispose();
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

    public class ClientReceiver extends Thread {
        private Socket clientSoc;
        private DataInputStream dis;
        private Gson gson = new Gson();

        public ClientReceiver(Socket clientSoc) {
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
                    isRunning = false;
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

        private void handleScoreAddedPacket(String msgStr) {
            JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
            int score = msgJson.getAsJsonPrimitive("score").getAsInt();
            setScore(score);

        }

        private void handleLeadersUpdatePacket(String msgStr) {
            JsonObject msgJson = gson.fromJson(msgStr, JsonObject.class);
            String leadersJson = msgJson.getAsJsonPrimitive("leadersJson").getAsString();
            new Thread(() -> {
                paintLeaderBoard(leadersJson);
            }).start();

        }

        private void paintLeaderBoard(String leadersJson) {
            java.lang.reflect.Type listType = new TypeToken<List<UserForAchievements>>() {
            }.getType();
            List<UserForAchievements> leadersList = new Gson().fromJson(leadersJson, listType);
            for (int i = 0; i < leadersList.size(); i++) {
                leadersLabels[i].setText((i + 1) + ". " +
                        leadersList.get(i).getUsername() + " " + leadersList.get(i).getScore());
            }
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
                } else if (typeMsg.equals(Config.typesServerMsg.SCORE_ADDED.getType())) {
                    handleScoreAddedPacket(msgStr);
                } else if (typeMsg.equals(Config.typesServerMsg.LEADERS_UPDATE.getType())) {
                    handleLeadersUpdatePacket(msgStr);
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
