package dawist_o.pacman.gui;

import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame;
import dawist_o.pacman.pacmanData.PacmanGame.State;

import java.awt.*;

public class HeadUpDisplay extends PacmanActor {

    public HeadUpDisplay(PacmanGame game) {
        super(game);
    }

    @Override
    public void init() {
        //загружаем жизни пакмана
        loadFrames("/resources/pacman_life.png");
    }

    @Override
    public void draw(Graphics2D g) {
        //отображаем текущий и максимальный счет,а также количетсво жизней пакмана
        if (!visible)
            return;
        game.drawText(g, "SCORE", 10, 1);
        game.drawText(g, game.getScore(), 10, 10);
        game.drawText(g, "HIGH SCORE", 78, 1);
        game.drawText(g, game.getHighScore(), 78, 10);
        game.drawText(g, "LIVES:", 10, 274);
        for (int live = 0; live < game.lives; live++) {
            g.drawImage(frame, 57 + 20 * live, 272, null);
        }
    }

    @Override
    public void stateChanged() {
        //не отображаем интерфейс на стадии инициализации и превью
        visible = game.getState() != State.INITIALIZING
                && game.getState() != State.PREVIEW;
    }
}
