package dawist_o.pacman.gui;

import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame;

import java.awt.*;

public class PowerFood extends PacmanActor {
    public int row;
    public int col;
    private boolean eated;

    public PowerFood(PacmanGame game, int col, int row) {
        super(game);
        this.row = row;
        this.col = col;
    }

    @Override
    public void init() {
        //загружаем изображение еды в нужные точки
        loadFrames("/resources/powerBall.png");
        xCoord = col * 8 + 1 - 32;
        yCoord = (row + 3) * 8 + 1;
        eated = true;
        collider = new Rectangle(0, 0, 4, 4);
    }


    @Override
    public void updatePlaying() {
        //моргание точек
        visible = !eated && ((int) (System.nanoTime() * 0.0000000075) % 3) > 0;
        if (eated || game.getState() == PacmanGame.State.PACMAN_DIED)
            return;
        //если пакман жив, то проверяем местоположение пакмана и точек
        if (game.checkCollision(this, Pacman.class) != null) {
            visible = false;
            eated = true;
            //если точка съедена, то отнимаем количество еды на карте,
            //добавляем очки и активируем режим страха у призраков
            game.currentFoodCount--;
            game.addScore(50);
            game.startGhostFearMode();
        }
    }

    @Override
    public void stateChanged() {
        //если мы не в игре, то не отображаем точки
        if (game.getState() == PacmanGame.State.MENU
                || game.getState() == PacmanGame.State.LEVEL_CLEARED
                || game.getState() == PacmanGame.State.GAME_OVER) {
            visible = false;
            eated = true;
        } else if (game.getState() == PacmanGame.State.READY) {
            visible = true;
            eated = false;
        }
    }
}
