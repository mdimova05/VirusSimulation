import java.awt.Color;
import java.awt.Point;
import java.util.*;

/**
 * The model managing the virus simulation grid and disease spread.
 * Based on CritterModel structure but adapted for virus simulation.
 * 
 * @author Virus Simulation Team
 * @version 1.0
 */
public class VirusModel extends Observable {
    // Constants
    public static final String EMPTY = " ";
    
    // Grid dimensions
    private final int width;
    private final int height;
    
    // Data structures
    private final Person[][] grid;           // 2D grid of people
    private final String[][] display;        // String representation
    private final Color[][] colorDisplay;    // Color representation
    private final List<Person> personList;   // List of all people
    private final Map<Person, Point> locationMap;  // Person -> location
    private final Map<String, ClassState> classStateMap;  // Class statistics
    private final Random rand;
    
    // Simulation state
    private int moveCount;
    
    /**
     * Constructs a new virus simulation model.
     * @param width grid width
     * @param height grid height
     */
    public VirusModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.moveCount = 0;
        
        grid = new Person[width][height];
        display = new String[width][height];
        colorDisplay = new Color[width][height];
        personList = new ArrayList<>();
        locationMap = new HashMap<>();
        classStateMap = new TreeMap<>();
        rand = new Random();
        
        updateDisplay();
    }
    
    /**
     * Adds people of the given class to the simulation.
     * @param count number of people to add
     * @param personClass the class to instantiate
     */
    public void add(int count, Class<? extends Person> personClass) {
        String className = personClass.getName();
        if (!classStateMap.containsKey(className)) {
            classStateMap.put(className, new ClassState(personClass));
        }
        
        for (int i = 0; i < count; i++) {
            try {
                Person person = personClass.getDeclaredConstructor().newInstance();
                Point location = randomOpenLocation();
                
                personList.add(person);
                locationMap.put(person, location);
                grid[location.x][location.y] = person;
                
                classStateMap.get(className).count++;
            } catch (Exception e) {
                System.err.println("Error creating person: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        updateDisplay();
        setChanged();
        notifyObservers();
    }
    
    /**
     * Updates the simulation by one time step.
     */
    public void update() {
        moveCount++;
        
        // Shuffle for fairness
        Collections.shuffle(personList);
        
        // Move all people
        for (int i = 0; i < personList.size(); i++) {
            Person person = personList.get(i);
            if (!person.isAlive()) continue;
            
            Point oldLocation = locationMap.get(person);
            grid[oldLocation.x][oldLocation.y] = null;
            
            // Get movement direction
            Direction move = person.getMove();
            Point newLocation = new Point(oldLocation);
            movePoint(newLocation, move);
            
            // Check if new location is occupied
            Person other = grid[newLocation.x][newLocation.y];
            if (other == null) {
                // Move to new location
                oldLocation.setLocation(newLocation);
                grid[newLocation.x][newLocation.y] = person;
            } else {
                // Stay in place (collision)
                grid[oldLocation.x][oldLocation.y] = person;
                
                // Check for infection transmission
                checkInfection(person, other);
            }
            
            // Check for infection with adjacent people (even if person moved)
            checkAdjacentInfections(person);
        }
        
        // Update sick people
        updateSickPeople();
        
        // Update display
        updateDisplay();
        setChanged();
        notifyObservers();
    }
    
    /**
     * Checks for virus transmission between two people.
     */
    private void checkInfection(Person person1, Person person2) {
        // Check if person1 is sick and can infect person2
        if (person1 instanceof SickPerson && person1.isAlive()) {
            SickPerson sickPerson = (SickPerson) person1;
            double infectionChance = Config.BASE_INFECTION_RATE * sickPerson.getInfectionMultiplier();
            
            if (person2.tryInfect(infectionChance)) {
                // Infection successful - replace person2 with sick version
                replaceWithSick(person2);
            }
        }
        
        // Check if person2 is sick and can infect person1
        if (person2 instanceof SickPerson && person2.isAlive()) {
            SickPerson sickPerson = (SickPerson) person2;
            double infectionChance = Config.BASE_INFECTION_RATE * sickPerson.getInfectionMultiplier();
            
            if (person1.tryInfect(infectionChance)) {
                // Infection successful - replace person1 with sick version
                replaceWithSick(person1);
            }
        }
    }
    
    /**
     * Checks for virus transmission with all adjacent people.
     */
    private void checkAdjacentInfections(Person person) {
        Point location = locationMap.get(person);
        if (location == null) return;
        
        // Check all 8 surrounding cells (and center)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // Skip self
                
                int newX = (location.x + dx + width) % width;
                int newY = (location.y + dy + height) % height;
                
                Person neighbor = grid[newX][newY];
                if (neighbor != null && neighbor != person) {
                    // Only infect if within infection distance
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance <= Config.INFECTION_DISTANCE) {
                        checkInfection(person, neighbor);
                    }
                }
            }
        }
    }
    
    /**
     * Replaces a healthy person with a sick one.
     */
    private void replaceWithSick(Person person) {
        Point location = locationMap.get(person);
        if (location == null) return;
        
        // Remove old person
        String oldClassName = person.getClass().getName();
        personList.remove(person);
        locationMap.remove(person);
        classStateMap.get(oldClassName).count--;
        
        // Create new sick person
        SickPerson sickPerson = new SickPerson();
        personList.add(sickPerson);
        locationMap.put(sickPerson, location);
        grid[location.x][location.y] = sickPerson;
        
        // Update statistics
        String sickClassName = sickPerson.getClass().getName();
        if (!classStateMap.containsKey(sickClassName)) {
            classStateMap.put(sickClassName, new ClassState(SickPerson.class));
        }
        classStateMap.get(sickClassName).count++;
    }
    
    /**
     * Updates all sick people's health status.
     */
    private void updateSickPeople() {
        List<Person> toReplace = new ArrayList<>();
        
        for (Person person : personList) {
            if (person instanceof SickPerson && person.isAlive()) {
                SickPerson sickPerson = (SickPerson) person;
                sickPerson.updateSickness();
                
                if (!sickPerson.isAlive()) {
                    // Person died
                    String className = person.getClass().getName();
                    classStateMap.get(className).deaths++;
                } else if (sickPerson.hasRecovered()) {
                    // Person recovered
                    toReplace.add(person);
                }
            }
        }
        
        // Replace recovered people
        for (Person person : toReplace) {
            replaceWithRecovered(person);
        }
    }
    
    /**
     * Replaces a sick person with a recovered one.
     */
    private void replaceWithRecovered(Person person) {
        Point location = locationMap.get(person);
        if (location == null) return;
        
        // Remove old person
        String oldClassName = person.getClass().getName();
        personList.remove(person);
        locationMap.remove(person);
        classStateMap.get(oldClassName).count--;
        
        // Create recovered person
        RecoveredPerson recoveredPerson = new RecoveredPerson();
        personList.add(recoveredPerson);
        locationMap.put(recoveredPerson, location);
        grid[location.x][location.y] = recoveredPerson;
        
        // Update statistics
        String recoveredClassName = recoveredPerson.getClass().getName();
        if (!classStateMap.containsKey(recoveredClassName)) {
            classStateMap.put(recoveredClassName, new ClassState(RecoveredPerson.class));
        }
        classStateMap.get(recoveredClassName).count++;
    }
    
    /**
     * Resets the simulation.
     */
    public void reset() {
        // Clear all data structures
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = null;
            }
        }
        personList.clear();
        locationMap.clear();
        
        // Reset statistics but keep class registrations
        for (ClassState state : classStateMap.values()) {
            state.reset();
        }
        
        moveCount = 0;
        updateDisplay();
        setChanged();
        notifyObservers();
    }
    
    /**
     * Updates the display arrays.
     */
    private void updateDisplay() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y] == null) {
                    display[x][y] = EMPTY;
                    colorDisplay[x][y] = Color.WHITE;
                } else {
                    display[x][y] = grid[x][y].toString();
                    colorDisplay[x][y] = grid[x][y].getColor();
                }
            }
        }
    }
    
    /**
     * Moves a point in the given direction (with wrapping).
     * @param p the point to move
     * @param direction the direction to move
     */
    private Point movePoint(Point p, Direction direction) {
        if (direction == Direction.NORTH) {
            p.y = (p.y - 1 + height) % height;
        } else if (direction == Direction.SOUTH) {
            p.y = (p.y + 1) % height;
        } else if (direction == Direction.EAST) {
            p.x = (p.x + 1) % width;
        } else if (direction == Direction.WEST) {
            p.x = (p.x - 1 + width) % width;
        }
        // CENTER: Stay in place
        return p;
    }
    
    /**
     * Finds a random open location on the grid.
     */
    private Point randomOpenLocation() {
        Point p = new Point();
        do {
            p.x = rand.nextInt(width);
            p.y = rand.nextInt(height);
        } while (grid[p.x][p.y] != null);
        return p;
    }
    
    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getMoveCount() { return moveCount; }
    public String getString(int x, int y) { return display[x][y]; }
    public Color getColor(int x, int y) { return colorDisplay[x][y]; }
    
    public Set<String> getClassNames() {
        return classStateMap.keySet();
    }
    
    public int getCount(String className) {
        ClassState state = classStateMap.get(className);
        return state == null ? 0 : state.count;
    }
    
    public int getDeaths(String className) {
        ClassState state = classStateMap.get(className);
        return state == null ? 0 : state.deaths;
    }
    
    /**
     * Class to track statistics for each person type.
     */
    private static class ClassState {
        Class<? extends Person> personClass;
        int count;
        int deaths;
        
        ClassState(Class<? extends Person> personClass) {
            this.personClass = personClass;
            this.count = 0;
            this.deaths = 0;
        }
        
        void reset() {
            count = 0;
            deaths = 0;
        }
    }
}
