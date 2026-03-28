import java.awt.Color;

/**
 * Represents a super spreader - a sick person who spreads the virus more easily.
 * Super spreaders have a higher infection rate when interacting with others.
 */
public class SuperSpreader extends SickPerson {
    private int superSpreaderMultiplier = 2.5;
    /**
     * Constructs a super spreader.
     */
    public SuperSpreader() {
        super();
    }
    
    @Override
    public Color getColor() {
        return new Color(128, 0, 128); // Purple
    }
    
    @Override
    public String toString() {
        return "X";
    }
    
    /**
     * Gets the infection rate multiplier for super spreader.
     */
    @Override
    public double getInfectionMultiplier() {
        return superSpreaderMultiplier;
    }
}
