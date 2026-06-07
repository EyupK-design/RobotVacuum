package model;

import common.Interfaces.IRobot;
import common.Interfaces.IRoom;

// Tüm temizleme algoritmalarının uyması gereken soyut sınıf.
// getNextMove() robotun bir sonraki hedef koordinatını döndürür.
// reset() simülasyon sıfırlanınca veya algoritma değiştirilince çağrılır.
public abstract class CleaningAlgorithm {

    public abstract int[] getNextMove(IRobot robot, IRoom room);

    // Alt sınıflar iç sayaçlarını burada sıfırlar. Varsayılan gövde boştur.
    public void reset() {}
}
