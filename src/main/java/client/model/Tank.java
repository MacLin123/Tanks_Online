package client.model;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public class Tank {
    private Image[] tankImg;
    private BufferedImage imgBuf;
    //    private final int RED_TANK_IND = 0;
//    private final int BLUE_TANK_IND = 4;
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
        for (int i = val; i < tankImg.length + val; i++) {
            tankImg[i - val] = new ImageIcon("Imgs/" + i + ".png").getImage();
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

    public void goBackward() {
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

    public boolean isCollision(int xPos, int yPos) {
        List<Tank> tanks = GameArena.getTankArr();
        int x, y;
        for (int i = 1; i < tanks.size(); i++) { // from one because id starts with one
            if (tanks.get(i) != null) {
                x = tanks.get(i).getPosX();
                y = tanks.get(i).getPosY();


                if (direction == 1) {
                    if (((yPos <= y + 43) && yPos >= y) && ((xPos <= x + 43 && xPos >= x) ||
                            (xPos + 43 >= x && xPos + 43 <= x + 43))) {
                        return true;
                    }
                } else if (direction == 2) {
                    if (((xPos + 43 >= x) && xPos + 43 <= x + 43) && ((yPos <= y + 43 & yPos >= y) ||
                            (yPos + 43 >= y && yPos + 43 <= y + 43))) {
                        return true;
                    }
                } else if (direction == 3) {
                    if (((yPos + 43 >= y) && yPos + 43 <= y + 43) && ((xPos <= x + 43 && xPos >= x) ||
                            (xPos + 43 >= x && xPos + 43 <= x + 43))) {
                        return true;
                    }
                } else if (direction == 4) {
                    if (((xPos <= x + 43) && xPos >= x) && ((yPos <= y + 43 && yPos >= y) ||
                            (yPos + 43 >= y && yPos + 43 <= y + 43))) {
                        return true;
                    }
                }
            }


//                if (direction == 1) {
//                    if (((yPos <= y + minYpos) && yPos >= y) && ((xPos <= x + minXpos && xPos >= x)
//                            || (xPos + minXpos >= x && xPos + minXpos <= x + minXpos))) {
//                        return true;
//                    }
////                    if((yPos <= minYpos || y >= maxYPos || (Math.abs(yPos - y) <= minYpos)) ||
////                            (xPos <= minXpos || x >= maxXpos || (Math.abs(xPos - x) <= minXpos))) {
////                        return true;
////                    }
//                } else if (direction == 2) {
//                    if (((xPos + minXpos >= x) && xPos <= x) && ((yPos <= y + minYpos & yPos >= y) || (yPos + minYpos >= y && yPos + minYpos <= y + minYpos))) {
//                        return true;
//                    }
//                } else if (direction == 3) {
//                    if (((yPos + minXpos >= y) && yPos <= y) && ((xPos <= x + minXpos && xPos >= x)
//                            || (xPos + minXpos >= x && xPos <= x))) {
//                        return true;
//                    }
//                } else if (direction == 4) {
//                    if (((xPos <= x + minXpos) && xPos >= x) && ((yPos <= y + minYpos && yPos >= y)
//                            || (yPos + minYpos >= y && yPos <= y))) {
//                        return true;
//                    }
//                }
        }
        return false;
    }
}
