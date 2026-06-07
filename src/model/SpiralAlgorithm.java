package model;

import common.Direction;
import common.Interfaces.IRobot;
import common.Interfaces.IRoom;

// Dıştan içe spiral hareket algoritması.
// EAST 1 adım, SOUTH 1 adım, WEST 2 adım, NORTH 2 adım şeklinde büyüyen döngüler çizer.
// Engel veya sıkışma durumunda WallFollow kurtarma mantığına geçer.
public class SpiralAlgorithm extends CleaningAlgorithm {

    // Mevcut yönde kaç adım atıldı ve hedef kaç adım
    private int stepsTaken  = 0;
    private int stepsTarget = 1;

    // Her iki dönüşte bir stepsTarget artar (spiral büyümesi)
    private int turnCount = 0;

    // Arka arkaya kaç kez engele takıldı. 4 olunca spiral sıfırlanır.
    private int blockedCount = 0;

    @Override
    public int[] getNextMove(IRobot robot, IRoom room) {
        int cx = robot.getX();
        int cy = robot.getY();
        Direction dir = robot.getDirection();

        int nx = cx + dir.getDx();
        int ny = cy + dir.getDy();

        // Hedef adım dolmadıysa ve önü açıksa düz devam et
        if (stepsTaken < stepsTarget && room.isWalkable(nx, ny)) {
            stepsTaken++;
            blockedCount = 0;
            return new int[]{nx, ny};
        }

        // Yön değiştirme zamanı: saat yönünde açık yön ara
        Direction turned = findTurnRight(dir, cx, cy, room);
        if (turned != null) {
            turnCount++;
            // Her 2 dönüşte spiral bir adım daha büyür
            if (turnCount % 2 == 0) stepsTarget++;
            stepsTaken   = 1;
            blockedCount = 0;
            robot.setDirection(turned);
            return new int[]{cx + turned.getDx(), cy + turned.getDy()};
        }

        // Saat yönünde de yol yok: kurtarma modu (sağ, düz, sol, geri)
        blockedCount++;
        Direction rescue = findEscape(dir, cx, cy, room);

        if (rescue != null) {
            // 4 kez üst üste takılındıysa spiral sayaçlarını tamamen sıfırla
            if (blockedCount >= 4) {
                stepsTaken   = 0;
                stepsTarget  = 1;
                turnCount    = 0;
                blockedCount = 0;
            }
            robot.setDirection(rescue);
            return new int[]{cx + rescue.getDx(), cy + rescue.getDy()};
        }

        // 4 taraf da kapalı, yerinde kal
        return new int[]{cx, cy};
    }

    // Saat yönünde (turnRight) dönüp açık yön arar. 3 farklı yönü dener.
    private Direction findTurnRight(Direction dir, int cx, int cy, IRoom room) {
        Direction candidate = dir.turnRight();
        for (int i = 0; i < 3; i++) {
            if (room.isWalkable(cx + candidate.getDx(), cy + candidate.getDy()))
                return candidate;
            candidate = candidate.turnRight();
        }
        return null;
    }

    // Sağ, düz, sol, geri sırasıyla ilk açık yönü döndürür.
    private Direction findEscape(Direction dir, int cx, int cy, IRoom room) {
        for (Direction d : new Direction[]{
                dir.turnRight(), dir, dir.turnLeft(), dir.opposite()}) {
            if (room.isWalkable(cx + d.getDx(), cy + d.getDy())) return d;
        }
        return null;
    }

    @Override
    public void reset() {
        stepsTaken   = 0;
        stepsTarget  = 1;
        turnCount    = 0;
        blockedCount = 0;
    }
}
