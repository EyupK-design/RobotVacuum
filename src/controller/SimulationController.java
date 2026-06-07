package controller;

import common.Constants;
import common.Direction;
import common.DirtType;
import common.EventLogger;
import common.Interfaces.ICell;
import common.Interfaces.IChargingStation;
import common.Interfaces.IPathFinder;
import common.Interfaces.IRobot;
import common.Interfaces.IRoom;
import common.Interfaces.ISimulationController;
import common.Interfaces.SimulationObserver;
import common.SimulationEvent;
import common.SimulationState;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import model.CleaningAlgorithm;
import model.PathFinder;

import java.util.List;

// Simülasyonun tüm iş mantığını yürütür. Model ile View arasındaki köprüdür.
// JavaFX Timeline döngüsüyle her tick'te robotu hareket ettirir ve temizlik yapar.
// ISimulationController arayüzünü implement eder. View bu arayüzü kullanır.
public class SimulationController implements ISimulationController {

    private final IRoom   room;
    private final IRobot  robot;

    // Observer listesi ve simülasyon anlık durum snapshot'ı
    private final ObserverManager observerManager = new ObserverManager();
    private final SimulationState state           = new SimulationState();

    // BFS yol bulma ve bulunan yolu adım adım takip etme
    private final IPathFinder pathFinder  = new PathFinder();
    private final PathFollower pathFollower = new PathFollower();

    private CleaningAlgorithm algorithm;
    private String algorithmName;

    private Timeline timeline;
    private boolean  running            = false;
    private boolean  paused             = false;
    private boolean  returningToStation = false;

    private double speed;
    private int    elapsedTicks  = 0;
    private String statusMessage = "Hazır";

    public SimulationController(IRoom room, IRobot robot) {
        this.room          = room;
        this.robot         = robot;
        this.algorithmName = Constants.ALGO_RANDOM;
        this.algorithm     = AlgorithmFactory.create(algorithmName);
        this.speed         = robot.getSpeed();
        refreshState();
    }

    // View ve GridView aynı room referansını kullanmalıdır.
    // Farklı referans kullanılırsa tıklanan engel ekranda görünmez.
    public IRoom  getRoom()  { return room;  }
    public IRobot getRobot() { return robot; }

    // ── Simülasyon kontrolü ───────────────────────────────────────

    @Override
    public void startSimulation() {
        if (running && !paused) return;
        running = true;
        paused  = false;
        buildTimeline();
        timeline.play();
        statusMessage = "Temizleniyor...";
        observerManager.notifySimulationStarted();
        log(SimulationEvent.SIMULATION_STARTED, null);
        refreshState();
    }

    @Override
    public void pauseSimulation() {
        if (!running || paused) return;
        paused = true;
        if (timeline != null) timeline.pause();
        statusMessage = "Duraklatıldı";
        observerManager.notifySimulationPaused();
        log(SimulationEvent.SIMULATION_PAUSED, null);
        refreshState();
    }

    @Override
    public void resetSimulation() {
        running            = false;
        paused             = false;
        returningToStation = false;
        elapsedTicks       = 0;
        if (timeline != null) timeline.stop();
        pathFollower.clear();
        room.reset();
        robot.reset(Constants.CHARGING_STATION_X, Constants.CHARGING_STATION_Y);
        algorithmName = Constants.ALGO_RANDOM;
        algorithm     = AlgorithmFactory.create(algorithmName);
        algorithm.reset();
        speed         = Constants.DEFAULT_SPEED;
        robot.setSpeed(speed);
        statusMessage = "Hazır";
        EventLogger.getInstance().clear();
        observerManager.notifySimulationReset();
        refreshState();
    }

    @Override
    public void returnToStation() {
        IChargingStation station = room.getChargingStation();

        // Zaten istasyondaysa direkt şarj başlat
        if (robot.getX() == station.getX() && robot.getY() == station.getY()) {
            arriveAtStation(station);
            refreshState();
            return;
        }

        List<int[]> path = pathFinder.findPath(
                room, robot.getX(), robot.getY(), station.getX(), station.getY());

        if (path.isEmpty()) {
            statusMessage = "İstasyona yol bulunamadı!";
            refreshState();
            return;
        }

        pathFollower.setPath(path);
        returningToStation = true;
        statusMessage      = "İstasyona dönülüyor...";
        log(SimulationEvent.RETURNING_TO_STATION, "kullanıcı isteği");

        // startSimulation() çağırmak yerine doğrudan timeline başlatılır.
        // Böylece running bayrağı ve observer bildirimleri karışmaz.
        if (!running || paused) {
            running = true;
            paused  = false;
            buildTimeline();
            timeline.play();
            observerManager.notifySimulationStarted();
        }
        refreshState();
    }

    // ── Kullanıcı eylemleri ───────────────────────────────────────

