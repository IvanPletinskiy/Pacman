package dawist_o.pacman.engine;

import java.util.ArrayDeque;
import java.util.Deque;
import java.awt.Point;

public class PathFinder {
    public int[][] map;
    public Deque<Point> path = new ArrayDeque<>();

    public PathFinder(int[][] maze) {
        //копируем исходную карту в map
        map = new int[maze.length][maze[0].length];
        for (int i = 0; i < map.length; i++) {
            System.arraycopy(maze[i], 0, map[i], 0, map[0].length);
        }
    }

    private void clearMap() {
        //подготавливаем map оставляя только стены(-1) и пустые клетки(0)
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0)
                    map[i][j] = 0;
            }
        }
    }

    private final int[] neighborsCells = {1, 0, -1, 0, 0, 1, 0, -1};

    public void find(int sourceX, int sourceY, int destineX, int destineY) {
        path.clear();
        clearMap();
        int score = 1;
        map[destineY][destineX] = score; //желаемое положение отмечено единицей
        boolean isFinding = true;
        while (isFinding) {
            boolean isAtLeastOneFounded = false;
            for (int y = 0; y < map.length; y++) {
                for (int x = 0; x < map[0].length; x++) {
                    //если дошли до желаемой точки
                    if (getMapScore(x, y) == score) {
                        isAtLeastOneFounded = true;
                        //проверяем все возможные варианты прохода
                        boolean isPathNotFounded = true;
                        for (int n = 0; n < neighborsCells.length && isPathNotFounded; n += 2) {
                            int dX = x + neighborsCells[n];
                            int dY = y + neighborsCells[n + 1];
                            if (getMapScore(dX, dY) == 0) {//если можно пройти, то клетка = score+1
                                map[dY][dX] = score + 1;
                                if (dX == sourceX && dY == sourceY) {
                                    //когда дошли до начала, заполняем путь
                                    fillPath(score, dX, dY);
                                    isFinding = false;
                                    isPathNotFounded = false;
                                }
                            }
                        }
                    }
                }
            }
            //если не надо искать путь
            if (!isAtLeastOneFounded) {
                isFinding = false;
            }
            //если не дошли до начала, то инкрементим метку(score)
            score++;
        }
    }

    private int getMapScore(int x, int y) {
        if (x < 0 || x > map[0].length - 1 || y < 0 || y > map.length - 1) {
            return -1;
        }
        return map[y][x];
    }

    private void fillPath(int score, int dX, int dY) {
        int directionIndex = 0;
        while (score > 0) {
            //с помощью score "декодируем" направление и заполняем path точками, которые следует пройти на пути
            int stepX = neighborsCells[directionIndex];
            int stepY = neighborsCells[directionIndex + 1];
            if (getMapScore(dX + stepX, dY + stepY) == score) {
                directionIndex = 0;
                path.add(new Point(dX += stepX, dY += stepY));
                score--;
            } else
                directionIndex += 2;
        }
    }

    public boolean hasNext() {
        return !path.isEmpty();
    }

    public Point getNext() {
        //получение следующей точки
        if (path.isEmpty())
            return null;
        return path.pop();
    }
}
