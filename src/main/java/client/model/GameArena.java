package client.model;

import client.controller.KeyManager;
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
        addKeyListener(new KeyManager(tank));
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
        g.fillRect(40, 50, getWidth() - 80, getHeight() - 80); // replace with const
//        g.drawImage(new ImageIcon("Imgs/background.jpg").getImage(),70,50,null);
        g.setColor(Color.RED);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        g.drawString("TANKS ONLINE", 220, 30);

        if (gameRunning) {
            g.drawImage(tank.getBufImg(), tank.getPosX(), tank.getPosY(), this);
            for (int j = 0; j < Config.MAX_BULLETS; j++) {
                if (tank.getBullets()[j] != null) {
                    if (!tank.getBullets()[j].isStop()) {
                        g.drawImage(tank.getBullets()[j].getBulletBufImg(),
                                tank.getBullets()[j].getPosX(),
                                tank.getBullets()[j].getPosY(), this);
                    }
                }
            }
            for (int i = 0; i < tankArr.size(); i++) {
                if (tankArr.get(i) != null)
                    g.drawImage(tankArr.get(i).getBufImg(), tankArr.get(i).getPosX(),
                            tankArr.get(i).getPosY(), this);
                for (int j = 0; j < Config.MAX_BULLETS; j++) {
                    if (tankArr.get(i) != null) {
                        if (tankArr.get(i).getBullets()[j] != null) {
                            if (!tankArr.get(i).getBullets()[j].isStop()) {
                                g.drawImage(tankArr.get(i).getBullets()[j].getBulletBufImg(),
                                        tankArr.get(i).getBullets()[j].getPosX(),
                                        tankArr.get(i).getBullets()[j].getPosY(), this);
                            }
                        }
                    }
                }
            }
        }
        repaint();
    }

    public void connetNewTank(Tank newTank) {
        System.out.println("tank id  = " + newTank.getTankID());
        tankArr.set(newTank.getTankID(), newTank);
    }

    public void removeTank(int tankId) {
        tankArr.set(tankId, null);
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

