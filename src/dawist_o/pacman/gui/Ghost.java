
package dawist_o.pacman.gui;

import com.sun.security.jgss.GSSUtil;
import dawist_o.pacman.engine.PathFinder;
import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame.State;
import dawist_o.pacman.pacmanData.PacmanGame;
import org.w3c.dom.ls.LSOutput;

import java.awt.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Ghost extends PacmanActor {

    public Pacman pacman;
    public int type;
    public Point[] initPositions = {//начальные позиции привидений
            new Point(18, 11), new Point(16, 14),
            new Point(18, 14), new Point(20, 14)};
    public int cageUpDownCount;

    //состояния привидения
    public enum Mode {
        CAGE,
        NORMAL,
        FEAR,
        DIED
    }

    public Mode mode = Mode.CAGE;

    public int dirX;
    public int dirY;
    public int col;
    public int row;

    public int currentDir = 0;
    public int prevDirection;

    public List<Integer> requestDirections = new ArrayList<>();
    public int requestDirection;
    public static final int[] backwardDirections = {2, 3, 0, 1};

    public long fearStartTime;
    public boolean isInFear;

    public PathFinder pathFinder;

    public Ghost(PacmanGame game, Pacman pacman, int type) {
        super(game);
        this.pacman = pacman;
        this.type = type;
        this.pathFinder = new PathFinder(game.maze);
    }

    @Override
    public void updatePlaying() {
        //в зависимости от состояния привидения вызывать нужные методы
        switch (mode) {
            case CAGE -> updateGhostCage();
            case NORMAL -> updateGhostNormal();
            case FEAR -> updateGhostFear();
            case DIED -> updateGhostDied();
        }
        updateAnimation();
    }

    private void setMode(Mode mode) {
        this.mode = mode;
        instructionPointer = 0;
    }

    @Override
    public void init() {
        String[] ghostFrameNames = new String[16];
        for (int i = 0; i < 8; i++) {
            //модельки привидидения
            ghostFrameNames[i] = "/resources/ghost_" + type + "_" + i + ".png";
        }
        for (int i = 0; i < 4; i++) {
            //модельки привидения в режиме страха
            ghostFrameNames[8 + i] = "/resources/ghost_vulnerable_" + i + ".png";
        }
        for (int i = 0; i < 4; i++) {
            //модельки привидения при смерти
            ghostFrameNames[12 + i] = "/resources/ghost_died_" + i + ".png";
        }
        loadFrames(ghostFrameNames);
        collider = new Rectangle(0, 0, 8, 8);
        setMode(Mode.CAGE);
    }

    private int getTargetX(int col) {
        return col * 8 - 3 - 32;
    }

    private int getTargetY(int row) {
        return (row + 3) * 8 - 2;
    }

    public void updatePosition() {
        xCoord = getTargetX(col);
        yCoord = getTargetY(row);
    }

    private void updatePosition(int col, int row) {
        this.col = col;
        this.row = row;
        updatePosition();
    }

    private boolean moveToTargetPosition(int targetX, int targetY, int velocity) {
        //движения привидений к вабранной позиции
        int spaceX = (int) (targetX - xCoord);
        int spaceY = (int) (targetY - yCoord);
        int dX = velocity * (Integer.compare(spaceX, 0));
        int dY = velocity * (Integer.compare(spaceY, 0));
        xCoord += dX;
        yCoord += dY;
        return spaceX != 0 || spaceY != 0;
    }

    private boolean moveToGridPosition(int col, int row, int velocity) {
        //движение приведений в матрице
        int targetX = getTargetX(col);
        int targetY = getTargetY(row);
        return moveToTargetPosition(targetX, targetY, velocity);
    }

    private void controlOutsideMovement() {
        //проход привидения в боковые грани
        if (col == 1) {
            col = 34;
            xCoord = getTargetX(col);
        } else if (col == 34) {
            col = 1;
            xCoord = getTargetX(col);
        }
    }

    public void updateAnimation() {
        int frameIndex = 0;
        switch (mode) {
            case CAGE:
            case NORMAL:
                frameIndex = 2 * currentDir + (int) (System.nanoTime() * 0.00000001) % 2;
                if (!isInFear) {
                    break;
                }
            case FEAR:
                //после 5 секунд страха привидение начинает моргать
                if (System.currentTimeMillis() - fearStartTime > 5000) {
                    frameIndex = 8 + (int) (System.nanoTime() * 0.00000002) % 4;
                } else {
                    frameIndex = 8 + (int) (System.nanoTime() * 0.00000001) % 2;
                }
                break;
            case DIED:
                frameIndex = 12 + currentDir;
                break;
        }
        frame = frames[frameIndex];
    }

    private void updateGhostCage() {
        boolean isInCage = true;
        //движение привидений в клетке
        while (isInCage) {
            switch (instructionPointer) {
                case 0:
                    Point initialPosition = initPositions[type];
                    updatePosition(initialPosition.x, initialPosition.y);
                    xCoord -= 4; //сдвиг влево для корректного отображения призраков в клетке
                    cageUpDownCount = 0;
                    if (type == 0) { //красное привидение
                        instructionPointer = 6;
                        break;
                    } else if (type == 2) { //синее привидение
                        instructionPointer = 2;
                        break;
                    }
                    instructionPointer = 1;
                case 1:
                    if (moveToTargetPosition((int) xCoord, 138, 1)) {
                        isInCage = false;
                        break;
                    }
                    instructionPointer = 2;
                case 2:
                    if (moveToTargetPosition((int) xCoord, 130, 1)) {
                        isInCage = false;
                        break;
                    }
                    cageUpDownCount++;
                    if (cageUpDownCount <= type * 2) {
                        instructionPointer = 1;
                        isInCage = false;
                        break;
                    }
                    instructionPointer = 3;
                case 3:
                    if (moveToTargetPosition((int) xCoord, 134, 1)) {
                        isInCage = false;
                        break;
                    }
                    instructionPointer = 4;
                case 4:
                    //выпускаем привидений из клетки, проводя их через единицы
                    if (moveToTargetPosition(105, 134, 1)) {
                        isInCage = false;
                        break;
                    }
                    instructionPointer = 5;
                case 5:
                    if (moveToTargetPosition(105, 110, 1)) {
                        isInCage = false;
                        break;
                    }
                    if ((int) (2 * Math.random()) == 0) {
                        instructionPointer = 7;
                        break;
                    }
                    instructionPointer = 6;
                case 6:
                    //если движение после выхода из клетки вправо
                    if (moveToTargetPosition(109, 110, 1)) {
                        isInCage = false;
                        break;
                    }
                    requestDirection = 0;
                    prevDirection = 0;
                    updatePosition(18, 11);
                    instructionPointer = 8;
                    break;
                case 7:
                    //если движение после выхода из клетки влево
                    if (moveToTargetPosition(101, 110, 1)) {
                        isInCage = false;
                        break;
                    }
                    requestDirection = 2;
                    prevDirection = 2;
                    updatePosition(17, 11);
                    instructionPointer = 8;
                case 8:
                    setMode(Mode.NORMAL);
                    isInCage = false;
                    break;
            }
        }
    }

    private final PacmanCaughtEvent pacmanCaughtEvent = new PacmanCaughtEvent();

    private class PacmanCaughtEvent implements Runnable {
        @Override
        public void run() {
            game.setState(State.PACMAN_DIED);
        }
    }

    private void updateGhostNormal() {
        //обычный режим привидений
        if (checkFearTime() && isInFear) {
            setMode(Mode.FEAR);
            isInFear = false;
        }
        if (type == 0 || type == 1) { //красное и розовое привидения преследуют пакмана
            updateGhostMovement(true, pacman.col, pacman.row, 1, pacmanCaughtEvent, 0, 1, 2, 3);
        } else {
            updateGhostMovement(false, 0, 0, 1, pacmanCaughtEvent, 0, 1, 2, 3);
        }
    }

    private void updateGhostFear() {
        if (isInFear)
            isInFear = false;
        //если привидения в режиме страха
        updateGhostMovement(true, pacman.col, pacman.row, 1, ghostCaughtEvent, backwardDirections);
        if (!checkFearTime()) {
            setMode(Mode.NORMAL);
        }
    }

    private void updateGhostMovement(boolean useTarget, int targetCol, int targetRow
            , int velocity, Runnable collisionWithTarget, int... desiredDirectionsMap) {
        //проверяем допустимые направления движения привидения
        requestDirections.clear();
        //если привидение следует за пакмана, то ищем путь к нему
        if (useTarget) {
            if (targetCol - col > 0) {
                requestDirections.add(desiredDirectionsMap[0]);
            } else if (targetCol - col < 0) {
                requestDirections.add(desiredDirectionsMap[2]);
            }
            if (targetRow - row > 0) {
                requestDirections.add(desiredDirectionsMap[1]);
            } else if (targetRow - row < 0) {
                requestDirections.add(desiredDirectionsMap[3]);
            }
        }
        //вибарием любое из предложенных направлений случайно
        if (requestDirections.size() > 0) {
            int selectedChaseDirection = (int) (requestDirections.size() * Math.random());
            requestDirection = requestDirections.get(selectedChaseDirection);
        }
        boolean movement = true;
        while (movement) {
            switch (instructionPointer) {
                case 0:
                    //проверяем прохождение в боковые грани
                    if (row == 14 && (prevDirection == 2 || prevDirection == 0)) {
                        controlOutsideMovement();
                    }
                    double angle = Math.toRadians(requestDirection * 90);
                    dirX = (int) Math.cos(angle);
                    dirY = (int) Math.sin(angle);
                    //если привидение может пройти и преследует цель, то текущему направлению присваиваем желаемое
                    if (useTarget && game.maze[row + dirY][col + dirX] == 0
                            && requestDirection != backwardDirections[prevDirection]) {
                        currentDir = requestDirection;
                    } else {
                        do {
                            //если нет, то случайно выбираем направление
                            currentDir = (int) (4 * Math.random());
                            angle = Math.toRadians(currentDir * 90);
                            dirX = (int) Math.cos(angle);
                            dirY = (int) Math.sin(angle);

                        } while (game.maze[row + dirY][col + dirX] == -1
                                || currentDir == backwardDirections[prevDirection]);
                        //пока привидение не найдет допустимое направление такое, чтобы он не меняло
                        //текущее направление призрака на противоположное
                    }
                    col += dirX;
                    row += dirY;
                    instructionPointer = 1;
                case 1:
                    //двигаем привидение в матричной системе
                    if (!moveToGridPosition(col, row, velocity)) {
                        prevDirection = currentDir;
                        instructionPointer = 0;
                    }
                    if (collisionWithTarget != null && checkCollisionWithPacman())
                        collisionWithTarget.run();
                    movement = false;
                    break;
            }
        }
    }

    private final GhostCaughtEvent ghostCaughtEvent = new GhostCaughtEvent();

    private class GhostCaughtEvent implements Runnable {
        @Override
        public void run() {
            game.ghostCaught(Ghost.this);
        }

    }

    private boolean checkFearTime() {//првоерка времени эффекта страха
        return System.currentTimeMillis() - fearStartTime <= 8000;
    }

    private void updateGhostDied() {
        boolean isGoingToCage = true;
        while (isGoingToCage) {
            switch (instructionPointer) {
                case 0:
                    //если пакман съел привидение, то ищем путь до клетки
                    pathFinder.find(col, row, 18, 11);
                    instructionPointer = 1;
                case 1:
                    if (!pathFinder.hasNext()) {
                        instructionPointer = 3;
                        break;
                    }
                    //если путь не пустой, то ведем привидение по нему к клетке
                    Point nextPosition = pathFinder.getNext();
                    col = nextPosition.x;
                    row = nextPosition.y;
                    instructionPointer = 2;
                case 2:
                    if (!moveToGridPosition(col, row, 2)) {
                        if (row == 11 && (col == 17 || col == 18)) {
                            instructionPointer = 3;
                            break;
                        }
                        instructionPointer = 1;
                        break;
                    }
                    isGoingToCage = false;
                    break;
                case 3:
                    //так как клетка для привидений ограждена единицами
                    //заводим привидение в нее вручную с помощью 3 и 4 кейса
                    if (!moveToTargetPosition(105, 110, 2)) {
                        instructionPointer = 4;
                        break;
                    }
                    isGoingToCage = false;
                    break;
                case 4:
                    if (!moveToTargetPosition(105, 134, 2)) {

                        instructionPointer = 5;
                        break;
                    }
                    isGoingToCage = false;
                    break;
                case 5:
                    setMode(Mode.CAGE);
                    instructionPointer = 4;
                    isGoingToCage = false;
                    break;
            }
        }
    }


    @Override
    public void updateGhostCaught() {
        //если привидение съедено
        if (mode == Mode.DIED) {
            updateGhostDied();
            updateAnimation();
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
                    //задержка 1.5 секунды после смерти пакмана
                    if (System.currentTimeMillis() - waitTime < 1500) {
                        cooldown = false;
                        break;
                    }
                    visible = false;
                    //помещаем привидениий в клетку
                    setMode(Mode.CAGE);
                    updateAnimation();
                    cooldown = false;
                    break;
            }
        }
        updateAnimation();
    }

    @Override
    public void updateLevelCleared() {
        boolean cooldownLevel = true;
        while (cooldownLevel) {
            switch (instructionPointer) {
                case 0:
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    //задержка при обновлении уровня
                    if (System.currentTimeMillis() - waitTime < 1500) {
                        cooldownLevel = false;
                        break;
                    }
                    visible = false;
                    setMode(Mode.CAGE);
                    updateAnimation();
                    cooldownLevel = false;
                    break;
            }
        }
    }

    private boolean checkCollisionWithPacman() {//проверка столкновения с пакманом
        pacman.updateCollider();
        updateCollider();
        return pacman.collider.intersects(collider);
    }

    @Override
    public void updateCollider() { //обновление тела привидения
        collider.setLocation((int) (xCoord + 4), (int) (yCoord + 4));
    }


    @Override
    public void updateMenu() {
        //движение привидений в меню
        int frameIndex = 0;
        xCoord = pacman.xCoord + 17 + 17 * type;
        yCoord = 200;
        if (pacman.currentDir == 0) {
            frameIndex = 8 + (int) (System.nanoTime() * 0.00000001) % 2;
        } else if (pacman.currentDir == 2) {
            frameIndex = 2 * pacman.currentDir + (int) (System.nanoTime() * 0.00000001) % 2;
        }
        frame = frames[frameIndex];
    }

    @Override
    public void stateChanged() {
        switch (game.getState()) {
            case MENU -> {
                updateMenu();
                visible = true;
            }
            case READY -> visible = false;
            case READY_NEXT_LIFE -> {
                setMode(Mode.CAGE);
                updateAnimation();
                Point initialPosition = initPositions[type];
                updatePosition(initialPosition.x, initialPosition.y);
                xCoord -= 4;
            }
            case PLAYING -> {
                if (mode != Mode.CAGE) instructionPointer = 0;
            }
            case PACMAN_DIED, LEVEL_CLEARED -> instructionPointer = 0;
        }
    }

    public void showAll() {
        visible = true;
    }

    public void hideAll() {
        visible = false;
    }

    public void startGhostFearMode() {
        fearStartTime = System.currentTimeMillis();
        isInFear = true;
    }

    public void died() {
        setMode(Mode.DIED);
    }
}


