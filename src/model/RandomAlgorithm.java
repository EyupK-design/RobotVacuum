package model;

import common.Direction;
import common.Interfaces.IRobot;
import common.Interfaces.IRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Rastgele hareket algoritması. Geri dönüşü son seçenek yapar,
// temizlenmemiş hücrelere öncelik vererek daha iyi alan taraması sağlar.
public class RandomAlgorithm extends CleaningAlgorithm {

    private final Random random = new Random();

    // Son seçilen yön. Geri dönüşü engellemek için saklanır.
    private Direction lastChosen = null;

    @Override
    public int[] getNextMove(IRobot robot, IRoom room) {
        int cx = robot.getX();
        int cy = robot.getY();
        Direction currentDir = robot.getDirection();
        Direction opposite   = currentDir.opposite();

        // Geçilebilir yönler üç gruba ayrılır: öncelikli, geri dönüş olmayan, tümü.
        List<Direction> preferred    = new ArrayList<>(); // temizlenmemiş + geri dönüş yok
        List<Direction> noBacktrack  = new ArrayList<>(); // sadece geri dönüş yok
        List<Direction> allWalkable  = new ArrayList<>(); // her açık yön

        for (Direction dir : Direction.values()) {
            int nx = cx + dir.getDx();
            int ny = cy + dir.getDy();
            if (!room.isWalkable(nx, ny)) continue;

            allWalkable.add(dir);

            if (dir != opposite) {
                noBacktrack.add(dir);
                // Temizlenmemiş hücreye giden yön en yüksek önceliği alır
                if (room.getCell(nx, ny) != null && !room.getCell(nx, ny).isCleaned()) {
                    preferred.add(dir);
                }
            }
        }

        if (allWalkable.isEmpty()) return new int[]{cx, cy};

        // Öncelik sırası: temizlenmemiş hedef > geri dönüş olmayan > tümü
        List<Direction> candidates = !preferred.isEmpty()   ? preferred
                                   : !noBacktrack.isEmpty() ? noBacktrack
                                   : allWalkable;

        Direction chosen = candidates.get(random.nextInt(candidates.size()));
        robot.setDirection(chosen);
        lastChosen = chosen;

        return new int[]{cx + chosen.getDx(), cy + chosen.getDy()};
    }

    @Override
    public void reset() {
        lastChosen = null;
    }
}
