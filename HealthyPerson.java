import java.awt.Color;

/**
 * Represents a healthy, uninfected person.
 * Healthy people can be infected when exposed to virus.
 * 
 * @author Virus Simulation Team
 * @version 1.0
 */
public class HealthyPerson extends Person {
    
    /**
     * Constructs a healthy person.
     */
    public HealthyPerson() {
        super();
    }
    
    @Override
    public Color getColor() {
        return Color.GREEN;
    }
    
    @Override
    public String toString() {
        return "H";
    }
    
    @Override
    public boolean tryInfect(double infectionChance) {
        return random.nextDouble() < infectionChance;
    }
}
