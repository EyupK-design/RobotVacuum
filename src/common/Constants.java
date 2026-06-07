package common;

// Projenin tüm sabit değerlerini tek yerde tutar.
// Hiçbir dosyada sabit sayı hardcode edilmez, buradan okunur.
public final class Constants {

    private Constants() {}

    // Grid ve canvas boyutları
    public static final int GRID_ROWS    = 15;
    public static final int GRID_COLS    = 20;
    public static final int CELL_SIZE    = 40;
    public static final int CANVAS_WIDTH  = GRID_COLS * CELL_SIZE;
    public static final int CANVAS_HEIGHT = GRID_ROWS * CELL_SIZE;

    // Robot parametreleri
    public static final int    MAX_BATTERY           = 100;
    public static final int    LOW_BATTERY_THRESHOLD = 20;
    public static final int    BATTERY_PER_MOVE      = 1;
    public static final int    CHARGING_RATE         = 5;
    public static final double DEFAULT_SPEED         = 1.0;
    public static final double MIN_SPEED             = 0.5;
    public static final double MAX_SPEED             = 3.0;

    // Şarj istasyonu başlangıç konumu (sol üst köşe)
    public static final int CHARGING_STATION_X = 0;
    public static final int CHARGING_STATION_Y = 0;

    // Kir temizleme tick sayıları. Robot kirli hücre üzerinde bu kadar tick kalmalıdır.
    // DUST 1 tick: tek geçişte temizlenir.
    // LIQUID 2 tick: iki geçişte temizlenir.
    // STAIN 3 tick: üç geçişte temizlenir.
    public static final int DUST_CLEAN_TICKS   = 1;
    public static final int LIQUID_CLEAN_TICKS = 2;
    public static final int STAIN_CLEAN_TICKS  = 3;

    // Temizleme tamamlanınca harcanan ek batarya miktarı
    public static final int DUST_BATTERY_COST   = 1;
    public static final int LIQUID_BATTERY_COST = 3;
    public static final int STAIN_BATTERY_COST  = 5;

    // Hücre renkleri (JavaFX CSS hex formatı)
    public static final String COLOR_CELL_EMPTY    = "#FFFFFF";
    public static final String COLOR_CELL_CLEANED  = "#E8F5E9";
    public static final String COLOR_CELL_OBSTACLE = "#546E7A";
    public static final String COLOR_GRID_LINE     = "#BDBDBD";

    // Kir renkleri
    public static final String COLOR_DIRT_DUST   = "#D4A843";
    public static final String COLOR_DIRT_LIQUID = "#2196F3";
    public static final String COLOR_DIRT_STAIN  = "#6D4C41";

    // Robot ve yol renkleri
    public static final String COLOR_ROBOT            = "#37474F";
    public static final String COLOR_ROBOT_OUTLINE    = "#90A4AE";
    public static final String COLOR_PATH             = "#80CBC4";
    public static final String COLOR_CHARGING_STATION = "#FFC107";

    // Batarya seviyesine göre renk eşikleri
    public static final String COLOR_BATTERY_HIGH   = "#4CAF50";
    public static final String COLOR_BATTERY_MEDIUM = "#FF9800";
    public static final String COLOR_BATTERY_LOW    = "#F44336";

    // Algoritma isim sabitleri. AlgorithmFactory bu değerleri kullanır.
    public static final String ALGO_RANDOM      = "RANDOM";
    public static final String ALGO_SPIRAL      = "SPIRAL";
    public static final String ALGO_WALL_FOLLOW = "WALL_FOLLOW";

    // Simülasyon döngü hızı (ms). Hız çarpanına bölünür.
    public static final int SIMULATION_TICK_MS = 500;

    // BFS maksimum adım sınırı. Aşılırsa yol bulunamadı kabul edilir.
    public static final int MAX_PATH_STEPS = 1000;

    // Pencere ve panel boyutları
    public static final double WINDOW_WIDTH        = 1060;
    public static final double WINDOW_HEIGHT       = 720;
    public static final double CONTROL_PANEL_WIDTH = 230;
    public static final double STATUS_BAR_HEIGHT   = 50;
}
