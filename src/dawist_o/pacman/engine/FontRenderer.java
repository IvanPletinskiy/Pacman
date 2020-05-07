package dawist_o.pacman.engine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FontRenderer {
    public BufferedImage fontImage;
    public BufferedImage[] letters;

    public int letterWidth;
    public int letterHeight;

    public FontRenderer(String fontLetter, int columns, int rows) {
        loadFont(fontLetter, columns, rows);
    }

    public void loadFont(String fileName, int columns, int rows) {
        try {
            fontImage = ImageIO.read(getClass().getResourceAsStream(fileName));
            loadFont(fontImage, columns, rows);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFont(BufferedImage image, int columns, int rows) {
        //массив символов
        letters = new BufferedImage[columns * rows];
        //размеры каждого символа
        letterHeight = fontImage.getHeight() / rows;
        letterWidth = fontImage.getWidth() / columns;
        //загружаем каждую символы в массив
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                letters[i * rows + j] = new BufferedImage(letterWidth, letterHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D letterG = (Graphics2D) letters[i * rows + j].getGraphics();
                letterG.drawImage(fontImage, 0, 0, letterWidth, letterHeight
                        , j * letterWidth, i * letterHeight
                        , j * letterWidth + letterWidth,
                        i * letterHeight + letterHeight, null);
            }
        }
    }

    public void drawText(Graphics2D letterG, String text, int x, int y) {
        if (letters == null)
            return;
        int pointerX = 0;
        int pointerY = 0;
        // отрисовываем текст составляя его из массива символов
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == (int) '\n') {
                pointerY += letterHeight;
                pointerX = 0;
            } else {
                letterG.drawImage(letters[text.charAt(i)], pointerX + x, pointerY + y + 1, null);
                pointerX += letterWidth;
            }
        }
    }
}
