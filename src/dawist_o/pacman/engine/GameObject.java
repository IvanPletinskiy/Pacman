package dawist_o.pacman.engine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameObject<T extends Game> {
    //константа благодаря которой можно отслеживать границы столкновения с объектом
    public static final boolean DRAW_COLLIDER = false;

    public T game;
    public double xCoord, yCoord;
    public boolean visible;
    public BufferedImage frame;
    public BufferedImage[] frames;
    public Rectangle collider;

    protected int instructionPointer;
    protected long waitTime;

    public GameObject(T game) {
        this.game = game;
    }

    public void init() {
    }

    public void update() {

    }

    public void draw(Graphics2D g) {
        if (!visible)
            return;
        //отрисовка модели объекта
        if (frame != null)
            g.drawImage(frame, (int) xCoord, (int) yCoord, frame.getWidth(), frame.getHeight(), null);
        if (DRAW_COLLIDER && collider != null) {
            updateCollider();
            g.setColor(Color.RED);
            g.draw(collider);
        }
    }

    protected void loadFrames(String... objFrames) {
        //загрузка кадров объекта
        try {
            frames = new BufferedImage[objFrames.length];
            for (int i = 0; i < objFrames.length; i++) {
                frames[i] = ImageIO.read(getClass().getResourceAsStream(objFrames[i]));
            }
            frame = frames[0];
        } catch (IOException e) {
            Logger.getLogger(GameObject.class.getName()).log(Level.SEVERE, null, e);
            System.exit(1);

        }
    }

    public void updateCollider() {
        if (collider != null)
            collider.setLocation((int) xCoord, (int) yCoord);
    }

}
