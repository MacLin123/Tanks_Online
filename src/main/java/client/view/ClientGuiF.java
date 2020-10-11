package client.view;

import client.model.BClient;
import client.model.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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

        portText = new JTextField("11111");
        portText.setBounds(90, 55, 100, 30);

        scoreLabel = new JLabel("Score : 0");
        scoreLabel.setBounds(90, 130, 100, 20);

        scoreLabel.setFont(new Font(scoreLabel.getFont().getName(),
                Font.PLAIN, scoreLabel.getHeight()));

        connectBtn = new JButton("Connect");
        connectBtn.setBounds(90, 100, 90, 25);
        connectBtn.addActionListener(this);
        connectBtn.setFocusable(true);

//        client = BClient.getInstance();


        mainPanel.add(ipLabel);
        mainPanel.add(portLabel);
        mainPanel.add(scoreLabel);
        mainPanel.add(ipText);
        mainPanel.add(portText);
        mainPanel.add(connectBtn);
        getContentPane().add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        ClientGuiF cgf = new ClientGuiF();
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == connectBtn) {
            connectBtn.setEnabled(false);
            // handling...
        }
    }

    public void windowOpened(WindowEvent e) {

    }

    public void windowClosing(WindowEvent e) {
        JOptionPane.showConfirmDialog(this,
                "leaving so soon?", "Tanks_Online",
                JOptionPane.YES_NO_OPTION);
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
}
