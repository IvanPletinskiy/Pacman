package dawist_o.pacman.pacmanData;

import dawist_o.pacman.engine.GameObject;

public class PacmanActor extends GameObject<PacmanGame> {

    public PacmanActor(PacmanGame game) {
        super(game);
    }

    //в зависимости от состояние обновляем определенные элементы игры
    @Override
    public void update() {
        switch (game.getState()) {
            case INITIALIZING -> updateInitializing();
            case MENU -> updateMenu();
            case PACMAN_DIED -> updatePacmanDied();
            case PLAYING -> updatePlaying();
            case READY -> updateReady();
            case READY_NEXT_LIFE -> updateReadyNextLife();
            case GHOST_CAUGHT -> updateGhostCaught();
            case LEVEL_CLEARED -> updateLevelCleared();
            case GAME_OVER -> updateGameOver();
            case PREVIEW -> updatePreview();
        }
    }

    public void updatePreview() {

    }

    public void updateReadyNextLife() {

    }

    public void updateGameOver() {

    }

    public void updateGhostCaught() {

    }

    public void updateLevelCleared() {

    }

    public void updateReady() {

    }

    public void updatePlaying() {

    }

    public void updatePacmanDied() {

    }

    public void updateMenu() {

    }

    public void updateInitializing() {

    }

    public void stateChanged() {
    }
}
