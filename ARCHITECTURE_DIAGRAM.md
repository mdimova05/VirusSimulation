# Virus Simulation Architecture Diagram

## Class Hierarchy
```
                           Person (abstract)
                          /       |        \
                         /        |         \
              HealthyPerson   SickPerson   RecoveredPerson
                   |              |
                   |              |
            VaccinatedPerson  SuperSpreader
```

## Main Components Flow
```
┌─────────────────────────────────────────────────────────────────┐
│                     VirusSimulation (Main GUI)                  │
│  - JFrame with control buttons (Start/Stop/Reset/Step)          │
│  - Timer that triggers updates                                  │
└──────────────────┬──────────────────────────────────────────────┘
                   │ contains
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                         VirusPanel                              │
│  - JPanel that draws the grid                                   │
│  - Observes VirusModel for changes                              │
│  - Paints colored squares for each person                       │
└──────────────────┬──────────────────────────────────────────────┘
                   │ observes
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                        VirusModel                               │
│  - Person[][] grid (2D array)                                   │
│  - List<Person> personList                                      │
│  - Map<Person, Point> locationMap                               │
│  - Manages movement, collisions, infections                     │
└──────────────────┬──────────────────────────────────────────────┘
                   │ contains
                   ▼
              Person objects
           (all types mixed together)
```

## One Simulation Step (Tick)
```
1. VirusSimulation.Timer fires
        ↓
2. Call model.update()
        ↓
3. VirusModel processes each person:
        ├─→ Get person's direction (person.getMove())
        ├─→ Calculate new position with movePoint()
        ├─→ Check if space occupied (collision)
        ├─→ Move or stay in place
        └─→ Update grid and locationMap
        ↓
4. Check adjacent infections:
        ├─→ For each SickPerson/SuperSpreader
        ├─→ Check 8 surrounding cells
        ├─→ If HealthyPerson found, tryInfect()
        └─→ Replace with SickPerson if infected
        ↓
5. Update each person's health:
        ├─→ SickPerson: lose health based on severity
        ├─→ Check if dead (health ≤ 0)
        ├─→ Check if recovered (sickTime ≥ RECOVERY_TIME)
        └─→ Replace person if status changed
        ↓
6. model.notifyObservers()
        ↓
7. VirusPanel.update() called
        ↓
8. VirusPanel.repaint()
        ↓
9. Screen updates with new positions/colors
```

## Direction System
```
┌──────────────────────────────────────┐
│           Direction Class            │
│  (Singleton-like pattern)            │
├──────────────────────────────────────┤
│  NORTH ─────→ Direction(0, "North")  │
│  SOUTH ─────→ Direction(1, "South")  │
│  EAST  ─────→ Direction(2, "East")   │
│  WEST  ─────→ Direction(3, "West")   │
│  CENTER ────→ Direction(4, "Center") │
├──────────────────────────────────────┤
│  + random() : Direction              │
│  + fromValue(int) : Direction        │
└──────────────────────────────────────┘
         ▲
         │ returns
         │
    Person.getMove()
```

## Infection Spread (Adjacent Checking)
```
Grid around a SickPerson at position (x,y):

    ┌───┬───┬───┐
    │ ? │ ? │ ? │  Check all 8 surrounding cells
    ├───┼───┼───┤
    │ ? │ S │ ? │  S = SickPerson (or SuperSpreader)
    ├───┼───┼───┤  ? = Could be HealthyPerson
    │ ? │ ? │ ? │
    └───┴───┴───┘

For each "?" cell:
  1. Is there a person there?
  2. Is that person HealthyPerson?
  3. Roll dice: Math.random() < infectionChance?
  4. If yes → Replace HealthyPerson with new SickPerson
```

## Person Transformation Flow
```
Start
  ↓
HealthyPerson (green)
  │
  ├─→ Gets infected → SickPerson (red/orange/yellow)
  │                      │
  │                      ├─→ Health ≤ 0 → DEAD (removed)
  │                      │
  │                      └─→ sickTime ≥ 25 → RecoveredPerson (blue)
  │
  └─→ Vaccinated at start → VaccinatedPerson (cyan)
                                │
                                └─→ Gets infected (5% chance) → SickPerson

Special case:
  SickPerson created with 10% chance → SuperSpreader (dark red)
                                        (higher infection rate)
```

## Data Structure Relationships
```
VirusModel contains:

1. Person[][] grid
   ┌─────┬─────┬─────┬─────┐
   │  H  │null │  S  │  R  │  Array indexed by [x][y]
   ├─────┼─────┼─────┼─────┤  Holds reference to Person
   │null │  V  │  H  │null │  or null if empty
   └─────┴─────┴─────┴─────┘

2. List<Person> personList
   [Person1, Person2, Person3, ...]
   - Stores all living people
   - Iterated during update()

3. Map<Person, Point> locationMap
   Person1 → Point(2, 5)
   Person2 → Point(7, 3)
   Person3 → Point(0, 0)
   - Quick lookup: Where is this person?

All three stay synchronized:
  - Moving a person updates all three
  - Removing a person updates all three
  - Adding a person updates all three
```

## Color Coding
```
┌────────────────────┬─────────────────────┐
│   Person Type      │       Color         │
├────────────────────┼─────────────────────┤
│ HealthyPerson      │ Green               │
│ VaccinatedPerson   │ Cyan (light blue)   │
│ SickPerson (mild)  │ Yellow              │
│ SickPerson (mod)   │ Orange              │
│ SickPerson (severe)│ Red                 │
│ SuperSpreader      │ Dark Red            │
│ RecoveredPerson    │ Blue                │
└────────────────────┴─────────────────────┘
```

## Observer Pattern Flow
```
┌─────────────────┐
│  VirusModel     │ extends Observable (Subject)
│  (Observable)   │
└────────┬────────┘
         │ notifyObservers()
         │
         ▼
┌─────────────────┐
│  VirusPanel     │ implements Observer
│  (Observer)     │
└─────────────────┘
         │
         │ update() called automatically
         │
         ▼
    repaint() → Screen refreshes
```

## Config Constants Flow
```
All classes read from Config:

    Config.java
    ┌──────────────────────────┐
    │ GRID_WIDTH = 40          │──→ VirusModel uses for grid size
    │ GRID_HEIGHT = 30         │
    │ NUM_PEOPLE = 80          │──→ VirusModel creates people
    │ INITIALLY_INFECTED = 8   │
    │ CELL_SIZE = 14           │──→ VirusPanel uses for drawing
    │ RECOVERY_TIME = 25       │──→ SickPerson uses for healing
    │ DEATH_RATE = 0.015       │──→ SickPerson uses for mortality
    └──────────────────────────┘
```

## Polymorphism in Action
```
Person person = personList.get(i);  // Could be ANY Person subtype

person.getColor();   // Different color for each type
person.toString();   // Different letter for each type
person.tryInfect();  // Different logic for each type
person.getMove();    // All use same random movement

This is polymorphism:
  - Single reference type (Person)
  - Actual object could be any subclass
  - Correct method called based on ACTUAL type
  - No need to check "if (person instanceof ...)"
```
