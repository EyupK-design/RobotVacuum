package model;

import common.DirtType;
import common.Interfaces.IRobot;

// Toz kiri. 1 geçişte temizlenir, en düşük batarya maliyetine sahiptir.
public class DustDirt extends Dirt {

    public DustDirt() { super(DirtType.DUST); }

    @Override
    public void clean(IRobot robot) {
        robot.decreaseBattery(dirtType.getBatteryCost());
    }
}
