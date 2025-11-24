import java.awt.Color;

/**
 * Represents a vaccinated person.
 * Vaccinated people have high immunity and are resistant to infection.
 * 
 * @author Virus Simulation Team
 * @version 1.0
 */
public class VaccinatedPerson extends HealthyPerson {
    private double vaccineImmunity;
    private int vacineEffectivness = 0.85;
    
    /**
     * Constructs a vaccinated person.
     */
    public VaccinatedPerson() {
        super();
        this.vaccineImmunity = vacineEffectivness;
    }
    
    @Override
    public Color getColor() {
        return Color.YELLOW;
    }
    
    @Override
    public String toString() {
        return "V";
    }
    
    @Override
    public boolean tryInfect(double infectionChance) {
        // Vaccine provides strong protection
        double actualChance = infectionChance * (1.0 - vaccineImmunity);
        return random.nextDouble() < actualChance;
    }
    
    /**
     * Gets the immunity level from vaccination.
     * @return immunity as a percentage (0.0 to 1.0)
     */
    public double getImmunity() {
        return vaccineImmunity;
    }
    
    /**
     * Attempts to dodge virus exposure through immunity.
     * @return true if successfully dodged
     */
    public boolean dodgeVirus() {
        return random.nextDouble() < vaccineImmunity;
    }
}
