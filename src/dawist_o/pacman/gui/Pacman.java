package dawist_o.pacman.gui;

import dawist_o.pacman.engine.KeyboardListener;
import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Pacman extends PacmanActor {
    public int currentDir;
    public int requestDir;
    public int dirX;
    public int dirY;
    public long deathTime;
    public int row;
    public int col;

    public Pacman(PacmanGame game) {
        super(game);
    }

    @Override
    public void init() {
        String[] pacmanFrames = new String[30];
        //кадры движения пакмана
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                pacmanFrames[i * 4 + j] = "/resources/pacman_" + i + "_" + j + ".png";
            }
        }
        //кадры смерти пакмана
        for (int i = 0; i < 14; i++) {
            pacmanFrames[16 + i] = "/resources/pacman_died_" + i + ".png";
        }
        loadFrames(pacmanFrames);
        reset();
        collider = new Rectangle(0, 0, 8, 8);
    }

    private void reset() {
        //возврат на начальную позицию в строках и столбцах
        //установление первого направления и кадра
        col = 18;
        row = 23;
        updatePos();
        frame = frames[0];
        currentDir = requestDir = 0;
    }

    public void updatePos() {
        //обновление позиции в пикселях
        xCoord = col * 8 - 32 - 4 - 4;
        yCoord = (row + 3) * 8 - 4;
    }

    private boolean moveToTargetPos(int targetX, int targetY, int velocity) {
        //движение пакмана в меню
        int spaceX = (int) (targetX - xCoord);
        int spaceY = (int) (targetY - yCoord);
        int dX = velocity * (Integer.compare(spaceX, 0));
        int dY = velocity * (Integer.compare(spaceY, 0));
        xCoord += dX;
        yCoord += dY;
        return spaceX != 0 || spaceY != 0;
    }

    @Override
    public void updateMenu() {
        //анимация в меню
        boolean isMenuUpdate = true;
        while (isMenuUpdate) {
            switch (instructionPointer) {
                case 0:
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    //задержка перед движением влево
                    if (System.currentTimeMillis() - waitTime < 3000) {
                        isMenuUpdate = false;
                        break;
                    }
                    instructionPointer = 2;
                case 2:
                    //движение пакнмана на экране влево
                    currentDir = 0;
                    if (!moveToTargetPos(250, 200, 1)) {
                        waitTime = System.currentTimeMillis();
                        instructionPointer = 3;
                    }
                    isMenuUpdate = false;
                    break;
                case 3:
                    //задержка 3 секунды перед движение вправо
                    if (System.currentTimeMillis() - waitTime < 3000) {
                        isMenuUpdate = false;
                        break;
                    }
                    instructionPointer = 4;
                case 4:
                    //движение пакнмана на экране вправо
                    currentDir = 2;
                    if (!moveToTargetPos(-100, 200, 1)) {
                        instructionPointer = 0;
                    }
                    isMenuUpdate = false;
                    break;
            }
        }
        updateAnimation();
    }

    private void updateAnimation() {
        //обновление изображения пакмана
        int frameIndex = 4 * currentDir + (int) (System.nanoTime() * 0.00000002) % 4;
        frame = frames[frameIndex];
    }

    private void checkRequestDirection() {
        //изменение желаемого направления
        if (KeyboardListener.keyPressed[KeyEvent.VK_LEFT]) {
            requestDir = 2;
        } else if (KeyboardListener.keyPressed[KeyEvent.VK_RIGHT]) {
            requestDir = 0;
        } else if (KeyboardListener.keyPressed[KeyEvent.VK_UP]) {
            requestDir = 3;
        } else if (KeyboardListener.keyPressed[KeyEvent.VK_DOWN]) {
            requestDir = 1;
        }
    }

    @Override
    public void updatePlaying() {
        checkRequestDirection();
        boolean isMoving = true;
        while (isMoving) {
            switch (instructionPointer) {
                case 0:
                    //преобразуем направления на оси OX и OY  с помощью синуса и косинуса
                    double angle = Math.toRadians(requestDir * 90);
                    dirX = (int) Math.cos(angle);
                    dirY = (int) Math.sin(angle);
                    //если персонаж может пройти в желаемом направлении, то меняем текущее направление на желаемое
                    if (game.maze[row + dirY][col + dirX] == 0) {
                        currentDir = requestDir;
                    }
                    angle = Math.toRadians(currentDir * 90);
                    dirX = (int) Math.cos(angle);
                    dirY = (int) Math.sin(angle);
                    //если следующая координата стена, то не двигаем пакмана
                    if (game.maze[row + dirY][col + dirX] == -1) {
                        isMoving = false;
                        break;
                    }
                    //если следующий элемент лабиринта не стена, то делаем шаг
                    col += dirX;
                    row += dirY;
                    instructionPointer = 1;
                case 1:
                    //отображение пакмана при входе в боковые проходы
                    int targetX = col * 8 - 4 - 32;
                    int targetY = (row + 3) * 8 - 4;
                    int difX = (targetX - (int) xCoord);
                    int difY = (targetY - (int) yCoord);
                    xCoord += Integer.compare(difX, 0);
                    yCoord += Integer.compare(difY, 0);
                    //проход пакмана в боковые стороны
                    if (difX == 0 && difY == 0) {
                        instructionPointer = 0;
                        if (col == 1) {
                            col = 34;
                            xCoord = col * 8 - 4 - 24;
                        } else if (col == 34) {
                            col = 1;
                            xCoord = col * 8 - 4 - 24;
                        }
                    }
                    isMoving = false;
                    break;
            }
        }
        updateAnimation();
        if (game.isLevelCleared())
            game.levelCleared();
    }

    @Override
    public void updateCollider() {
        //обновляем границы пакмана
        collider.setLocation((int) (xCoord + 4), (int) (yCoord + 4));
    }


    @Override
    public void stateChanged() {
        //в зависимости от стадии игры изменяем положения пакмана, его видимость и текущий кадр
        switch (game.getState()) {
            case MENU -> {
                xCoord = -100;
                yCoord = 200;
                instructionPointer = 0;
                visible = true;
            }
            case READY -> visible = false;
            case READY_NEXT_LIFE -> reset();
            case PLAYING, PACMAN_DIED -> instructionPointer = 0;
            case LEVEL_CLEARED -> frame = frames[0];
        }
    }

    @Override
    public void updatePacmanDied() {
        boolean cooldown = true;
        while (cooldown) {
            switch (instructionPointer) {
                case 0:
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    if (System.currentTimeMillis() - waitTime < 2000) {
                        cooldown = false;
                        break;
                    }
                    deathTime = System.currentTimeMillis();
                    instructionPointer = 2;
                case 2:
                    //отрисовка кадров при смерти пакмана
                    int frameIndex = 16 + (int) ((System.currentTimeMillis() - deathTime) * 0.0075);
                    frame = frames[frameIndex];
                    if (frameIndex == 29) {
                        waitTime = System.currentTimeMillis();
                        instructionPointer = 3;
                    }
                    //если все кадры были отрисованы, то, начинаем следующую жизнь или возращаемся в меню
                    cooldown = false;
                    break;
                case 3:
                    if (System.currentTimeMillis() - waitTime < 1500) {
                        cooldown = false;
                        break;
                    }
                    instructionPointer = 4;
                case 4:
                    game.nextLife();
                    cooldown = false;
                    break;
            }
        }
    }

    public void showAll() {
        visible = true;
    }

    public void hideAll() {
        visible = false;
    }
}
