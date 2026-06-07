package model;

import common.DirtType;
import common.Interfaces.IRobot;

// Leke kiri. 3 geçişte temizlenir, en yüksek batarya maliyetine sahiptir.
public class StainDirt extends Dirt {

    public StainDirt() { super(DirtType.STAIN); }

    @Override
    public void clean(IRobot robot) {
        robot.decreaseBattery(dirtType.getBatteryCost());
    }
}
