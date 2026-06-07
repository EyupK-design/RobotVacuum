package common;

import java.util.List;

// Projenin tüm arayüz sözleşmelerini tek dosyada toplar.
// Model sınıfları bu arayüzleri implement eder. View ve Controller bu tipleri kullanır.
public final class Interfaces {

    private Interfaces() {}

    // Robot'un View ve Controller'a sunduğu sözleşme
    public interface IRobot {
        int         getX();
        int         getY();
        int         getBattery();
        Direction   getDirection();
        boolean     isBatteryLow();
        boolean     isCharging();
        double      getSpeed();
        List<int[]> getMovementPath();

        void move(int newX, int newY);
        void setDirection(Direction d);
        void decreaseBattery(int amount);
        void startCharging();
        void stopCharging();
        void chargeTick();
        void setSpeed(double speed);
        void reset(int startX, int startY);
    }

    // Odanın View ve Controller'a sunduğu sözleşme
    public interface IRoom {
        ICell            getCell(int x, int y);
        int              getRows();
        int              getCols();
        IChargingStation getChargingStation();

        boolean isValidPosition(int x, int y);
        boolean isWalkable(int x, int y);

        int getTotalWalkableCells();
        int getCleanedCellCount();
        int getDirtyCellCount();

        void addDirt(int x, int y, DirtType type);
        void addObstacle(int x, int y);
        void removeObstacle(int x, int y);
        void reset();
    }

    // Tek bir hücrenin View ve Controller'a sunduğu sözleşme
    public interface ICell {
        int      getX();
        int      getY();
        boolean  isObstacle();
        boolean  isCleaned();
        boolean  hasDirt();
        DirtType getDirtType();
        int      getDirtProgress();

        void setObstacle(boolean obstacle);
        void setDirt(DirtType type);
        void clearDirt();
        void setCleaned(boolean cleaned);
        void incrementDirtProgress();
    }

    // Şarj istasyonunun sözleşmesi
    public interface IChargingStation {
        int     getX();
        int     getY();
        boolean isRobotPresent();
        void    setRobotPresent(boolean present);
    }

    // View bileşenlerinin Controller olaylarını dinlediği gözlemci arayüzü
    public interface SimulationObserver {
        void onStateChanged(SimulationState state);
        void onRobotMoved(int newX, int newY, Direction direction);
        void onCellCleaned(int x, int y, DirtType dirtType);
        void onBatteryLow(int batteryLevel);
        void onRobotReachedStation();
        void onChargingComplete();
        void onSimulationStarted();
        void onSimulationPaused();
        void onSimulationReset();
        void onCleaningComplete(int totalSeconds);
    }

    // View'ın Controller'a yapabileceği çağrıların sözleşmesi
    public interface ISimulationController {
        void startSimulation();
        void pauseSimulation();
        void resetSimulation();
        void returnToStation();

        void addDirt(int gridX, int gridY, DirtType type);
        void addObstacle(int gridX, int gridY);
        void removeObstacle(int gridX, int gridY);
        void setSpeed(double speed);
        void setAlgorithm(String algorithmName);
        void setBattery(int level);

        void addObserver(SimulationObserver observer);
        void removeObserver(SimulationObserver observer);

        SimulationState getState();
        boolean isRunning();
        boolean isPaused();
    }

    // PathFinder algoritmaları için sözleşme. Controller bu arayüzü kullanır.
    public interface IPathFinder {
        List<int[]> findPath(IRoom room, int startX, int startY, int goalX, int goalY);
    }
}
