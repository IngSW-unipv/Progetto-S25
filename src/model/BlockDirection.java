package model;

public enum BlockDirection {
    FRONT(0, 0, 1),
    BACK(0, 0, -1),
    TOP(0, 1, 0),
    BOTTOM(0, -1, 0),
    RIGHT(1, 0, 0),
    LEFT(-1, 0, 0);

    private final int dx, dy, dz;

    BlockDirection(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public int getDz() { return dz; }
}