package controller;

import common.Constants;
import model.CleaningAlgorithm;
import model.RandomAlgorithm;
import model.SpiralAlgorithm;
import model.WallFollowAlgorithm;

// Algoritma adına göre doğru CleaningAlgorithm nesnesini üretir.
// Controller algoritma sınıflarına doğrudan bağımlı olmaz, bu fabrika aracılığıyla çalışır.
public final class AlgorithmFactory {

    private AlgorithmFactory() {}

    public static CleaningAlgorithm create(String algorithmName) {
        if (algorithmName == null) return new RandomAlgorithm();
        switch (algorithmName) {
            case Constants.ALGO_SPIRAL:      return new SpiralAlgorithm();
            case Constants.ALGO_WALL_FOLLOW: return new WallFollowAlgorithm();
            case Constants.ALGO_RANDOM:
            default:                         return new RandomAlgorithm();
        }
    }
}
