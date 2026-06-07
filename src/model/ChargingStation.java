package model;

import common.Interfaces.IChargingStation;

// Şarj istasyonunun konumunu ve robotun orada olup olmadığını tutar.
// Konum sabittir (final). Simülasyon boyunca değişmez.
public class ChargingStation implements IChargingStation {

    private final int x;
    private final int y;

    // Robot fiziksel olarak istasyondaysa true. Şarj mantığı buna göre çalışır.
    private boolean isRobotPresent;

    public ChargingStation(int x, int y) {
        this.x              = x;
        this.y              = y;
        this.isRobotPresent = true;
    }

    @Override public int     getX()                     { return x; }
    @Override public int     getY()                     { return y; }
    @Override public boolean isRobotPresent()           { return isRobotPresent; }
    @Override public void    setRobotPresent(boolean p) { this.isRobotPresent = p; }
}
