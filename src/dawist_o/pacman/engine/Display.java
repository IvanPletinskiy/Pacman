package dawist_o.pacman.engine;

import java.awt.*;
import java.awt.image.BufferStrategy;


public class Display extends Canvas {

    private Game game;
    private boolean isRunning;
    private BufferStrategy bs;

    public Display(Game game) {
        this.game = game;
        int screenX = (int) (game.screenSize.width * game.screenScale.getX());
        int screenY = (int) (game.screenSize.height * game.screenScale.getY());
        //устанавливаем размеры окна и добавляем взаимодействие с клавиатурой
        setPreferredSize(new Dimension(screenX, screenY));
        addKeyListener(new KeyboardListener());
    }

    //запускаем игру
    public void start() {
        if (isRunning)
            return;
        createBufferStrategy(3);
        bs = getBufferStrategy();
        game.init();
        isRunning = true;
        //создаем новый поток и запускаем его
        Thread thread = new Thread(new Loop());
        thread.start();
    }

    private class Loop implements Runnable {
        @Override
        public void run() {
            long desiredFrameRateTime = 1000 / 60; //желаемое количетсво кадров в секунду (60 fps)
            long currentTime = System.currentTimeMillis();
            long lastTime = currentTime - desiredFrameRateTime;
            long unprocessedTime = 0;        //необработанное время
            boolean needsRender = false;     //следует ли обновить кадры
            while (isRunning) {
                currentTime = System.currentTimeMillis();  //текущее время
                unprocessedTime += currentTime - lastTime; //время прошедшее без обработки
                lastTime = currentTime;
                //если необработанное время больше желаемого количества кадров, то обновляем все игровые объекты
                while (unprocessedTime >= desiredFrameRateTime) {
                    unprocessedTime -= desiredFrameRateTime;
                    update();
                    needsRender = true;
                }
                //рендерим графику
                if (needsRender) {
                    Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                    g.setBackground(Color.BLACK);
                    g.clearRect(0, 0, getWidth(), getHeight());
                    g.scale(game.screenScale.getX(), game.screenScale.getY());
                    draw(g);
                    g.dispose();
                    bs.show();
                    needsRender = false;
                } else {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    public void update() {
        game.update();
    }

    public void draw(Graphics2D g) {
        game.draw(g);
    }
}
