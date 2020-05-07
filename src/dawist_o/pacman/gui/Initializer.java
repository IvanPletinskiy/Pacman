package dawist_o.pacman.gui;

import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame.State;
import dawist_o.pacman.pacmanData.PacmanGame;

public class Initializer extends PacmanActor {

    public Initializer(PacmanGame game) {
        super(game);
    }

    @Override
    public void updateInitializing() {
        boolean timer = true;
        while (timer) {
            switch (instructionPointer) {
                case 0:
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    //задержка перед превью 1.5 секунды
                    if (System.currentTimeMillis() - waitTime < 1500) {
                        timer = false;
                        break;
                    }
                    instructionPointer = 2;
                case 2:
                    game.setState(State.PREVIEW);
                    timer = false;
                    break;
            }
        }
    }

}
