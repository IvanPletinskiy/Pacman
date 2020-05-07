package dawist_o.pacman.gui;

import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame;

public class Ready extends PacmanActor {
    public Ready(PacmanGame game) {
        super(game);
    }

    @Override
    public void init() {
        //изображение "READY"
        loadFrames("/resources/ready.png");
        xCoord = 11 * 8;
        yCoord = 20 * 8;
    }

    @Override
    public void updateReady() {
        boolean readyTimer=true;
        while (readyTimer) {
            switch (instructionPointer) {
                case 0:
                    game.restoreCurrentFoodCount();
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    //задержка 2 секунды перед началом игры
                    if (System.currentTimeMillis() - waitTime < 2000){
                        readyTimer=false;
                        break;
                    }
                    game.setState(PacmanGame.State.READY_NEXT_LIFE);
                    readyTimer=false;
                    break;
            }
        }
    }

    @Override
    public void updateReadyNextLife() {
        boolean readyNextLifeTimer=true;
        while (readyNextLifeTimer) {
            switch (instructionPointer) {
                case 0:
                    game.broadcastMessage("showAll");
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    //задержка 2 секунды перед началом следующей жизни
                    if (System.currentTimeMillis() - waitTime < 2000) {
                        readyNextLifeTimer=false;
                        break;
                    }
                    game.setState(PacmanGame.State.PLAYING);
                    readyNextLifeTimer=false;
                    break;
            }
        }
    }


    @Override
    public void stateChanged() {
        visible = false;
        //не показывать если не в игре
        if (game.getState() == PacmanGame.State.READY
                || game.getState() == PacmanGame.State.READY_NEXT_LIFE) {
            visible = true;
            instructionPointer = 0;
        }
    }
}
