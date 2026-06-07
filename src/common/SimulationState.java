package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Simülasyonun anlık durumunu tutan snapshot sınıfı.
// Controller her tick'te update() ile günceller. View sadece getter'ları okur.
public class SimulationState {

    private int       robotX, robotY;
    private int       battery;
    private Direction direction;
    private boolean   isCharging;
    private int       totalCells;
    private int       cleanedCells;
    private int       dirtyCells;
    private boolean   isRunning;
    private boolean   isPaused;
    private int       elapsedSeconds;
    private String    statusMessage;
    private String    activeAlgorithm;
    private List<int[]> movementPath;

    public SimulationState() {
        this.robotX          = Constants.CHARGING_STATION_X;
        this.robotY          = Constants.CHARGING_STATION_Y;
        this.battery         = Constants.MAX_BATTERY;
        this.direction       = Direction.EAST;
        this.isCharging      = false;
        this.totalCells      = 0;
        this.cleanedCells    = 0;
        this.dirtyCells      = 0;
        this.isRunning       = false;
        this.isPaused        = false;
        this.elapsedSeconds  = 0;
        this.statusMessage   = "Hazır";
        this.activeAlgorithm = Constants.ALGO_RANDOM;
        this.movementPath    = new ArrayList<>();
    }

    // Controller bu metodu her tick'te çağırır. Dışarıdan doğrudan erişilemez.
    public void update(int robotX, int robotY, int battery, Direction direction,
                       boolean isCharging, int totalCells, int cleanedCells,
                       int dirtyCells, boolean isRunning, boolean isPaused,
                       int elapsedSeconds, String statusMessage,
                       String activeAlgorithm, List<int[]> movementPath) {
        this.robotX          = robotX;
        this.robotY          = robotY;
        this.battery         = battery;
        this.direction       = direction;
        this.isCharging      = isCharging;
        this.totalCells      = totalCells;
        this.cleanedCells    = cleanedCells;
        this.dirtyCells      = dirtyCells;
        this.isRunning       = isRunning;
        this.isPaused        = isPaused;
        this.elapsedSeconds  = elapsedSeconds;
        this.statusMessage   = statusMessage;
        this.activeAlgorithm = activeAlgorithm;
        this.movementPath    = new ArrayList<>(movementPath);
    }

    // Temizlenmiş alan yüzdesi. View StatusBar ve ControlPanel'de gösterir.
    public double getCleanedPercentage() {
        if (totalCells == 0) return 0.0;
        return (cleanedCells * 100.0) / totalCells;
    }

    public double getDirtyPercentage() {
        if (totalCells == 0) return 0.0;
        return (dirtyCells * 100.0) / totalCells;
    }

    // Geçen süreyi MM:SS formatında döndürür. StatusBar'da gösterilir.
    public String getFormattedTime() {
        int m = elapsedSeconds / 60;
        int s = elapsedSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    // Batarya seviyesine göre CSS renk kodu döndürür. View etiket stilini buna göre ayarlar.
    public String getBatteryColor() {
        if (battery > 50) return Constants.COLOR_BATTERY_HIGH;
        if (battery > 20) return Constants.COLOR_BATTERY_MEDIUM;
        return Constants.COLOR_BATTERY_LOW;
    }

    // Yol listesinin değiştirilemez kopyasını döndürür. View doğrudan listeyi değiştiremez.
    public List<int[]> getMovementPath() {
        return Collections.unmodifiableList(movementPath);
    }

    public int       getRobotX()          { return robotX;          }
    public int       getRobotY()          { return robotY;          }
    public int       getBattery()         { return battery;         }
    public Direction getDirection()       { return direction;       }
    public boolean   isCharging()         { return isCharging;      }
    public int       getTotalCells()      { return totalCells;      }
    public int       getCleanedCells()    { return cleanedCells;    }
    public int       getDirtyCells()      { return dirtyCells;      }
    public boolean   isRunning()          { return isRunning;       }
    public boolean   isPaused()           { return isPaused;        }
    public int       getElapsedSeconds()  { return elapsedSeconds;  }
    public String    getStatusMessage()   { return statusMessage;   }
    public String    getActiveAlgorithm() { return activeAlgorithm; }
}
