package model;

public record WorldData(String name, long seed) {
    @Override
    public String toString() {
        return name + "\nSeed: " + seed;
    }
}