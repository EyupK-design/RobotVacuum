package model;

import common.Direction;
import common.Interfaces.IRobot;
import common.Interfaces.IRoom;

// Sağ el duvarı takip algoritması.
// Sağ taraf kapalıysa düz ilerler, sağ açılırsa sağa döner.
// Bu sayede odanın tüm dış çevresini ve iç duvarlarını sistematik tarar.
public class WallFollowAlgorithm extends CleaningAlgorithm {

    // Arka arkaya aynı yerde kalma sayacı. Takılma tespiti için kullanılır.
    private int stuckCount = 0;

    // Son konum. Hareket edip etmediğini anlamak için karşılaştırılır.
    private int lastX = -1;
    private int lastY = -1;

    @Override
    public int[] getNextMove(IRobot robot, IRoom room) {
        int cx = robot.getX();
        int cy = robot.getY();
        Direction cur = robot.getDirection();

        // Hareket olmadıysa stuckCount artar
        if (cx == lastX && cy == lastY) {
            stuckCount++;
        } else {
            stuckCount = 0;
        }
        lastX = cx;
        lastY = cy;

        // 3 tick boyunca kıpırdayamadıysa yönü sıfırla ve rastgele açık yöne geç
        if (stuckCount >= 3) {
            stuckCount = 0;
            Direction escape = findAnyOpen(cur, cx, cy, room);
            if (escape != null) {
                robot.setDirection(escape);
                return new int[]{cx + escape.getDx(), cy + escape.getDy()};
            }
            return new int[]{cx, cy};
        }

        Direction right   = cur.turnRight();
        Direction forward = cur;
        Direction left    = cur.turnLeft();
        Direction back    = cur.opposite();

        boolean rightOpen   = room.isWalkable(cx + right.getDx(),   cy + right.getDy());
        boolean forwardOpen = room.isWalkable(cx + forward.getDx(), cy + forward.getDy());
        boolean leftOpen    = room.isWalkable(cx + left.getDx(),    cy + left.getDy());
        boolean backOpen    = room.isWalkable(cx + back.getDx(),    cy + back.getDy());

        // Gerçek sağ el kuralı:
        // Sağ kapalı + ileri açık → düz git (duvara paralel hareket)
        // Sağ açık               → sağa dön (duvara yapış)
        // İleri de kapalı        → sola dön (köşe al)
        // Her yer kapalı         → geri dön

        if (!rightOpen && forwardOpen) {
            robot.setDirection(forward);
            return new int[]{cx + forward.getDx(), cy + forward.getDy()};
        }
        if (rightOpen) {
            robot.setDirection(right);
            return new int[]{cx + right.getDx(), cy + right.getDy()};
        }
        if (leftOpen) {
            robot.setDirection(left);
            return new int[]{cx + left.getDx(), cy + left.getDy()};
        }
        if (backOpen) {
            robot.setDirection(back);
            return new int[]{cx + back.getDx(), cy + back.getDy()};
        }

        return new int[]{cx, cy};
    }

    // Herhangi bir yönde açık hücre arar. Takılma kurtarmasında kullanılır.
    private Direction findAnyOpen(Direction current, int cx, int cy, IRoom room) {
        Direction[] order = {
            current.turnLeft(), current.opposite(),
            current.turnRight(), current
        };
        for (Direction d : order) {
            if (room.isWalkable(cx + d.getDx(), cy + d.getDy())) return d;
        }
        return null;
    }

    @Override
    public void reset() {
        stuckCount = 0;
        lastX = -1;
        lastY = -1;
    }
}
