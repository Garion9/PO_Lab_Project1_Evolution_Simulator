package movement;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    public String toString() {
        return switch (this) {
            case NORTH -> "Polnoc";
            case NORTHEAST -> "Polnocny Wschod";
            case EAST -> "Wschod";
            case SOUTHEAST -> "Poludniowy Wschod";
            case SOUTH -> "Poludnie";
            case SOUTHWEST -> "Poludniowy Zachod";
            case WEST -> "Zachod";
            case NORTHWEST -> "Polnocny Zachod";
        };
    }

    public Vector2d toUnitVector() {
        return switch(this) {
            case NORTH -> new Vector2d(0,1);
            case NORTHEAST -> new Vector2d(1,1);
            case EAST -> new Vector2d(1,0);
            case SOUTHEAST -> new Vector2d(1,-1);
            case SOUTH -> new Vector2d(0,-1);
            case SOUTHWEST -> new Vector2d(-1,-1);
            case WEST -> new Vector2d(-1,0);
            case NORTHWEST -> new Vector2d(-1,1);
        };
    }
}