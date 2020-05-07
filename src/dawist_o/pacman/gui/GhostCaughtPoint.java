package dawist_o.pacman.gui;

import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame.State;
import dawist_o.pacman.pacmanData.PacmanGame;

import java.awt.*;

public class GhostCaughtPoint extends PacmanActor {

    private final Pacman pacman;

    public GhostCaughtPoint(PacmanGame game, Pacman pacman) {
        super(game);
        this.pacman = pacman;
    }

    @Override
    public void init() {
        //загрузка очков при поимке призраков
        loadFrames("/resources/point_0.png", "/resources/point_1.png"
                , "/resources/point_2.png", "/resources/point_3.png");
        collider = new Rectangle(0, 0, 4, 4);
    }

    private void updatePos(int column, int row) {
        yCoord = (row + 3) * 8 + 1;
        xCoord = column * 8 - 4 - 32;
    }

    @Override
    public void updateGhostCaught() {
        boolean notCuaght = true;
        while (notCuaght) {
            switch (instructionPointer) {
                case 0:
                    //эффект поимки призрака и добавление очков
                    updatePos(game.caughtGhost.col, game.caughtGhost.row);
                    pacman.visible = false;
                    game.caughtGhost.visible = false;
                    int scoreFrame = game.currentGhostCaughtScore;
                    frame = frames[scoreFrame];
                    game.addScore(game.caughtGhostScores[scoreFrame]);
                    game.currentGhostCaughtScore++;
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    //остановка игры на пол-секунды при поимке призрака
                    if (System.currentTimeMillis() - waitTime < 500) {
                        notCuaght = false;
                        break;
                    }
                    pacman.visible = true;
                    pacman.updatePos();
                    pacman.xCoord += 4;
                    game.caughtGhost.visible = true;
                    game.caughtGhost.updatePosition();
                    game.caughtGhost.died();
                    game.setState(State.PLAYING);
                    notCuaght = false;
                    break;
            }
        }
    }

    @Override
    public void stateChanged() {
        visible = false;
        //если призрак пойман, то показываем иконку очков
        if (game.getState() == State.GHOST_CAUGHT) {
            visible = true;
            instructionPointer = 0;
        }
    }

    public void hideAll() {
        visible = false;
    }
}
