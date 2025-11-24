import java.awt.Color;

/**
 * Represents a person infected with the virus.
 * Sick people lose health over time and can infect others.
 * 
 * @author Virus Simulation Team
 * @version 1.0
 */
public class SickPerson extends Person {
    protected int sicknessDuration;
    protected int severityLevel;  // How severe their case is (1-3)
    private int deathRate = 0.015;
    private int recoveryTime = 25;

    
    /**
     * Constructs a sick person.
     */
    public SickPerson() {
        super();
        this.sicknessDuration = 0;
        // Random severity: 70% mild (1), 20% moderate (2), 10% severe (3)
        double rand = random.nextDouble();
        if (rand < 0.70) {
            this.severityLevel = 1;
        } else if (rand < 0.90) {
            this.severityLevel = 2;
        } else {
            this.severityLevel = 3;
        }
    }
    
    @Override
    public Color getColor() {
        return Color.RED;
    }
    
    @Override
    public String toString() {
        return "S";
    }
    
    @Override
    public boolean tryInfect(double infectionChance) {
        // Already sick, cannot be infected again
        return false;
    }
    
    /**
     * Updates sickness duration (called by model).
     */
    public void updateSickness() {
        if (!alive) return;
        
        sicknessDuration++;
        
        // Health loss depends on severity
        // Mild (1): lose health every 3 ticks
        // Moderate (2): lose health every 2 ticks  
        // Severe (3): lose health every tick
        if (severityLevel == 3 || 
           (severityLevel == 2 && sicknessDuration % 2 == 0) ||
           (severityLevel == 1 && sicknessDuration % 3 == 0)) {
            health -= 1;
        }
        
        // Death chance increases with severity and duration
        // No death in first 5 ticks (incubation)
        if (sicknessDuration > 5) {
            double deathChance = deathRate * severityLevel;
            if (random.nextDouble() < deathChance) {
                health = 0;
            }
        }
        
        if (health <= 0) {
            alive = false;
        }
    }
    
    /**
     * Checks if this person has recovered from the illness.
     * @return true if recovery time has been reached and person is alive
     */
    public boolean hasRecovered() {
        return alive && sicknessDuration >= recoveryTime;
    }
    
    /**
     * Gets the infection rate multiplier for this sick person.
     * @return the multiplier (1.0 for normal sick person)
     */
    public double getInfectionMultiplier() {
        return 1.0;
    }
}
