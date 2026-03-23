# Virus Spread Simulation - Final Project

## Project Overview
This project is a Java based virus simulation that models disease spread through a population. The simulation demonstrates how a certian disease spreads based on closeness, immunity, and indivdual characteristics.

## Class Hierarchy (Structure)

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
- Tracks people and their positions
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

## Design Patterns Used

- **Inheritance**: 3-level hierarchy with proper OOP design
- **Observer Pattern**: Model notifies GUI of changes
- **Polymorphism**: All Person subclasses handled uniformly in grid

## What I Learned
- Applied objected oriented programming by designing a multi level class hierarchy with inheritance and polymorphism
- Used design pattern such as the Observer Pattern which seperates the simulation from the user interface
- Modeled a real world disease spread using probability such as infection rates, recovery and mortality
- Improved debugging and testing skills

