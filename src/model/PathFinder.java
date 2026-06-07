package model;

import common.Constants;
import common.Direction;
import common.Interfaces.IPathFinder;
import common.Interfaces.IRoom;

import java.util.*;

// BFS (Breadth-First Search) algoritmasıyla en kısa yolu bulan sınıf.
// Başlangıçtan hedefe giden koordinat listesini döndürür. Yol yoksa boş liste.
public class PathFinder implements IPathFinder {

    @Override
    public List<int[]> findPath(IRoom room, int startX, int startY, int goalX, int goalY) {
        if (startX == goalX && startY == goalY) return new ArrayList<>();

        int rows = room.getRows();
        int cols = room.getCols();

        boolean[][] visited = new boolean[rows][cols];

        // parent[y][x] = bu hücreye nereden gelindiği bilgisi. Geri izleme için kullanılır.
        int[][][] parent = new int[rows][cols][2];

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startY][startX] = true;

        boolean found = false;
        int steps = 0;

        while (!queue.isEmpty() && steps < Constants.MAX_PATH_STEPS) {
            int[] cur = queue.poll();
            int cx = cur[0];
            int cy = cur[1];

            if (cx == goalX && cy == goalY) { found = true; break; }

            for (Direction dir : Direction.values()) {
                int nx = cx + dir.getDx();
                int ny = cy + dir.getDy();

                // İstasyon hedefi her zaman geçerlidir, diğer engeller kontrol edilir
                boolean isGoal = (nx == goalX && ny == goalY);
                if ((isGoal || room.isWalkable(nx, ny)) && !visited[ny][nx]) {
                    visited[ny][nx]    = true;
                    parent[ny][nx]     = new int[]{cx, cy};
                    queue.add(new int[]{nx, ny});
                }
            }
            steps++;
        }

        if (!found) return new ArrayList<>();

        // Hedeften başlangıca geriye doğru yolu oluştur, sonra tersine çevir
        List<int[]> path = new ArrayList<>();
        int cx = goalX;
        int cy = goalY;
        while (cx != startX || cy != startY) {
            path.add(new int[]{cx, cy});
            int[] p = parent[cy][cx];
            cx = p[0];
            cy = p[1];
        }
        Collections.reverse(path);
        return path;
    }
}
