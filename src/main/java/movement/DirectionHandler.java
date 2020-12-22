package movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DirectionHandler {
    private static final ArrayList<MapDirection> directions = new ArrayList<MapDirection>(Arrays.asList(
            MapDirection.NORTH,
            MapDirection.NORTHEAST,
            MapDirection.EAST,
            MapDirection.SOUTHEAST,
            MapDirection.SOUTH,
            MapDirection.SOUTHWEST,
            MapDirection.WEST,
            MapDirection.NORTHWEST));

    public static MapDirection rotate(MapDirection startingDirection, int rotationValue) {
        return directions.get( (directions.indexOf(startingDirection) + rotationValue) % 8 );
    }

    public static MapDirection getRandom() {
        Random generator = new Random();
        return directions.get( generator.nextInt(8) );
    }

    public static ArrayList<MapDirection> possibleDirectionsList() {
        return new ArrayList<MapDirection>(Arrays.asList(
                MapDirection.NORTH,
                MapDirection.NORTHEAST,
                MapDirection.EAST,
                MapDirection.SOUTHEAST,
                MapDirection.SOUTH,
                MapDirection.SOUTHWEST,
                MapDirection.WEST,
                MapDirection.NORTHWEST));
    }
}
