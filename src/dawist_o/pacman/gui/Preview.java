package dawist_o.pacman.gui;

import dawist_o.pacman.pacmanData.PacmanActor;
import dawist_o.pacman.pacmanData.PacmanGame;
import dawist_o.pacman.pacmanData.PacmanGame.State;

import java.awt.*;

public class Preview extends PacmanActor {
    private final String text = "dawist_o";
    private int textIndex;

    public Preview(PacmanGame game) {
        super(game);
    }

    @Override
    public void updatePreview() {
        boolean previewTimer = true;
        while (previewTimer) {
            switch (instructionPointer) {
                case 0:
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    //задержка перед поялвением последующей буквы
                    if (System.currentTimeMillis() - waitTime < 100) {
                        previewTimer = false;
                        break;
                    }
                    textIndex++;
                    if (textIndex < text.length()) {
                        instructionPointer = 0;
                        previewTimer = false;
                        break;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 2;
                case 2:
                    //времяя показа превью полностью
                    if (System.currentTimeMillis() - waitTime < 2000) {
                        previewTimer = false;
                        break;
                    }
                    visible = false;
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 3;
                case 3:
                    //задержка перед переходом в меню игры
                    if (System.currentTimeMillis() - waitTime < 1500) {
                        previewTimer = false;
                        break;
                    }
                    game.setState(State.MENU);
                    previewTimer = false;
                    break;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (visible)
            game.drawText(g, text.substring(0, textIndex), 83, 130);
    }

    @Override
    public void stateChanged() {
        visible = false;
        //если превью, то делаем текст видимым
        if (game.state == State.PREVIEW) {
            visible = true;
            textIndex = 0;
        }
    }

}
