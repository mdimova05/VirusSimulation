import java.awt.Color;

/**
 * Represents a person who has recovered from the virus.
 * Recovered people have natural immunity.
 */
public class RecoveredPerson extends Person {
    private double immunityLevel = 0.90;
    
    /**
     * Constructs a recovered person.
     */
    public RecoveredPerson() {
        super();
        this.immunityLevel = immunityLevel;
        this.health = 85; // Recovered but not at full health
    }
    
    @Override
    public Color getColor() {
        return Color.BLUE;
    }
    
    @Override
    public String toString() {
        return "R";
    }
    
    @Override
    public boolean tryInfect(double infectionChance) {
        // Has natural immunity, very low chance of reinfection
        double actualChance = infectionChance * (1.0 - immunityLevel);
        return random.nextDouble() < actualChance;
    }
    
    /**
     * Gets the immunity level of this person.
     */
    public double getImmunity() {
        return immunityLevel;
    }
}
