package common;

// Robotun 4 yönünü ve hareketini temsil eden enum.
// getDx/getDy grid koordinat değişimini verir. turnLeft/Right/opposite yön hesaplamada kullanılır.
public enum Direction {

    NORTH("Kuzey (↑)",  0, -1),
    SOUTH("Güney (↓)",  0,  1),
    EAST ("Doğu (→)",   1,  0),
    WEST ("Batı (←)",  -1,  0);

    private final String displayName;
    private final int    dx;
    private final int    dy;

    Direction(String displayName, int dx, int dy) {
        this.displayName = displayName;
        this.dx = dx;
        this.dy = dy;
    }

    public String getDisplayName() { return displayName; }
    public int    getDx()          { return dx; }
    public int    getDy()          { return dy; }

    public Direction turnLeft() {
        switch (this) {
            case NORTH: return WEST;
            case WEST:  return SOUTH;
            case SOUTH: return EAST;
            case EAST:  return NORTH;
            default:    return this;
        }
    }

    public Direction turnRight() {
        switch (this) {
            case NORTH: return EAST;
            case EAST:  return SOUTH;
            case SOUTH: return WEST;
            case WEST:  return NORTH;
            default:    return this;
        }
    }

    public Direction opposite() {
        switch (this) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST:  return WEST;
            case WEST:  return EAST;
            default:    return this;
        }
    }
}
