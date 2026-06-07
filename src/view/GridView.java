package view;

import common.Constants;
import common.Direction;
import common.DirtType;
import common.Interfaces;
import common.SimulationState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

// Odayı Canvas üzerine çizen View bileşeni. SimulationObserver olarak kayıtlıdır.
// Fare tıklamalarını yakalayıp Controller'a iletir. Animasyonları da burada yönetir.
public class GridView extends Canvas implements Interfaces.SimulationObserver {

    private final Interfaces.IRoom                 room;
    private final Interfaces.ISimulationController controller;
    private final ControlPanel                     controlPanel;

    // Son alınan SimulationState. onRobotMoved() gibi partial güncellemelerde kullanılır.
    private SimulationState lastState = null;

    // Temizleme animasyonu değişkenleri. Kir silinince daire küçülerek kaybolur.
    private double   animRadius  = 0;
    private int      animCellX   = -1;
    private int      animCellY   = -1;
    private Timeline animTimeline = null;

    public GridView(Interfaces.IRoom room,
                    Interfaces.ISimulationController controller,
                    ControlPanel controlPanel) {
        super(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT);
        this.room         = room;
        this.controller   = controller;
        this.controlPanel = controlPanel;
        redraw(null);
        setupMouse();
    }

    // Fare sol tıkı: seçili araca göre kir veya engel ekler.
    // Fare sağ tıkı: her zaman engeli veya kiri siler.
    private void setupMouse() {
        setOnMouseClicked(event -> {
            int gx = (int) (event.getX() / Constants.CELL_SIZE);
            int gy = (int) (event.getY() / Constants.CELL_SIZE);
            if (gx < 0 || gx >= Constants.GRID_COLS || gy < 0 || gy >= Constants.GRID_ROWS) return;

            if (event.getButton() == MouseButton.SECONDARY) {
                controller.removeObstacle(gx, gy);
            } else if (event.getButton() == MouseButton.PRIMARY) {
                switch (controlPanel.getSelectedTool()) {
                    case "OBSTACLE": controller.addObstacle(gx, gy);                     break;
                    case "DUST":     controller.addDirt(gx, gy, DirtType.DUST);    break;
                    case "LIQUID":   controller.addDirt(gx, gy, DirtType.LIQUID);  break;
                    case "STAIN":    controller.addDirt(gx, gy, DirtType.STAIN);   break;
                }
            }
        });
    }

    // Canvas'ı katmanlı olarak çizer: hücreler > istasyon > animasyon > yol > robot.
    private void redraw(SimulationState state) {
        GraphicsContext gc = getGraphicsContext2D();

        // 1. Tüm hücreler
        for (int y = 0; y < room.getRows(); y++)
            for (int x = 0; x < room.getCols(); x++) {
                Interfaces.ICell cell = room.getCell(x, y);
                if (cell != null) CellRenderer.render(gc, cell);
            }

        // 2. Şarj istasyonu
        Interfaces.IChargingStation st = room.getChargingStation();
        if (st != null) RobotDrawer.drawChargingStation(gc, st.getX(), st.getY());

        // 3. Temizleme animasyonu (aktifse): yarı saydam beyaz daire küçülerek kaybolur
        if (animCellX >= 0 && animCellY >= 0 && animRadius > 0) {
            gc.setFill(Color.rgb(255, 255, 255, 0.65));
            double cx = animCellX * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
            double cy = animCellY * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
            gc.fillOval(cx - animRadius, cy - animRadius, animRadius * 2, animRadius * 2);
        }

        // 4. Yol izi ve robot (state yoksa çizilmez)
        if (state != null) {
            RobotDrawer.drawPath(gc, state.getMovementPath());
            RobotDrawer.drawRobot(gc, state.getRobotX(), state.getRobotY(),
                    state.getDirection(), state.getBattery());
        }
    }

    // ── Observer metotları ────────────────────────────────────────

    @Override
    public void onStateChanged(SimulationState state) {
        this.lastState = state;
        Platform.runLater(() -> redraw(state));
    }

    @Override
    public void onRobotMoved(int x, int y, Direction d) {
        Platform.runLater(() -> redraw(lastState));
    }

    // Kir silinince daire animasyonu başlatılır. Önceki animasyon varsa durdurulur.
    @Override
    public void onCellCleaned(int x, int y, DirtType dirtType) {
        Platform.runLater(() -> {
            if (animTimeline != null) animTimeline.stop();
            animCellX  = x;
            animCellY  = y;
            animRadius = Constants.CELL_SIZE / 2.0;

            animTimeline = new Timeline(new KeyFrame(Duration.millis(30), ev -> {
                animRadius -= 2.5;
                redraw(lastState);
                if (animRadius <= 0) {
                    animTimeline.stop();
                    animCellX = -1;
                    animCellY = -1;
                    redraw(lastState);
                }
            }));
            animTimeline.setCycleCount(Timeline.INDEFINITE);
            animTimeline.play();
        });
    }

    @Override
    public void onSimulationReset() {
        this.lastState = null;
        Platform.runLater(() -> {
            if (animTimeline != null) { animTimeline.stop(); animTimeline = null; }
            animCellX = -1;
            animCellY = -1;
            redraw(null);
        });
    }

    @Override public void onBatteryLow(int b)         {}
    @Override public void onRobotReachedStation()     {}
    @Override public void onChargingComplete()        {}
    @Override public void onSimulationStarted()       {}
    @Override public void onSimulationPaused()        {}
    @Override public void onCleaningComplete(int s)   {}
}
