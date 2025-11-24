# Virus Spread Simulation - Final Project

## Project Overview
This is a Java-based virus simulation that models disease spread through a population. The simulation demonstrates how COVID-19 (or similar diseases) spreads based on proximity, immunity, and various individual characteristics.

## Class Hierarchy (3 Levels, 6 Classes)

### Level 1: Base Class
- **Person** - Abstract base class for all people in the simulation
  - Properties: health, alive status
  - Methods: getMove(), getColor(), toString(), tryInfect()

### Level 2: Primary States
- **HealthyPerson** extends Person - Uninfected individuals (Green "H")
- **SickPerson** extends Person - Infected individuals (Red "S")  
- **RecoveredPerson** extends Person - Recovered with immunity (Blue "R")

### Level 3: Specialized Types
- **VaccinatedPerson** extends HealthyPerson - High immunity (Yellow "V")
- **SuperSpreader** extends SickPerson - Higher transmission rate (Purple "X")

## Implementation Details

### Grid-Based Model (SimulationGrid)
- Manages a 2D grid (default 60x50 cells)
- Tracks all people and their positions
- Handles movement and collision detection
- Processes disease transmission
- Converts sick people to recovered after recovery period

### Disease Mechanics
- **Transmission**: Occurs when sick person collides with susceptible person
- **Base Infection Rate**: 20% per contact
- **Super Spreader Multiplier**: 2.5x higher transmission
- **Incubation Period**: 3 ticks before symptoms
- **Recovery Time**: 15 ticks to recover
- **Death Rate**: 3% chance while sick
- **Immunity**: 
  - Vaccine: 85% effective
  - Natural (post-recovery): 90% effective

### GUI Features
- **Real-time visualization** of the grid
- **Color-coded display**:
  - Green (H) = Healthy
  - Yellow (V) = Vaccinated  
  - Red (S) = Sick
  - Purple (X) = Super Spreader
  - Blue (R) = Recovered
- **Statistics panel** showing alive/dead counts per class
- **Controls**: Go, Stop, Tick (step-by-step), Reset
- **Move counter** tracking simulation time

## How to Compile and Run

```bash
# Navigate to project directory
cd c:\Dev\Java\VirusSimulation

# Compile all Java files
javac *.java

# Run the simulation
java VirusSimulation
```

## Configuration

Adjust parameters in `Config.java`:
- Grid dimensions (DEFAULT_WIDTH, DEFAULT_HEIGHT)
- Population sizes
- Infection rates and distances
- Recovery and death rates
- Immunity effectiveness
- Animation speed (UPDATE_DELAY_MS)

## Files Included

1. **Config.java** - All simulation constants
2. **Person.java** - Abstract base class
3. **HealthyPerson.java** - Level 2 class
4. **SickPerson.java** - Level 2 class  
5. **RecoveredPerson.java** - Level 2 class
6. **VaccinatedPerson.java** - Level 3 class (extends HealthyPerson)
7. **SuperSpreader.java** - Level 3 class (extends SickPerson)
8. **VirusModel.java** - Grid model managing simulation state
9. **VirusPanel.java** - GUI panel for display
10. **VirusSimulation.java** - Main class with GUI and controls

## Design Patterns Used

- **Inheritance**: 3-level hierarchy with proper OOP design
- **Observer Pattern**: Model notifies GUI of changes
- **MVC Architecture**: Separation of model (VirusModel), view (VirusPanel), and controller (VirusSimulation)
- **Encapsulation**: Private fields, public methods
- **Polymorphism**: All Person subclasses handled uniformly in grid

## Assignment Requirements Met

✅ User can specify options (adjustable in Config.java)  
✅ Simulation runs as animation in GUI  
✅ Grid-based coordinate system  
✅ Simulation state stored in grid class (VirusModel)  
✅ Separate GUI class animates by redrawing (VirusPanel)  
✅ Three-level inheritance (Person → HealthyPerson/SickPerson/RecoveredPerson → VaccinatedPerson/SuperSpreader)  
✅ Six classes minimum in hierarchy  
✅ Grid contains instances of all classes  
✅ Disease spread simulation with configurable parameters  

## Future Enhancements

Possible extra credit features:
1. **JavaDoc API** - Already has JavaDoc comments, can generate with `javadoc *.java`
2. **GUI Input Controls** - Add sliders, text fields for runtime parameter adjustment
3. **Save/Load Feature** - Serialize simulation parameters to file

## Author
Virus Simulation Team  
CS 142 Final Project
