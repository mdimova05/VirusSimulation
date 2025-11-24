/**
 * Represents the five possible directions a person can move in the simulation.
 * Each direction has an integer value and a method to compute the next position.
 */
public class Direction {
    public static final Direction NORTH = new Direction(0, "North");
    public static final Direction SOUTH = new Direction(1, "South");
    public static final Direction EAST = new Direction(2, "East");
    public static final Direction WEST = new Direction(3, "West");
    public static final Direction CENTER = new Direction(4, "Center");
    
    private static final Direction[] ALL_DIRECTIONS = {NORTH, SOUTH, EAST, WEST, CENTER};
    
    private final int value;
    private final String name;
    
    private Direction(int value, String name) {
        this.value = value;
        this.name = name;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Returns a random direction from all available directions.
     */
    public static Direction random() {
        int index = (int) (Math.random() * ALL_DIRECTIONS.length);
        return ALL_DIRECTIONS[index];
    }
    
    /**
     * Gets a Direction by its integer value.
     */
    public static Direction fromValue(int value) {
        if (value >= 0 && value < ALL_DIRECTIONS.length) {
            return ALL_DIRECTIONS[value];
        }
        return CENTER;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
