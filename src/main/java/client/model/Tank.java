package client.model;

import config.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public class Tank {
    private Image[] tankImg;
    private BufferedImage bufImg;
    // 3 - shot\sec, 10s fly time shot(sec)
    private Bullet bullets[] = new Bullet[Config.MAX_BULLETS];
    private int curBullet = 0;
    private int tankID;
    private int posX = -1, posY = -1;

    public int getDirection() {
        return direction;
    }

    private int direction = 1;
    private double velocity = 4;
    private int width = 490, height = 510;
    private final int minXpos = 50, minYpos = 60;
    private final int maxXpos = height - minXpos, maxYPos = height - minYpos;

    private static final int TANK_SIZE = 40; // 40x40

    public Tank() {
        initCoordRandom();
        loadImage(4);
    }

    public Tank(int x, int y, int dir, int id) {
        posX = x;
        posY = y;
        tankID = id;
        direction = dir;
        loadImage(0);
    }

    public static int getTankSize() {
        return TANK_SIZE;
    }

    public void initCoordRandom() {
        Random random = new Random();
        posX = random.nextInt(maxXpos - minXpos) + minXpos;
        posY = random.nextInt(maxYPos - minYpos) + minYpos;
    }

    public void loadImage(int val) {
        tankImg = new Image[4];
        for (int i = val; i < tankImg.length + val; i++) {
            tankImg[i - val] = new ImageIcon("Imgs/" + i + ".png").getImage();
        }
        bufImg = new BufferedImage(tankImg[direction - 1].getWidth(null),
                tankImg[direction - 1].getHeight(null), BufferedImage.TYPE_INT_RGB);
        bufImg.createGraphics().drawImage(tankImg[direction - 1], 0, 0, null);

    }

    public BufferedImage getBufImg() {
        return bufImg;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setTankID(int tankID) {
        this.tankID = tankID;
    }

    public int getTankID() {
        return tankID;
    }

    public void setDirection(int direction) {
        bufImg = new BufferedImage(tankImg[direction - 1].getWidth(null),
                tankImg[direction - 1].getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        bufImg.createGraphics().drawImage(tankImg[direction - 1], 0, 0, null);
        this.direction = direction;
    }

    public void goLeft() {
        if (direction != 4) {
            setDirection(4);
        }

        int tmpCoordX = (int) (posX - velocity);
        if (!isCollision(tmpCoordX, posY) && tmpCoordX < minXpos) {
            posX = minXpos;
        } else if (!isCollision(tmpCoordX, posY)) {
            posX = tmpCoordX;
        }


    }

    public void goRight() {
        if (direction != 2) {
            setDirection(2);
        }
        int temp;
        int tmpCoordX = (int) (posX + velocity);
        if (!isCollision(tmpCoordX, posY) && tmpCoordX > width) {

            posX = width;
        } else if (!isCollision(tmpCoordX, posY)) {
            posX = tmpCoordX;
        }

    }

    public void goForward() {
        if (direction != 1) {
            setDirection(1);
        }

        int tmpCoordY = (int) (posY - velocity);
        if (!isCollision(posX, tmpCoordY) && tmpCoordY < minYpos) {
            posY = minYpos;
        } else if (!isCollision(posX, tmpCoordY)) {
            posY = tmpCoordY;
        }

    }

    public void goBack() {
        if (direction != 3) {
            setDirection(3);
        }

        int tmpCoordY = (int) (posY + velocity);
        if (!isCollision(posX, tmpCoordY) && tmpCoordY > height) {
            posY = height;
        } else if (!isCollision(posX, tmpCoordY)) {
            posY = tmpCoordY;
        }
    }

    public void myShot() {
        bullets[curBullet] = new Bullet(this.getPosX(), this.getPosY(), direction);

        bullets[curBullet].startBulletThread(true);
        curBullet++;
        if (curBullet % bullets.length == 0) {
            curBullet = 0;
        }
        System.out.println(curBullet);
    }

    /**
     * Other player shot
     */
    public void otherShot() {
        bullets[curBullet] = new Bullet(this.getPosX(), this.getPosY(), direction);

        bullets[curBullet].startBulletThread(false);
        curBullet++;
    }

    public boolean isCollision(int xPos, int yPos) {
        List<Tank> tanks = GameArena.getTankArr();
        if (tanks == null) {
            return false;
        }
        int x, y;
        for (int i = 0; i < tanks.size(); i++) { // from one because id starts with one
            if (tanks.get(i) != null) {
                x = tanks.get(i).getPosX();
                y = tanks.get(i).getPosY();

                boolean condition1Y = (yPos <= y + TANK_SIZE) && yPos >= y;
                boolean condition2Y = (yPos + TANK_SIZE >= y) && yPos + TANK_SIZE <= y + TANK_SIZE;
                boolean condition1X = xPos <= x + TANK_SIZE && xPos >= x;
                boolean condition2X = xPos + TANK_SIZE >= x && xPos + TANK_SIZE <= x + TANK_SIZE;
                if (direction == 1) {
                    if (condition1Y && (condition1X || condition2X)) {
                        return true;
                    }
                } else if (direction == 2) {
                    if (condition2X && (condition1Y || condition2Y)) {
                        return true;
                    }
                } else if (direction == 3) {
                    if (condition2Y && (condition1X || condition2X)) {
                        return true;
                    }
                } else if (direction == 4) {
                    if (condition1X && (condition1Y || condition2Y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Bullet[] getBullets() {
        return bullets;
    }
}
