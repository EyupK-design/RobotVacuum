package model;

import common.Constants;
import common.Direction;
import common.Interfaces.IRobot;

import java.util.ArrayList;
import java.util.List;

// Robotun tüm durumunu tutar: konum, batarya, yön, hız, geçilen yol.
// IRobot arayüzünü implement eder. Controller bu sınıfı IRobot olarak kullanır.
public class Robot implements IRobot {

    private int x;
    private int y;
    private int battery;
    private Direction direction;
    private boolean   isCharging;
    private double    speed;

    // Her move() çağrısında eski konum buraya eklenir. View yol çizmek için okur.
    private final List<int[]> movementPath;

    public Robot(int startX, int startY) {
        this.movementPath = new ArrayList<>();
        reset(startX, startY);
    }

    @Override public int       getX()          { return x; }
    @Override public int       getY()          { return y; }
    @Override public int       getBattery()    { return battery; }
    @Override public Direction getDirection()  { return direction; }
    @Override public boolean   isCharging()    { return isCharging; }
    @Override public double    getSpeed()      { return speed; }

    @Override
    public boolean isBatteryLow() {
        return battery <= Constants.LOW_BATTERY_THRESHOLD;
    }

    @Override
    public List<int[]> getMovementPath() { return movementPath; }

    // Robotu yeni koordinata taşır. Eski konum yola eklenir, batarya düşer.
    @Override
    public void move(int newX, int newY) {
        movementPath.add(new int[]{this.x, this.y});
        this.x = newX;
        this.y = newY;
        decreaseBattery(Constants.BATTERY_PER_MOVE);
    }

    @Override
    public void setDirection(Direction d) { this.direction = d; }

    // Math.max(0,...) ile bataryanın negatife düşmesi engellenir.
    @Override
    public void decreaseBattery(int amount) {
        this.battery = Math.max(0, this.battery - amount);
    }

    @Override public void startCharging() { this.isCharging = true;  }
    @Override public void stopCharging()  { this.isCharging = false; }

    // Her şarj tick'inde CHARGING_RATE kadar batarya artar. MAX'ı aşamaz.
    @Override
    public void chargeTick() {
        if (isCharging)
            this.battery = Math.min(Constants.MAX_BATTERY, this.battery + Constants.CHARGING_RATE);
    }

    // Hızı MIN_SPEED ile MAX_SPEED arasına kilitler. Timeline süresi buna göre hesaplanır.
    @Override
    public void setSpeed(double speed) {
        this.speed = Math.max(Constants.MIN_SPEED, Math.min(Constants.MAX_SPEED, speed));
    }

    // Robotu başlangıç değerlerine döndürür. Yol listesi temizlenir.
    @Override
    public void reset(int startX, int startY) {
        this.x          = startX;
        this.y          = startY;
        this.battery    = Constants.MAX_BATTERY;
        this.direction  = Direction.EAST;
        this.isCharging = false;
        this.speed      = Constants.DEFAULT_SPEED;
        this.movementPath.clear();
    }
}
