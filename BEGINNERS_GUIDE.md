# Virus Simulation - Beginner's Guide

## What Does This Program Do?

Imagine a grid (like a checkerboard) where each square can have a person. These people move around randomly, and when a sick person bumps into a healthy person, the healthy person might get infected. The program shows this happening in a window on your screen with colors representing different types of people.

---

## The Big Picture: How Everything Works Together

Think of this program like a restaurant:
- **Person classes** = Different types of customers (some hungry, some full, some picky)
- **VirusModel** = The restaurant manager who keeps track of everyone
- **VirusPanel** = The artist who draws the restaurant layout
- **VirusSimulation** = The restaurant owner who controls everything
- **Config** = The rulebook for how the restaurant operates

---

## Part 1: The Person Family (Inheritance)

### What is Inheritance?

Inheritance is like a family tree. A child inherits traits from their parents, but can also have their own unique features.

```
Person (Grandparent - most general)
  ├── HealthyPerson (Parent)
  │     └── VaccinatedPerson (Child - has extra immunity)
  ├── SickPerson (Parent)
  │     └── SuperSpreader (Child - spreads virus more)
  └── RecoveredPerson (Parent - has immunity from being sick)
```

### Person.java - The Base Class (Grandparent)

```java
public abstract class Person {
    protected int health;        // How healthy they are (0-100)
    protected boolean alive;     // Are they still alive?
    protected Random random;     // For making random choices
```

**Key Concepts:**
- `abstract class` = A blueprint that can't be used directly (like saying "animal" - you need to be more specific like "dog" or "cat")
- `protected` = Only this class and its children can access these variables
- Variables store information about each person

```java
public enum Direction {
    NORTH, SOUTH, EAST, WEST, CENTER
}
```
**What's an enum?** It's a fixed list of options. Like a multiple choice question where the answers never change. Here, a person can only move in these 5 directions.

```java
public Direction getMove() {
    int choice = random.nextInt(5);  // Pick random number 0-4
    return Direction.values()[choice]; // Return that direction
}
```
**What this does:** Randomly picks a direction for the person to move.

```java
public abstract Color getColor();
public abstract String toString();
public abstract boolean tryInfect(double infectionChance);
```
**Abstract methods** = These are like saying "every person MUST have a color, but each type decides what color." The parent doesn't decide - the children do.

---

### HealthyPerson.java - Green People

```java
public class HealthyPerson extends Person {
```
**"extends Person"** means HealthyPerson inherits everything from Person, like a child inheriting traits from a parent.

```java
public HealthyPerson() {
    super();  // Call the parent's constructor first
}
```
**super()** = "Call my parent (Person) to set up the basic stuff first"

```java
@Override
public Color getColor() {
    return Color.GREEN;  // Healthy people show up as green
}

@Override
public String toString() {
    return "H";  // Display as letter "H" on the grid
}
```
**@Override** = "I'm replacing the parent's version with my own." Healthy people are green and show as "H".

```java
@Override
public boolean tryInfect(double infectionChance) {
    return random.nextDouble() < infectionChance;
}
```
**What this does:**
- `random.nextDouble()` generates a random decimal between 0.0 and 1.0
- If infectionChance is 0.20 (20%), and random generates 0.15, then 0.15 < 0.20 is TRUE → person gets infected!
- If random generates 0.85, then 0.85 < 0.20 is FALSE → person stays healthy!

---

### SickPerson.java - Red People

```java
protected int sicknessDuration;  // How long they've been sick
```

```java
public void updateSickness() {
    if (!alive) return;  // If dead, do nothing
    
    sicknessDuration++;  // Increase days sick
    health -= 1;         // Lose 1 health point
    
    if (random.nextDouble() < Config.DEATH_RATE) {
        health = 0;  // 3% chance to die immediately
    }
    
    if (health <= 0) {
        alive = false;  // If health hits 0, person dies
    }
}
```

**Step by step:**
1. Each tick (time step), person gets sicker
2. They lose health
3. Small random chance of death
4. If health reaches 0, they die

```java
public boolean hasRecovered() {
    return alive && sicknessDuration >= Config.RECOVERY_TIME;
}
```
**What this means:** If person is alive AND has been sick for 15+ ticks, they've recovered!

---

### VaccinatedPerson.java - Yellow People

```java
public class VaccinatedPerson extends HealthyPerson {
```
**Notice:** This extends **HealthyPerson**, not Person directly. It's a special type of healthy person!

