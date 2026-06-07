package model;

import common.DirtType;
import common.Interfaces.IRobot;

// Kir türlerinin ortak şablonu. Temizleme süre ve maliyeti DirtType enum'undan alınır.
// Alt sınıflar clean() metodunu kendi DirtType'larıyla implement eder.
public abstract class Dirt {

    protected final DirtType dirtType;

    public Dirt(DirtType dirtType) {
        this.dirtType = dirtType;
    }

    public DirtType getDirtType() { return dirtType; }

    // Temizleme tamamlanınca çağrılır. Robot bataryası bu metod içinde düşürülür.
    public abstract void clean(IRobot robot);
}
