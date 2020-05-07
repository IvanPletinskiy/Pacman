package dawist_o.pacman.gui;

import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame.State;
import dawist_o.pacman.pacmanData.PacmanGame;

public class Background extends PacmanActor {
    public Background(PacmanGame game) {
        super(game);
    }

    private int frameCount;

    @Override
    public void init() {
        //загружаем модельки игрового поля
        loadFrames("/resources/background_0.png", "/resources/background_1.png");
    }

    @Override
    public void updateLevelCleared() {
        boolean notCleared = true;
        while (notCleared) {
            switch (instructionPointer) {
                case 0:
                    frameCount = 0;
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    //задержка 1.5 секунды
                    if (System.currentTimeMillis() - waitTime < 1500) {
                        notCleared = false;
                        break;
                    }
                    instructionPointer = 2;
                case 2:
                    //меняем кадр карты
                    frame = frames[1];
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 3;
                case 3:
                    //задержка 0.2 секунды для моргания
                    if (System.currentTimeMillis() - waitTime < 200) {
                        notCleared = false;
                        break;
                    }
                    frame = frames[0];
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 4;
                case 4:
                    if (System.currentTimeMillis() - waitTime < 200) {
                        notCleared = false;
                        break;
                    }
                    //моргание в количетсве 5 раз
                    frameCount++;
                    if (frameCount < 5) {
                        instructionPointer = 2;
                        break;
                    }
                    //очистка уровня перед следующей игрой
                    game.broadcastMessage("hideAll");
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 5;
                case 5:
                    //следующий уровень
                    if (System.currentTimeMillis() - waitTime < 500) {
                        notCleared = false;
                        break;
                    }
                    visible = true;
                    game.nextLevel();
                    notCleared = false;
                    break;
            }
        }
    }

    @Override
    public void stateChanged() {
        //отоброжение фона в зависимости от состояния игры
        if (game.getState() == State.MENU)
            visible = false;
        else if (game.getState() == State.READY)
            visible = true;
        else if (game.getState() == State.LEVEL_CLEARED)
            instructionPointer = 0;
    }
}
