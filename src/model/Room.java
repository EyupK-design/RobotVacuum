package model;

import common.Constants;
import common.DirtType;
import common.Interfaces.ICell;
import common.Interfaces.IChargingStation;
import common.Interfaces.IRoom;

// Odanın tüm hücrelerini grid olarak tutar ve üzerindeki işlemleri yönetir.
// Controller ve View bu sınıfı IRoom arayüzü üzerinden kullanır.
public class Room implements IRoom {

    // grid[y][x] sıralaması kullanılır. Java'da ilk indeks satır (y), ikinci sütun (x).
    private Cell[][] grid;

    private final ChargingStation chargingStation;
    private final int rows;
    private final int cols;

    public Room() {
        this.rows = Constants.GRID_ROWS;
        this.cols = Constants.GRID_COLS;
        this.chargingStation = new ChargingStation(
                Constants.CHARGING_STATION_X, Constants.CHARGING_STATION_Y);
        initializeGrid();
    }

    // Tüm Cell nesnelerini yeniden oluşturur. reset() çağrısında da kullanılır.
    private void initializeGrid() {
        grid = new Cell[rows][cols];
        for (int y = 0; y < rows; y++)
            for (int x = 0; x < cols; x++)
                grid[y][x] = new Cell(x, y);
    }

    // Sınır dışı erişimde null döner. Çağıran taraf null kontrolü yapmalıdır.
    @Override
    public ICell getCell(int x, int y) {
        if (!isValidPosition(x, y)) return null;
        return grid[y][x];
    }

    @Override public int getRows() { return rows; }
    @Override public int getCols() { return cols; }

    @Override
    public IChargingStation getChargingStation() { return chargingStation; }

    @Override
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && y >= 0 && x < cols && y < rows;
    }

    // Hem sınır hem engel kontrolü yapar. Tüm hareket kararları buraya dayanır.
    @Override
    public boolean isWalkable(int x, int y) {
        return isValidPosition(x, y) && !grid[y][x].isObstacle();
    }

    @Override
    public int getTotalWalkableCells() {
        int count = 0;
        for (int y = 0; y < rows; y++)
            for (int x = 0; x < cols; x++)
                if (!grid[y][x].isObstacle()) count++;
        return count;
    }

    @Override
    public int getCleanedCellCount() {
        int count = 0;
        for (int y = 0; y < rows; y++)
            for (int x = 0; x < cols; x++)
                if (grid[y][x].isCleaned()) count++;
        return count;
    }

    @Override
    public int getDirtyCellCount() {
        int count = 0;
        for (int y = 0; y < rows; y++)
            for (int x = 0; x < cols; x++)
                if (grid[y][x].hasDirt()) count++;
        return count;
    }

    // Engel üstüne kir eklenemez. isValidPosition kontrolü zorunludur.
    @Override
    public void addDirt(int x, int y, DirtType type) {
        if (isValidPosition(x, y) && !grid[y][x].isObstacle())
            grid[y][x].setDirt(type);
    }

    // Engel konulunca altındaki kir de silinir. Kirli engel istatistikleri bozar.
    @Override
    public void addObstacle(int x, int y) {
        if (isValidPosition(x, y)) {
            grid[y][x].setObstacle(true);
            grid[y][x].clearDirt();
        }
    }

    @Override
    public void removeObstacle(int x, int y) {
        if (isValidPosition(x, y)) grid[y][x].setObstacle(false);
    }

    // Tüm hücreleri sıfırdan oluşturur. Engeller ve kirler tamamen temizlenir.
    @Override
    public void reset() {
        initializeGrid();
    }
}
