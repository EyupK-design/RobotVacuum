package controller;

import common.Direction;
import common.DirtType;
import common.Interfaces.SimulationObserver;
import common.SimulationState;

import java.util.ArrayList;
import java.util.List;

// Observer listesini yönetir ve olayları tüm dinleyicilere iletir.
// Controller doğrudan liste yönetmek yerine bu sınıfı kullanır.
public class ObserverManager {

    private final List<SimulationObserver> observers = new ArrayList<>();

    public void addObserver(SimulationObserver o) {
        if (o != null && !observers.contains(o)) observers.add(o);
    }

    public void removeObserver(SimulationObserver o) { observers.remove(o); }

    public void notifyStateChanged(SimulationState state) {
        for (SimulationObserver o : observers) o.onStateChanged(state);
    }

    public void notifyRobotMoved(int x, int y, Direction dir) {
        for (SimulationObserver o : observers) o.onRobotMoved(x, y, dir);
    }

    public void notifyCellCleaned(int x, int y, DirtType type) {
        for (SimulationObserver o : observers) o.onCellCleaned(x, y, type);
    }

    public void notifyBatteryLow(int level) {
        for (SimulationObserver o : observers) o.onBatteryLow(level);
    }

    public void notifyRobotReachedStation() {
        for (SimulationObserver o : observers) o.onRobotReachedStation();
    }

    public void notifyChargingComplete() {
        for (SimulationObserver o : observers) o.onChargingComplete();
    }

    public void notifySimulationStarted() {
        for (SimulationObserver o : observers) o.onSimulationStarted();
    }

    public void notifySimulationPaused() {
        for (SimulationObserver o : observers) o.onSimulationPaused();
    }

    public void notifySimulationReset() {
        for (SimulationObserver o : observers) o.onSimulationReset();
    }

    public void notifyCleaningComplete(int totalSeconds) {
        for (SimulationObserver o : observers) o.onCleaningComplete(totalSeconds);
    }
}