```java
private double vaccineImmunity;

public VaccinatedPerson() {
    super();  // Call HealthyPerson's constructor
    this.vaccineImmunity = Config.VACCINE_EFFECTIVENESS; // 85%
}
```

```java
@Override
public boolean tryInfect(double infectionChance) {
    double actualChance = infectionChance * (1.0 - vaccineImmunity);
    return random.nextDouble() < actualChance;
}
```

**The Math:**
- Normal infection chance = 20%
- Vaccine immunity = 85%
- Actual chance = 20% × (1.0 - 0.85) = 20% × 0.15 = 3%
- So vaccinated people only have 3% infection chance instead of 20%!

---

### SuperSpreader.java - Purple People

```java
public class SuperSpreader extends SickPerson {
    
    @Override
    public double getInfectionMultiplier() {
        return Config.SUPERSPREADER_MULTIPLIER; // 2.5x
    }
}
```

**What this means:** When a super spreader tries to infect someone, their infection chance is 2.5 times higher!
- Normal sick person: 20% chance
- Super spreader: 20% × 2.5 = 50% chance!

---

## Part 2: The Grid Manager (VirusModel.java)

Think of this as the "brain" of the simulation. It keeps track of everything.

### Key Data Structures

```java
private final Person[][] grid;  // 2D array = the game board
```
**What's a 2D array?** It's like a spreadsheet with rows and columns:
```
    0   1   2   3   (x-axis)
0 [ H ] [  ] [ S ] [  ]
1 [  ] [ V ] [  ] [ H ]
2 [ S ] [  ] [ R ] [  ]
(y-axis)
```

```java
private final List<Person> personList;  // All people in one list
private final Map<Person, Point> locationMap;  // "Where is this person?"
```

**List vs Map:**
- **List** = Like a line of people. You can go through them one by one.
- **Map** = Like a phone book. You look up a person and get their location.

### The Main Update Loop

```java
public void update() {
    moveCount++;  // Increment the turn counter
    
    Collections.shuffle(personList);  // Randomize order (fair!)
```
**Why shuffle?** So the same people don't always move first. Everyone gets a fair chance.

```java
    for (int i = 0; i < personList.size(); i++) {
        Person person = personList.get(i);
        if (!person.isAlive()) continue;  // Skip dead people
```

**Get current location:**
```java
        Point oldLocation = locationMap.get(person);
        grid[oldLocation.x][oldLocation.y] = null;  // Remove from old spot
```

**Move the person:**
```java
        Person.Direction move = person.getMove();  // Ask: where do you want to go?
        Point newLocation = new Point(oldLocation);
        movePoint(newLocation, move);  // Calculate new position
```

**Check for collision:**
```java
        Person other = grid[newLocation.x][newLocation.y];
        if (other == null) {
            // Empty square - move there
            oldLocation.setLocation(newLocation);
            grid[newLocation.x][newLocation.y] = person;
        } else {
            // Someone's there - stay put, but check for infection
            grid[oldLocation.x][oldLocation.y] = person;
            checkInfection(person, other);
        }
```

### How Infection Works

```java
private void checkInfection(Person person1, Person person2) {
    if (person1 instanceof SickPerson && person1.isAlive()) {
```
**instanceof** = "Is this person a SickPerson (or a child class like SuperSpreader)?"

```java
        SickPerson sickPerson = (SickPerson) person1;
        double infectionChance = Config.BASE_INFECTION_RATE * 
                                 sickPerson.getInfectionMultiplier();
```
**Calculate chance:**
- Normal sick: 0.20 × 1.0 = 20%
- Super spreader: 0.20 × 2.5 = 50%

```java
        if (person2.tryInfect(infectionChance)) {
            replaceWithSick(person2);  // Turn them into a sick person!
        }
```

### Replacing People (Transformation)

```java
private void replaceWithSick(Person person) {
    Point location = locationMap.get(person);
    
    // Remove old person
    personList.remove(person);
    locationMap.remove(person);
    
    // Create new sick person at same location
    SickPerson sickPerson = new SickPerson();
    personList.add(sickPerson);
    locationMap.put(sickPerson, location);
    grid[location.x][location.y] = sickPerson;
```

**Why replace instead of just changing a variable?**
Because each person type is a different CLASS with different methods. A HealthyPerson doesn't have `updateSickness()` method, but SickPerson does!

---

## Part 3: The Display (VirusPanel.java)

This draws everything you see on screen.

