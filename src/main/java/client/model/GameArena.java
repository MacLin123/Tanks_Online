package client.model;


import config.Config;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameArena extends JPanel {
    private Tank tank;
    private int width = 580, height = 600;
    private static List<Tank> tankArr;
    private boolean gameRunning;

    public GameArena(Tank tank, Client client, boolean gameRunning) {
        this.tank = tank;
        this.gameRunning = gameRunning;
        setSize(width, height);
//        addKeyListener();
        setFocusable(true);

        tankArr = new ArrayList<>(Config.MAX_PLAYERS);
        for (int i = 0; i < Config.MAX_PLAYERS; i++) {
            tankArr.add(null);
        }

    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.fillRect(40, 50, getWidth() - 80, getHeight()); // replace with const
//        g.drawImage(new ImageIcon("Imgs/background.jpg").getImage(),70,50,null);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        g.drawString("TANKS ONLINE", 220, 30);

        if (gameRunning) {
            g.drawImage(tank.getImgBuf(), tank.getPosX(), tank.getPosY(), this);

            for (int i = 1; i < tankArr.size(); i++) {
                if (tankArr.get(i) != null)
                    g.drawImage(tankArr.get(i).getImgBuf(), tankArr.get(i).getPosX(),
                            tankArr.get(i).getPosY(), this);
            }
        }
        repaint();
    }
    public void connetNewTank(Tank newTank) {
        tankArr.set(newTank.getTankID(),newTank);
    }
    public void removeTank(int tankId) {
        tankArr.set(tankId,null);
    }

    public Tank getTank(int id) {
        return tankArr.get(id);
    }
    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    public static List<Tank> getTankArr() {
        return tankArr;
    }
}

