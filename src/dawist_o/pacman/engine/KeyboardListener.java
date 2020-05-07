package dawist_o.pacman.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardListener extends KeyAdapter {
    public static boolean[] keyPressed = new boolean[256];

    @Override
    public void keyPressed(KeyEvent e) {
        //позиции кода нажатой клавиши в массиве присваиваем true
        keyPressed[e.getKeyCode()] = true;
    }
    @Override
    public void keyReleased(KeyEvent e) {
        //позиции кода отпущенной клавиши в массиве присваиваем true
        keyPressed[e.getKeyCode()] = false;
    }
}