```java
public class VirusPanel extends JPanel implements Observer {
```
**Observer Pattern:** Like subscribing to notifications. When the model changes, this panel gets notified and redraws itself.

```java
protected void paintComponent(Graphics g) {
    super.paintComponent(g);  // Clear the screen first
```

**Drawing the grid:**
```java
    for (int x = 0; x < model.getWidth(); x++) {
        for (int y = 0; y < model.getHeight(); y++) {
            String text = model.getString(x, y);  // Get "H", "S", "V", etc.
            Color color = model.getColor(x, y);   // Get green, red, yellow, etc.
            
            if (!text.equals(" ")) {  // If not empty
                // Draw the letter
                g.setColor(color);
                g.drawString(text, x * 12, y * 12);
            }
```

**Think of it like coloring a grid on graph paper** - for each square, check what should be there and color it appropriately.

```java
@Override
public void update(Observable o, Object arg) {
    repaint();  // Redraw everything
}
```
**When this gets called:** Whenever the model changes (person moves, gets infected, etc.), this automatically redraws the screen.

---

## Part 4: The Controller (VirusSimulation.java)

This is the "main control panel" with all the buttons.

```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        VirusSimulation sim = new VirusSimulation();
        sim.start();
    });
}
```
**What this does:** Starts the program on a special thread designed for GUIs (graphical user interfaces).

### Setting Up the Simulation

```java
model = new VirusModel(Config.DEFAULT_WIDTH, Config.DEFAULT_HEIGHT);

// Add people
model.add(15, VaccinatedPerson.class);  // 15 vaccinated
model.add(1, SuperSpreader.class);       // 1 super spreader
model.add(1, SickPerson.class);          // 1 normal sick
model.add(25, HealthyPerson.class);      // 25 healthy
```

**Class objects:** `VaccinatedPerson.class` is like a recipe. The model uses it to create actual VaccinatedPerson objects.

### The Timer

```java
timer = new javax.swing.Timer(Config.UPDATE_DELAY_MS, this);
```
**What's a Timer?** Like an alarm clock that goes off repeatedly:
1. Wait 150 milliseconds (0.15 seconds)
2. DING! Call `actionPerformed()`
3. Repeat

```java
public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();  // What triggered this?
    
    if (src == go) {
        timer.start();  // Start the repeating timer
    } else if (src == stop) {
        timer.stop();   // Stop the timer
    } else if (src == timer) {
        model.update();  // Update the simulation
        updateStatusLabel();
        updateClassPanels();
    }
}
```

**Flow:**
1. User clicks "Go" button → timer starts
2. Timer goes DING every 150ms → model.update() is called
3. Model updates → notifies panel → panel redraws
4. You see animation!

---

## Part 5: Configuration (Config.java)

This is like the "settings menu" for the simulation.

```java
public static final int DEFAULT_WIDTH = 60;
public static final int DEFAULT_HEIGHT = 50;
```
**final** = This value can never change. It's a constant.

```java
public static final double BASE_INFECTION_RATE = 0.20;
```
Change this to 0.50 (50%) for a super contagious virus!

```java
public static final int RECOVERY_TIME = 15;
```
Change this to 5 for quick recovery, or 30 for long illness.

---

## How It All Works Together: A Complete Example

Let's trace what happens when you click "Go":

1. **User clicks "Go" button**
   - `actionPerformed()` is called
   - `timer.start()` begins

2. **Timer goes off (every 150ms)**
   - Calls `model.update()`

3. **Model.update() executes**
   - Shuffles person list
   - For each person:
     - Gets their current position from `locationMap`
     - Asks them: "What direction do you want to move?"
     - Calculates new position
     - Checks if someone's already there
     - If collision with sick person → check infection
     - Moves person to new spot

4. **Infection check happens**
   - SickPerson calculates infection chance (20% or 50%)
   - Calls `healthyPerson.tryInfect(0.20)`
   - Random number generator decides: infected or not?
   - If infected: replace HealthyPerson with new SickPerson

5. **Update sick people**
   - For each SickPerson, call `updateSickness()`
   - Increase sickness duration
   - Decrease health
   - Check if recovered (duration >= 15)
   - If recovered: replace SickPerson with RecoveredPerson

6. **Notify observers**
   - Model calls `setChanged()` and `notifyObservers()`
   - VirusPanel receives notification
   - Panel calls `repaint()`

7. **Repaint happens**
   - `paintComponent()` is called
   - Loops through grid
   - For each square: get color and text, draw it
   - You see updated animation!

