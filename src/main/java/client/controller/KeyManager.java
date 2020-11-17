package client.controller;

import client.model.BClient;
import client.model.Client;
import client.model.MsgProtocol;
import client.model.Tank;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {
    private Tank tank;
    private Client client;
    private final int LEFT = KeyEvent.VK_LEFT;
    private final int RIGHT = KeyEvent.VK_RIGHT;
    private final int UP = KeyEvent.VK_UP;
    private final int DOWN = KeyEvent.VK_DOWN;
    private static final long MINIMUM_SHOTS_DELAY = 400; //millis
    private long lastShotTimeStamp = 0;
    private MsgProtocol msgProtocol;

    public KeyManager(Tank tank) {
        this.client = BClient.getInstance();
        this.tank = tank;
        msgProtocol = new MsgProtocol();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == LEFT) {
            tank.goLeft();

            client.sendToServer(msgProtocol.updateJsonPacket(tank.getPosX(),
                    tank.getPosY(), tank.getTankID(), tank.getDirection()));
        } else if (e.getKeyCode() == RIGHT) {
            tank.goRight();
            client.sendToServer(msgProtocol.updateJsonPacket(tank.getPosX(),
                    tank.getPosY(), tank.getTankID(), tank.getDirection()));

        } else if (e.getKeyCode() == UP) {
            tank.goForward();
            client.sendToServer(msgProtocol.updateJsonPacket(tank.getPosX(),
                    tank.getPosY(), tank.getTankID(), tank.getDirection()));

        } else if (e.getKeyCode() == DOWN) {
            tank.goBack();

            client.sendToServer(msgProtocol.updateJsonPacket(tank.getPosX(),
                    tank.getPosY(), tank.getTankID(), tank.getDirection()));

        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            long now = System.currentTimeMillis();
            if (now - lastShotTimeStamp > MINIMUM_SHOTS_DELAY) {
                client.sendToServer(msgProtocol.shotJsonPacket(tank.getTankID()));
                tank.myShot();

                lastShotTimeStamp = now;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
