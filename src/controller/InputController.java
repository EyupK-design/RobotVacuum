package controller;

import common.Constants;
import common.DirtType;
import common.Interfaces.ISimulationController;

// Kullanıcı girdilerini (fare tıklama, buton) SimulationController metodlarına yönlendirir.
// Piksel koordinatını grid koordinatına çevirir. View bu sınıfı opsiyonel kullanabilir.
public class InputController {

    private final ISimulationController controller;

    public InputController(ISimulationController controller) {
        this.controller = controller;
    }

    // Piksel koordinatından grid hücresini hesaplar ve engel ekler veya kaldırır
    public void handleGridClick(double pixelX, double pixelY, boolean leftButton) {
        int gridX = (int) (pixelX / Constants.CELL_SIZE);
        int gridY = (int) (pixelY / Constants.CELL_SIZE);
        if (!isInside(gridX, gridY)) return;
        if (leftButton) controller.addObstacle(gridX, gridY);
        else            controller.removeObstacle(gridX, gridY);
    }

    public void handleAddDirt(double pixelX, double pixelY, DirtType type) {
        int gridX = (int) (pixelX / Constants.CELL_SIZE);
        int gridY = (int) (pixelY / Constants.CELL_SIZE);
        if (isInside(gridX, gridY)) controller.addDirt(gridX, gridY, type);
    }

    public void handleStart()           { controller.startSimulation(); }
    public void handlePause()           { controller.pauseSimulation(); }
    public void handleReset()           { controller.resetSimulation(); }
    public void handleReturnToStation() { controller.returnToStation(); }

    public void handleAlgorithmChange(String name)  { controller.setAlgorithm(name); }
    public void handleSpeedChange(double speed)     { controller.setSpeed(speed); }

    private boolean isInside(int gx, int gy) {
        return gx >= 0 && gx < Constants.GRID_COLS && gy >= 0 && gy < Constants.GRID_ROWS;
    }
}
