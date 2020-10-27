package client.view;

import client.model.*;
import config.Config;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

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
    private JPanel mainPanel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private JLabel scoreLabel;
    private JTextField ipText;
    private JTextField portText;
    private Client client;
    private int width = 800, height = 600;
    private int xLoc = 60, yLoc = 100;
    private int score = 0;
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

    public static void main(String[] args) {
        ClientGuiF cgf = new ClientGuiF();
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
        JOptionPane.showConfirmDialog(this,
                "leaving so soon?", "Tanks_Online",
                JOptionPane.YES_NO_OPTION);
        BClient.getInstance().sendToServer(new MsgProtocol().ExitMessagePacket(tank.getTankID()));
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
        Socket clientSoc;
        DataInputStream dis;

        public ClientReciever(Socket clientSoc) {
            this.clientSoc = clientSoc;
            try {
                dis = new DataInputStream(clientSoc.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String msgStr = "";
            while (isRunning) {
                try {
                    msgStr = dis.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(msgStr);
                if (msgStr.startsWith(Config.typesServerMsg.ID.getType())) {
                    int id = Integer.parseInt(msgStr.substring(2));
                    tank.setTankID(id);
                    System.out.println("Tank id = " + id);
                } else if (msgStr.startsWith(Config.typesServerMsg.NEWCLIENT.getType())) { //new tank
                    int startPos = Config.typesServerMsg.NEWCLIENT.getType().length();
                    int pos1 = msgStr.indexOf(',');
                    int pos2 = msgStr.indexOf('-');
                    int pos3 = msgStr.indexOf('|');
                    int x = Integer.parseInt(msgStr.substring(startPos, pos1));
                    int y = Integer.parseInt(msgStr.substring(pos1 + 1, pos2));
                    int direction = Integer.parseInt(msgStr.substring(pos2 + 1, pos3));
                    int id = Integer.parseInt(msgStr.substring(pos3 + 1));
                    if (id != tank.getTankID())
                        gameArena.connetNewTank(new Tank(x, y, direction, id));
                }
            }
            try{
                dis.close();
                clientSoc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
