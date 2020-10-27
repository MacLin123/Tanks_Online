package client.model;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Tank {
    private Image[] tankImg;
    private BufferedImage imgBuf;
    //    private final int RED_TANK_IND = 0;
//    private final int BLUE_TANK_IND = 4;
    private int tankID;
    private int posX = -1, posY = -1;
    private int direction = 1;
    private double velocity = 0.03;
    private int width = 400, height = 300;
    final private int minXpos = 50, minYpos = 50;
    final private int maxXpos = height - minXpos, maxYPos = height - minYpos;

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

    public void initCoordRandom() {
        Random random = new Random();
        posX = random.nextInt(maxXpos - minXpos) + minXpos;
        posY = random.nextInt(maxYPos - minYpos) + minYpos;

        //CheckCollision??
    }

    public void loadImage(int val) {
        tankImg = new Image[4];
        for (int i = 0; i < tankImg.length; i++) {
            tankImg[i] = new ImageIcon("Imgs/" + val + ".png").getImage();
        }
        imgBuf = new BufferedImage(tankImg[direction - 1].getWidth(null),
                tankImg[direction - 1].getHeight(null), BufferedImage.TYPE_INT_RGB);
        imgBuf.createGraphics().drawImage(tankImg[direction - 1], 0, 0, null);

    }

    public BufferedImage getImgBuf() {
        return imgBuf;
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
        imgBuf = new BufferedImage(tankImg[direction - 1].getWidth(null),
                tankImg[direction - 1].getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        imgBuf.createGraphics().drawImage(tankImg[direction - 1], 0, 0, null);
        this.direction = direction;
    }
}
