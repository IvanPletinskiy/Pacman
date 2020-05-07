package dawist_o.pacman.gui;

import dawist_o.pacman.engine.KeyboardListener;
import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Menu extends PacmanActor {
    public Menu(PacmanGame game) {
        super(game);
    }

    private boolean pushSpaceToStartVisible;

    @Override
    public void init() {
        //загружаем надпись заголовка
        loadFrames("/resources/title.png");
        xCoord = 21;
        yCoord = 100;
    }

    @Override
    public void updateMenu() {
        boolean isInMenu = true;
        while (isInMenu) {
            switch (instructionPointer) {
                case 0:
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    if (System.currentTimeMillis() - waitTime < 500) {
                        isInMenu = false;
                        break;
                    }
                    instructionPointer = 2;
                case 2:
                    //эффект опускания заголовка игры
                    double dy = 100 - yCoord;
                    yCoord = yCoord + dy * 0.1;
                    if (Math.abs(dy) < 1) {
                        waitTime = System.currentTimeMillis();
                        instructionPointer = 3;
                    }
                    isInMenu = false;
                    break;
                case 3:
                    if (System.currentTimeMillis() - waitTime < 200) {
                        isInMenu = false;
                        break;
                    }
                    instructionPointer = 4;
                case 4:
                    pushSpaceToStartVisible = ((int) (System.nanoTime() * 0.0000000075) % 3) > 0;
                    if (KeyboardListener.keyPressed[KeyEvent.VK_SPACE])
                        game.start();
                    isInMenu = false;
                    break;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        //отображение информации
        if (!visible)
            return;
        super.draw(g);
        if (pushSpaceToStartVisible)
            game.drawText(g, "Push space to start", 37, 170);
        game.drawText(g, "Kovalenko Vladislav", 37, 235);
        game.drawText(g, "951008 BSUIR 2020", 45, 250);
    }

    @Override
    public void stateChanged() {
        visible = false;
        //если в меню, то присваиваем переменным следующие значения
        if (game.getState() == PacmanGame.State.MENU) {
            yCoord = -150;
            visible = true;
            pushSpaceToStartVisible = false;
            instructionPointer = 0;
        }
    }
}

