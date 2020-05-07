package dawist_o.pacman.gui;

import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame;

public class GameOver extends PacmanActor {
    public GameOver(PacmanGame game) {
        super(game);
    }

    @Override
    public void init() {
        //загрузка надписи "GAME OVER" по нужным координатам
        xCoord = 77;
        yCoord = 160;
        loadFrames("/resources/gameover.png");
    }

    @Override
    public void updateGameOver() {
        boolean isInGame = true;
        while (isInGame) {
            switch (instructionPointer) {
                case 0:
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    //задержка 3 секунды после смерти и возвращение в меню
                    if (System.currentTimeMillis() - waitTime < 2500) {
                        isInGame = false;
                        break;
                    }
                    game.returnToTitle();
                    isInGame = false;
                    break;
            }
        }
    }

    @Override
    public void stateChanged() {
        //если прогирали, то отображаем надпись
        visible = false;
        if (game.getState() == PacmanGame.State.GAME_OVER) {
            visible = true;
            instructionPointer = 0;
        }
    }
}
