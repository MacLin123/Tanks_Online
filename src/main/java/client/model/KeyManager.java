package client.model;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {
    private Tank tank;
    private Client client;
    private final int LEFT = KeyEvent.VK_LEFT;
    private final int RIGHT = KeyEvent.VK_RIGHT;
    private final int UP = KeyEvent.VK_UP;
    private final int DOWN = KeyEvent.VK_DOWN;
    private static int status = 0;
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

            client.sendToServer(msgProtocol.updatePacket(tank.getPosX(),
                    tank.getPosY(), tank.getTankID(), tank.getDirection()));
        } else if (e.getKeyCode() == RIGHT) {
            tank.goRight();
            client.sendToServer(msgProtocol.updatePacket(tank.getPosX(),
                    tank.getPosY(), tank.getTankID(), tank.getDirection()));

        } else if (e.getKeyCode() == UP) {
            tank.goForward();
            client.sendToServer(msgProtocol.updatePacket(tank.getPosX(),
                    tank.getPosY(), tank.getTankID(), tank.getDirection()));

        } else if (e.getKeyCode() == DOWN) {
            tank.goBackward();

            client.sendToServer(msgProtocol.updatePacket(tank.getPosX(),
                    tank.getPosY(), tank.getTankID(), tank.getDirection()));

        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            System.out.println("pi piu");
//            client.sendToServer(msgProtocol.ShotPacket(tank.getTankID()));
//            tank.shot();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
