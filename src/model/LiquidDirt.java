package model;

import common.DirtType;
import common.Interfaces.IRobot;

// Sıvı kiri. 2 geçişte temizlenir, orta batarya maliyetine sahiptir.
public class LiquidDirt extends Dirt {

    public LiquidDirt() { super(DirtType.LIQUID); }

    @Override
    public void clean(IRobot robot) {
        robot.decreaseBattery(dirtType.getBatteryCost());
    }
}
