import java.awt.Color;

/**
 * Represents a vaccinated person.
 * Vaccinated people have high immunity and are resistant to infection.
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
     */
    public double getImmunity() {
        return vaccineImmunity;
    }
    
    /**
     * Attempts to dodge virus exposure through immunity.
     */
    public boolean dodgeVirus() {
        return random.nextDouble() < vaccineImmunity;
    }
}
