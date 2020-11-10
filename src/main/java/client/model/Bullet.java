package client.model;

import client.view.ClientGuiF;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.ImageIcon;

public class Bullet {
    private Image bulletImg;
    private BufferedImage bulletBufImg;

    private int direction;
    private boolean stop = false;
    private int posX, posY;
    private double velocity = 10;
    private static final int size = 8; // width height

    public Bullet(int x, int y, int direction) {
        posX = x;
        posY = y;
        this.direction = direction;
        stop = false;
        bulletImg = new ImageIcon("Imgs/bullet0.png").getImage();

        bulletBufImg = new BufferedImage(bulletImg.getWidth(null), bulletImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
        bulletBufImg.createGraphics().drawImage(bulletImg, 0, 0, null);
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int x) {
        posX = x;
    }

    public void setPosY(int y) {
        posY = y;
    }

    public BufferedImage getBulletBufImg() {
        return bulletBufImg;
    }

    public boolean isStop() {
        return stop;
    }

    public boolean isCollision() {
        List<Tank> tanks = GameArena.getTankArr();
        int x, y;
        for (int i = 1; i < tanks.size(); i++) {
            if (tanks.get(i) != null) {
                x = tanks.get(i).getPosX();
                y = tanks.get(i).getPosY();

                if ((posY >= y && posY <= y + Tank.getTankSize()) && (posX >= x && posX <= x + Tank.getTankSize())) {

                    ClientGuiF.setScore(50);

                    ClientGuiF.getMainPanel().repaint();

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    if (tanks.get(i) != null)
                        BClient.getInstance().sendToServer((new MsgProtocol())
                                .removeClientPacket(tanks.get(i).getTankID()));

                    return true;
                }
            }
        }
        return false;
    }


    public void startBulletThread(boolean chekCollision) {

        new BulletThread(chekCollision).start();

    }

    private class BulletThread extends Thread {
        boolean checkCollision;

        public BulletThread(boolean checkCollision) {
            this.checkCollision = checkCollision;
        }

        public void run() {

            if (direction == 1) {
                posX = 17 + posX;
                while (posY > 50) {
                    posY = (int) (posY - velocity);
                    if (checkCollision && isCollision()) {
                        break;
                    }
                    try {

                        Thread.sleep(40);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                }

            } else if (direction == 2) {
                posY = 17 + posY;
                posX += 30;
                while (posX < 564) {
                    posX = (int) (posX + velocity);
                    if (checkCollision && isCollision()) {
                        break;
                    }
                    try {

                        Thread.sleep(40);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                }
            } else if (direction == 3) {
                posY += 30;
                posX += 20;
                while (posY < 505) {
                    posY = (int) (posY + velocity);
                    if (checkCollision && isCollision()) {
                        break;
                    }
                    try {

                        Thread.sleep(40);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                }
            } else if (direction == 4) {
                posY = 21 + posY;

                while (posX > 70) {
                    posX = (int) (posX - velocity);
                    if (checkCollision && isCollision()) {
                        break;
                    }
                    try {

                        Thread.sleep(40);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                }
            }

            stop = true;
        }
    }
}
