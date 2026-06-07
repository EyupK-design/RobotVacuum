package view;

import common.Constants;
import common.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;

// Canvas üzerine robot, şarj istasyonu ve hareket izini çizen yardımcı sınıf.
// Tüm metodlar statik olup GraphicsContext parametresi alır.
public class RobotDrawer {

    // Robotu daire + yön çizgisi olarak çizer. Yön, robotun baktığı yöne işaret eder.
    public static void drawRobot(GraphicsContext gc, int x, int y,
                                 Direction direction, int battery) {
        int px = x * Constants.CELL_SIZE;
        int py = y * Constants.CELL_SIZE;
        int s  = Constants.CELL_SIZE;

        gc.setFill(Color.web(Constants.COLOR_ROBOT));
        gc.fillOval(px + 4, py + 4, s - 8, s - 8);

        gc.setStroke(Color.web(Constants.COLOR_ROBOT_OUTLINE));
        gc.setLineWidth(2);
        gc.strokeOval(px + 4, py + 4, s - 8, s - 8);

        // Yön göstergesi: merkezden baktığı yöne doğru beyaz çizgi
        double cx = px + s / 2.0;
        double cy = py + s / 2.0;
        double r  = s / 3.5;
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeLine(cx, cy, cx + direction.getDx() * r, cy + direction.getDy() * r);
    }

    // Geçilen koordinatları çizgiyle birbirine bağlar. En az 2 nokta gerekir.
    public static void drawPath(GraphicsContext gc, List<int[]> path) {
        if (path == null || path.size() < 2) return;
        gc.setStroke(Color.web(Constants.COLOR_PATH));
        gc.setLineWidth(2.5);
        for (int i = 0; i < path.size() - 1; i++) {
            int[] a = path.get(i);
            int[] b = path.get(i + 1);
            double x1 = a[0] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
            double y1 = a[1] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
            double x2 = b[0] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
            double y2 = b[1] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
            gc.strokeLine(x1, y1, x2, y2);
        }
    }

    // Şarj istasyonunu sarı kare + iç çerçeve olarak çizer.
    public static void drawChargingStation(GraphicsContext gc, int x, int y) {
        int px = x * Constants.CELL_SIZE;
        int py = y * Constants.CELL_SIZE;
        int s  = Constants.CELL_SIZE;
        gc.setFill(Color.web(Constants.COLOR_CHARGING_STATION));
        gc.fillRect(px + 4, py + 4, s - 8, s - 8);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeRect(px + 10, py + 10, s - 20, s - 20);
    }
}