    @Override
    public void addDirt(int gridX, int gridY, DirtType type) {
        room.addDirt(gridX, gridY, type);
        log(SimulationEvent.DIRT_ADDED, "(" + gridX + "," + gridY + ") " + type);
        refreshState();
    }

    // Robotun üzerinde bulunduğu hücreye engel eklenemez
    @Override
    public void addObstacle(int gridX, int gridY) {
        if (gridX == robot.getX() && gridY == robot.getY()) return;
        room.addObstacle(gridX, gridY);
        log(SimulationEvent.OBSTACLE_ADDED, "(" + gridX + "," + gridY + ")");
        refreshState();
    }

    @Override
    public void removeObstacle(int gridX, int gridY) {
        room.removeObstacle(gridX, gridY);
        log(SimulationEvent.OBSTACLE_REMOVED, "(" + gridX + "," + gridY + ")");
        refreshState();
    }

    @Override
    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
        robot.setSpeed(newSpeed);
        // Çalışıyorsa timeline'ı yeni hızla yeniden inşa et
        if (running && !paused) {
            buildTimeline();
            timeline.play();
        }
        log(SimulationEvent.SPEED_CHANGED, newSpeed + "x");
        refreshState();
    }

    // Algoritma değiştirilince iç sayaçlar sıfırlanır. Spiral sayaçları taşınmaz.
    @Override
    public void setAlgorithm(String name) {
        this.algorithmName = name;
        this.algorithm     = AlgorithmFactory.create(name);
        this.algorithm.reset();
        log(SimulationEvent.ALGORITHM_CHANGED, name);
        refreshState();
    }

    @Override
    public void setBattery(int level) {
        int target  = Math.max(0, Math.min(Constants.MAX_BATTERY, level));
        int current = robot.getBattery();
        if (target < current) {
            robot.decreaseBattery(current - target);
        } else if (target > current) {
            boolean wasCharging = robot.isCharging();
            robot.startCharging();
            // Hedef seviyeye ulaşana kadar chargeTick döngüsüyle artır
            for (int i = 0; i < 100 && robot.getBattery() < target; i++)
                robot.chargeTick();
            if (!wasCharging) robot.stopCharging();
        }
        refreshState();
    }

    @Override public void addObserver(SimulationObserver o)    { observerManager.addObserver(o); }
    @Override public void removeObserver(SimulationObserver o) { observerManager.removeObserver(o); }
    @Override public SimulationState getState()  { return state;   }
    @Override public boolean isRunning()         { return running; }
    @Override public boolean isPaused()          { return paused;  }

    // ── Simülasyon döngüsü ────────────────────────────────────────

    // Timeline tick süresini hız çarpanına böler. Hız arttıkça döngü hızlanır.
    private void buildTimeline() {
        if (timeline != null) timeline.stop();
        double eff    = (speed <= 0) ? Constants.DEFAULT_SPEED : speed;
        double tickMs = Constants.SIMULATION_TICK_MS / eff;
        timeline = new Timeline(new KeyFrame(Duration.millis(tickMs), e -> tick()));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    // Her tick'te çalışan ana döngü. Sıralama önemlidir: hareket → varış → temizlik → batarya.
    private void tick() {
        if (!running || paused) return;
        elapsedTicks++;

        // 0. Şarj modu: batarya dolana kadar bekle, sonra temizliğe devam et
        if (robot.isCharging()) {
            robot.chargeTick();
            if (robot.getBattery() >= Constants.MAX_BATTERY) {
                robot.stopCharging();
                room.getChargingStation().setRobotPresent(false);
                returningToStation = false;
                statusMessage = "Şarj tamamlandı. Temizleniyor...";
                observerManager.notifyChargingComplete();
                log(SimulationEvent.CHARGING_COMPLETED, null);
            } else {
                statusMessage = "Şarj oluyor... %" + robot.getBattery();
                refreshState();
                return;
            }
        }

        // 1. Sonraki adımı belirle: istasyona dönüş modu veya temizleme algoritması
        int[] next;
        if (returningToStation) {
            if (pathFollower.isFollowing()) {
                next = pathFollower.nextStep();
                if (next != null) faceTowards(next[0], next[1]);
            } else {
                // Yol bitti ama henüz istasyona varılmadı, yeniden hesapla
                IChargingStation st = room.getChargingStation();
                List<int[]> rePath = pathFinder.findPath(
                        room, robot.getX(), robot.getY(), st.getX(), st.getY());
                if (!rePath.isEmpty()) {
                    pathFollower.setPath(rePath);
                    next = pathFollower.nextStep();
                    if (next != null) faceTowards(next[0], next[1]);
                } else {
                    returningToStation = false;
                    next = algorithm.getNextMove(robot, room);
                }
            }
        } else {
            next = algorithm.getNextMove(robot, room);
        }

        // 2. Hareket: isWalkable kontrolü algoritmanın döndürdüğü değeri doğrular
        if (next != null
                && (next[0] != robot.getX() || next[1] != robot.getY())
                && room.isWalkable(next[0], next[1])) {
            robot.move(next[0], next[1]);
            observerManager.notifyRobotMoved(robot.getX(), robot.getY(), robot.getDirection());
        }

        // 3. İstasyona varış kontrolü: hareket sonrası pozisyon karşılaştırılır
        IChargingStation station = room.getChargingStation();
        if (returningToStation
                && robot.getX() == station.getX()
                && robot.getY() == station.getY()) {
            arriveAtStation(station);
        }

        // 4. Mevcut hücreyi temizle (şarj sırasında yapılmaz)
        if (!robot.isCharging()) cleanCurrentCell();

        // 5. Batarya düşük kontrolü: otomatik dönüş tetiklenir
        if (!returningToStation && !robot.isCharging() && robot.isBatteryLow()) {
            beginReturnToStation(station);
        }

        // 6. Durum güncellemesi ve observer bildirimi
        refreshState();

        // 7. Tüm kirler bitti mi? Simülasyonu durdur.
        if (room.getDirtyCellCount() == 0 && !returningToStation && !robot.isCharging()) {
            running = false;
            if (timeline != null) timeline.stop();
            statusMessage = "Temizlik tamamlandı!";
            int totalSec = calcElapsedSeconds();
            observerManager.notifyCleaningComplete(totalSec);
            log(SimulationEvent.SIMULATION_COMPLETED, "süre=" + totalSec + "s");
            refreshState();
        }
    }

    // ── Yardımcı metotlar ─────────────────────────────────────────

    // Robot kirli hücredeyse her tick dirtProgress artar.
    // Eşik sayısına (cleanTicks) ulaşınca kir silinir ve batarya düşürülür.
    // Robot hücreyi terk edip geri gelse de progress sıfırlanmaz.
    private void cleanCurrentCell() {
        ICell cell = room.getCell(robot.getX(), robot.getY());
        if (cell == null || cell.isObstacle()) return;

        if (cell.hasDirt()) {
            DirtType type = cell.getDirtType();
            cell.incrementDirtProgress();

            if (cell.getDirtProgress() >= type.getCleanTicks()) {
                robot.decreaseBattery(type.getBatteryCost());
                cell.clearDirt();
                observerManager.notifyCellCleaned(robot.getX(), robot.getY(), type);
                log(SimulationEvent.CELL_CLEANED,
                        "(" + robot.getX() + "," + robot.getY() + ") " + type);
            }
        } else if (!cell.isCleaned()) {
            // Kirli olmayan ama ziyaret edilmemiş hücreyi temizlendi işaretle
            cell.setCleaned(true);
        }
    }

    private void beginReturnToStation(IChargingStation station) {
        List<int[]> path = pathFinder.findPath(
                room, robot.getX(), robot.getY(), station.getX(), station.getY());
        if (path.isEmpty()) return;
        pathFollower.setPath(path);
        returningToStation = true;
        statusMessage      = "Batarya düşük! İstasyona dönülüyor...";
        observerManager.notifyBatteryLow(robot.getBattery());
        log(SimulationEvent.BATTERY_LOW, "%" + robot.getBattery());
    }

    private void arriveAtStation(IChargingStation station) {
        station.setRobotPresent(true);
        robot.startCharging();
        returningToStation = false;
        pathFollower.clear();
        statusMessage = "İstasyona ulaşıldı. Şarj oluyor...";
        observerManager.notifyRobotReachedStation();
        log(SimulationEvent.REACHED_STATION, null);
    }

    // Robotu hedef hücreye doğru döndürür. BFS adımlarında yön güncellemek için kullanılır.
    private void faceTowards(int targetX, int targetY) {
        int dx = targetX - robot.getX();
        int dy = targetY - robot.getY();
        for (Direction d : Direction.values()) {
            if (d.getDx() == dx && d.getDy() == dy) {
                robot.setDirection(d);
                return;
            }
        }
    }

    private int calcElapsedSeconds() {
        double eff = (speed <= 0) ? Constants.DEFAULT_SPEED : speed;
        return (int) Math.round(elapsedTicks * Constants.SIMULATION_TICK_MS / eff / 1000.0);
    }

    private void log(SimulationEvent event, String detail) {
        EventLogger.getInstance().log(event, detail);
    }

    private void refreshState() {
        state.update(
                robot.getX(), robot.getY(), robot.getBattery(),
                robot.getDirection(), robot.isCharging(),
                room.getTotalWalkableCells(), room.getCleanedCellCount(),
                room.getDirtyCellCount(), running, paused,
                calcElapsedSeconds(), statusMessage, algorithmName,
                robot.getMovementPath()
        );
        observerManager.notifyStateChanged(state);
    }
}
