package model;

import common.DirtType;
import common.Interfaces.ICell;

// Odanın tek bir karesini temsil eder. x/y koordinatı final'dır, değişmez.
// Kir ilerlemesi (dirtProgress) robot ayrılsa bile sıfırlanmaz.
public class Cell implements ICell {

    private final int x;
    private final int y;

    private boolean  isObstacle;
    private boolean  isCleaned;
    private DirtType dirtType;

    // Robotun bu hücre üzerinde kaç kez durduğunu sayar. clearDirt() ile sıfırlanır.
    private int dirtProgress;

    public Cell(int x, int y) {
        this.x            = x;
        this.y            = y;
        this.isObstacle   = false;
        this.isCleaned    = false;
        this.dirtType     = null;
        this.dirtProgress = 0;
    }

    @Override public int      getX()            { return x; }
    @Override public int      getY()            { return y; }
    @Override public boolean  isObstacle()      { return isObstacle; }
    @Override public boolean  isCleaned()       { return isCleaned; }
    @Override public boolean  hasDirt()         { return dirtType != null; }
    @Override public DirtType getDirtType()     { return dirtType; }
    @Override public int      getDirtProgress() { return dirtProgress; }

    @Override
    public void setObstacle(boolean obstacle) {
        this.isObstacle = obstacle;
    }

    // Yeni kir atanırken ilerlemesayacı sıfırlanır ve hücre temiz işaretlenmez
    @Override
    public void setDirt(DirtType type) {
        this.dirtType     = type;
        this.dirtProgress = 0;
        this.isCleaned    = false;
    }

    // Kir tamamen temizlendiğinde çağrılır. Hücreyi temizlenmiş olarak işaretler.
    @Override
    public void clearDirt() {
        this.dirtType     = null;
        this.dirtProgress = 0;
        this.isCleaned    = true;
    }

    @Override
    public void setCleaned(boolean cleaned) {
        this.isCleaned = cleaned;
    }

    // Controller her tick'te çağırır. Yeterli sayıya ulaşınca kir temizlenir.
    @Override
    public void incrementDirtProgress() {
        this.dirtProgress++;
    }
}