8. **Update statistics**
   - Count healthy, sick, recovered, dead
   - Update the panels on the right side

9. **Wait 150ms and repeat!**

---

## Common Java Concepts Used

### 1. Classes and Objects
```java
// Class = blueprint
public class HealthyPerson extends Person { }

// Object = actual instance
HealthyPerson person1 = new HealthyPerson();
HealthyPerson person2 = new HealthyPerson();
```
**Analogy:** Class is like a cookie cutter, objects are the actual cookies.

### 2. Inheritance
```java
public class SuperSpreader extends SickPerson {
```
SuperSpreader inherits all methods and variables from SickPerson.

### 3. Polymorphism
```java
Person person = new VaccinatedPerson();  // VaccinatedPerson IS-A Person
```
You can treat a VaccinatedPerson as just a Person when you want to.

### 4. Abstract Classes/Methods
```java
public abstract Color getColor();
```
Parent says "you MUST implement this," but doesn't say how.

### 5. Override
```java
@Override
public Color getColor() {
    return Color.GREEN;
}
```
"I'm replacing my parent's version with my own specific version."

### 6. Collections
```java
List<Person> personList = new ArrayList<>();  // Resizable list
Map<Person, Point> locationMap = new HashMap<>();  // Key-value pairs
```

### 7. For-each Loop
```java
for (Person person : personList) {
    // Do something with each person
}
```

### 8. If-else Logic
```java
if (person.isAlive()) {
    // Person is alive - do something
} else {
    // Person is dead - skip them
}
```

---

## Debugging Tips for Beginners

### Print Statements
Add these to see what's happening:
```java
System.out.println("Person at (" + x + "," + y + ") moving " + direction);
System.out.println("Infection attempted! Chance: " + infectionChance);
System.out.println("Total people: " + personList.size());
```

### Common Mistakes

1. **NullPointerException**
```java
Person person = grid[x][y];
person.getColor();  // ERROR if person is null!

// Fix:
if (person != null) {
    person.getColor();
}
```

2. **Array Index Out of Bounds**
```java
grid[60][50] = person;  // ERROR if grid is only 60x50!

// Fix: Use modulo to wrap around
x = (x + 1) % width;
```

3. **Forgetting to call super()**
```java
public HealthyPerson() {
    // super();  // MISSING! Parent constructor not called!
}
```

---

## How to Modify This Project

### Make the disease more/less contagious
In `Config.java`:
```java
public static final double BASE_INFECTION_RATE = 0.50; // 50% instead of 20%
```

### Make people move faster
In `VirusSimulation.java`:
```java
timer = new javax.swing.Timer(50, this); // 50ms instead of 150ms
```

### Add a new person type
1. Create new class extending Person or a child class
2. Override `getColor()`, `toString()`, `tryInfect()`
3. Add it in `VirusSimulation.java`:
```java
model.add(10, YourNewPersonClass.class);
```

### Change grid size
In `Config.java`:
```java
public static final int DEFAULT_WIDTH = 100;  // Bigger grid!
public static final int DEFAULT_HEIGHT = 80;
```

---

## Summary: The Core Concept

This simulation is built on **objects interacting with each other**:
- Each person is an **object** with properties (health, alive)
- People are stored in a **grid** (2D array)
- A **timer** repeatedly calls update
- Update makes people **move** and potentially **interact** (infect)
- Sick people **transform** into recovered people over time
- The **GUI** draws everything you see

It's like a tiny world where simple rules (move randomly, infect nearby people) create complex patterns (disease spreading, recovery, population changes).

**The beauty of OOP (Object-Oriented Programming):** Each person knows how to behave. The grid just says "hey everyone, take your turn!" and each person does their own thing.

---

## Questions to Test Understanding

1. What happens when a HealthyPerson collides with a SuperSpreader?
2. Why do we shuffle the personList before each update?
3. What would happen if RECOVERY_TIME was 0?
4. Why can't we just change a HealthyPerson's health variable instead of replacing them with a SickPerson?
5. What does the timer do?

**Answers:**
1. The HealthyPerson has a 50% chance of becoming infected (20% base × 2.5 multiplier)
2. So everyone gets a fair chance to move first (prevents bias)
3. Sick people would recover instantly - no one would stay sick!
4. Because HealthyPerson and SickPerson have different methods - SickPerson has updateSickness(), HealthyPerson doesn't
5. Calls model.update() repeatedly to create animation

---

Good luck with your project! Remember: break problems into small pieces, test frequently, and use print statements to see what's happening! 🎓
