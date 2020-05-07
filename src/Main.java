import dawist_o.pacman.engine.Display;
import dawist_o.pacman.engine.Game;
import dawist_o.pacman.pacmanData.PacmanGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //сама игра
                Game game = new PacmanGame();
                //дисплэй который будет отображать происходящее
                Display view=new Display(game);
                JFrame frame=new JFrame();
                //заголовок
                frame.setTitle("Pacman");
                //иконка приложения
                loadIconImage(frame);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(view);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setResizable(false);
                view.requestFocus();
                view.start();
            }
        });
    }

    private static void  loadIconImage(JFrame frame){
        try {
            frame.setIconImage(ImageIO.read(Main.class.getResourceAsStream("/resources/pacman_icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
