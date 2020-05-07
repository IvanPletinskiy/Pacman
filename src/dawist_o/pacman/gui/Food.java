package dawist_o.pacman.gui;

import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame;

import java.awt.*;

public class Food extends PacmanActor {
    public int row;
    public int col;

    public Food(PacmanGame game, int col, int row) {
        super(game);
        this.row = row;
        this.col = col;
    }

    @Override
    public void init() {
        //загружаем модельки еды и расставляем их на поле в нужные точки
        loadFrames("/resources/food.png");
        xCoord = col * 8 + 3 - 32;
        yCoord = (row + 3) * 8 + 3;
        collider = new Rectangle(0, 0, 2, 2);
    }

    @Override
    public void updatePlaying() {
        //проверяем положение пакмана и еды
        if (game.checkCollision(this, Pacman.class) != null) {
            //если они пересекаются, то съедаем точку
            visible = false;
            game.currentFoodCount--;
            game.addScore(10);
        }
    }

    @Override
    public void stateChanged() {
        //если мы в меню, то не отображаем еду
        if (game.getState() == PacmanGame.State.MENU || game.getState() == PacmanGame.State.GAME_OVER)
            visible = false;
        else if (game.getState() == PacmanGame.State.READY)
            visible = true;
    }
}
