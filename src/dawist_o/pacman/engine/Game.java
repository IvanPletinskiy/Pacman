package dawist_o.pacman.engine;

import java.awt.*;
import java.awt.geom.Point2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Game {
    public Dimension screenSize;
    public Point2D screenScale;
    public List<GameObject<?>> gameObjects = new ArrayList<>();
    public FontRenderer fontRenderer = new FontRenderer("/resources/font8x8.png", 16, 16);

    public void init() {
    }

    public void update() {
        //обновление всех игровых объектов
        for (GameObject<?> object : gameObjects)
            object.update();
    }

    public void draw(Graphics2D g) {
        //отрисовка графики
        for (GameObject<?> object : gameObjects)
            object.draw(g);
    }

    public <T> T checkCollision(GameObject<?> a1, Class<T> type) {
        //проверка столкновения объектов
        a1.updateCollider();
        for (GameObject<?> a2 : gameObjects) {
            a2.updateCollider();
            if (a1 != a2
                    && type.isInstance(a2)
                    && a1.collider != null && a2.collider != null
                    && a1.visible && a2.visible
                    && a2.collider.intersects(a1.collider)) {
                return type.cast(a2);
            }
        }
        return null;
    }

    public void broadcastMessage(String message) {
        //поиск метода по названию и его выполнение
        for (GameObject object : gameObjects) {
            try {
                Method method = object.getClass().getMethod(message);
                method.invoke(object);
            } catch (Exception ignored) {
            }
        }
    }

    public void drawText(Graphics2D g, String text, int x, int y) {
        //отрисовка текста
        fontRenderer.drawText(g, text, x, y);
    }
}
