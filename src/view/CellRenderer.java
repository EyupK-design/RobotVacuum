package view;

import common.Constants;
import common.Interfaces.ICell;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// Tek bir hücreyi Canvas üzerine çizer. Model sınıflarını import etmez, ICell kullanır.
// Önce arka plan rengi belirlenir, sonra grid çizgisi çizilir.
public class CellRenderer {

    public static void render(GraphicsContext gc, ICell cell) {
        int x    = cell.getX() * Constants.CELL_SIZE;
        int y    = cell.getY() * Constants.CELL_SIZE;
        int size = Constants.CELL_SIZE;

        // Öncelik sırası: engel > kir > temizlendi > boş
        if (cell.isObstacle()) {
            gc.setFill(Color.web(Constants.COLOR_CELL_OBSTACLE));
        } else if (cell.hasDirt() && cell.getDirtType() != null) {
            gc.setFill(Color.web(cell.getDirtType().getColor()));
        } else if (cell.isCleaned()) {
            gc.setFill(Color.web(Constants.COLOR_CELL_CLEANED));
        } else {
            gc.setFill(Color.web(Constants.COLOR_CELL_EMPTY));
        }

        gc.fillRect(x, y, size, size);
        gc.setStroke(Color.web(Constants.COLOR_GRID_LINE));
        gc.setLineWidth(0.5);
        gc.strokeRect(x, y, size, size);
    }
}
