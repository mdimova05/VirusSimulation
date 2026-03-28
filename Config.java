/**
 * Configuration class containing all simulation parameters.
 * Modify these constants to adjust simulation behavior.
 */
public class Config {
    // Grid dimensions
    public static final int DEFAULT_WIDTH = 40;
    public static final int DEFAULT_HEIGHT = 30;
    
    // Cell size for drawing (pixels)
    public static final int CELL_SIZE = 14;  // Slightly larger for visibility
    
    // Population settings
    public static final int DEFAULT_PERSON_COUNT = 80;  // More people
    public static final int INITIAL_INFECTED = 8;  // Start with more sick people
    public static final int INITIAL_VACCINATED_COUNT = 30;
    
    // Disease transmission settings
    public static final double BASE_INFECTION_RATE = 0.20; // 20% chance per contact
    public static final int INFECTION_DISTANCE = 1; // cells (must be adjacent)
    public static final int INCUBATION_PERIOD = 3; // ticks before showing symptoms
    public static final int RECOVERY_TIME = 25; // ticks to recover
    public static final double DEATH_RATE = 0.015; // 1.5% chance of death per tick
    
    // Movement - all move at same speed for simplicity
    public static final int MOVE_SPEED = 1;
    
    // Immunity settings
    public static final double VACCINE_EFFECTIVENESS = 0.85; // 85% protection
    public static final double NATURAL_IMMUNITY = 0.90; // 90% protection after recovery
    
    // Super spreader settings
    public static final double SUPERSPREADER_MULTIPLIER = 2.5;
    
    // Simulation timing
    public static final int UPDATE_DELAY_MS = 150; // milliseconds between updates
    
    // Display
    public static final String EMPTY = " ";
    public static final int FONT_SIZE = 12;
}
