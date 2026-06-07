package common;

// Kir türlerini ve onlara ait temizleme/batarya değerlerini tanımlar.
// Cell ve Controller bu enum üzerinden kir maliyetlerini okur.
public enum DirtType {

    DUST  ("Toz",  Constants.DUST_CLEAN_TICKS,   Constants.DUST_BATTERY_COST,   Constants.COLOR_DIRT_DUST),
    LIQUID("Sıvı", Constants.LIQUID_CLEAN_TICKS,  Constants.LIQUID_BATTERY_COST, Constants.COLOR_DIRT_LIQUID),
    STAIN ("Leke", Constants.STAIN_CLEAN_TICKS,   Constants.STAIN_BATTERY_COST,  Constants.COLOR_DIRT_STAIN);

    private final String displayName;
    private final int    cleanTicks;   // Robot bu hücre üzerinde kaç tick kalmalı
    private final int    batteryCost;  // Temizleme tamamlanınca düşen batarya
    private final String color;        // GridView'da hücre rengi

    DirtType(String displayName, int cleanTicks, int batteryCost, String color) {
        this.displayName = displayName;
        this.cleanTicks  = cleanTicks;
        this.batteryCost = batteryCost;
        this.color       = color;
    }

    public String getDisplayName() { return displayName; }
    public int    getCleanTicks()  { return cleanTicks;  }
    public int    getBatteryCost() { return batteryCost; }
    public String getColor()       { return color;       }
}
