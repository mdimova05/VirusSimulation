import java.awt.Color;
import java.util.Random;

/**
 * Base class representing a person in the virus simulation.
 * All people have health status and can move around the grid.
 * Similar to Critter class structure.
 * 
 * @author Virus Simulation Team
 * @version 1.0
 */
public abstract class Person {
    protected int health; // 0-100
    protected boolean alive;
    protected Random random;
    
    /**
     * Constructs a person.
     */
    public Person() {
        this.health = 100;
        this.alive = true;
        this.random = new Random();
    }
    
    /**
     * Gets the direction this person wants to move.
     * @return the direction to move
     */
    public Direction getMove() {
        // Random movement by default
        return Direction.random();
    }
    
    /**
     * Gets the color to display this person in the GUI.
     * @return the color
     */
    public abstract Color getColor();
    
    /**
     * Gets the string representation of this person.
     * @return the display string
     */
    public abstract String toString();
    
    /**
     * Attempts to infect this person with a virus.
     * @param infectionChance the probability of infection (0.0 to 1.0)
     * @return true if infection was successful
     */
    public abstract boolean tryInfect(double infectionChance);
    
    /**
     * Called when a collision occurs with another person.
     * @param other the other person's class name
     */
    public void collide(String other) {
        // Override in subclasses if needed
    }
    
    // Getters and setters
    public int getHealth() { return health; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
}
