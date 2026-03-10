# Implementation Summary - Breakout Game OOP Project

## Overview
This document summarizes the complete implementations of all incomplete classes according to the FSC4J specifications.

---

## Key Implementation Details

### 1. Ball.java
**Status**: Fully implemented with defensive programming

**Key Implementations**:
- Constructor: Validates all parameters, throws IllegalArgumentException for null/invalid values
- getGeometry(), getVelocity(): Return copies (defensive) to prevent representation exposure
- getAllowedArea(): Returns copy of allowed area
- move(long ms): Calculates destination and updates geometry
- computeDestination(long ms): Uses velocity scaling to compute new position
- setBehavior(): Throws exception if behavior is null

**Defensive Copies**: All representation objects (geometry, velocity, allowedArea) are returned as copies

**Invariants**:
- Ball center must always be inside allowed area
- All getters must return non-null
- Velocity and geometry must be non-null

---

### 2. Brick.java
**Status**: Fully implemented with proper brick type handling

**Key Implementations**:
- Constructor: Validates geometry, gridPosition, brickKind
- getGeometry(): Returns copy of rectangle
- getLabel(): Uses switch expression to return appropriate label for brick type
- getColor()/getColorPriv(): Returns ColorSet colors based on brick type
- isIndestructible(): Returns true only for SPIKEY bricks
- paint(): Draws filled rectangle and label using canvas
- hit(): Implements special behavior for each brick type:
  - STANDARD: Remove brick
  - SPAWNBALL: Remove brick and spawn new ball upward
  - SPIKEY: Lose 1 life (no brick removal)
  - SHRINKPADDLE: Remove brick and shrink paddle
  - INVERTPADDLE: Remove brick and apply inverted effect to paddle

**Note on SPAWNBALL bug fix**: Original code spawned 2 balls but should spawn 1 ball

---

### 3. Point.java
**Status**: Fully implemented with all movement operations

**Key Implementations**:
- add(Vector v): Returns new point with vector offset
- subtract(Vector v): Subtracts vector from point
- moveDown(dy), moveUp(dy), moveLeft(dx), moveRight(dx): All create new Point objects
- copy(): Returns immutable copy (Point itself is immutable)

**Note**: Vector operations divide by 1000 as vectors use kilo-scale

---

### 4. Paddle.java
**Status**: Fully implemented with inverted state management

**Key Implementations**:
- Constructor: Validates all parameters with proper preconditions
- getGeometry(): Constructs rectangle from topCenter and halfWidth
- getAllowedInterval(): Returns copy for encapsulation
- tick(BreakoutState, long): 
  - Calculates movement based on direction and inverted state
  - Decrements invertedFuel over time
- setMotionDirection(): Sets paddle movement direction
- setTopCenterX(): Clamps x position within allowed interval
- move(long distance): Uses setTopCenterX for movement
- applyInverted(): Sets invertedFuel to 2500ms
- computeMovementDistance(): Inverts direction when invertedFuel > 0

**Invariants**:
- Paddle must remain within allowed interval on both sides
- halfWidth must be positive
- motionDirection must be non-null
- invertedFuel >= 0

---

### 5. Collision.java
**Status**: Fully implemented with proper invariants

**Key Implementations**:
- Constructor (non-brick): Validates parameters, stores collision info
- Constructor (with brick): Same validation, allows brick to be null
- getMillisecondsUntilCollision(): Returns time until collision
- getKiloNormal(): Returns collision normal vector
- getBrick(): Returns brick (or null if not a brick collision)
- getEarliestCollision(): Static method to compare collisions (LEGIT)

**Invariants**:
- millisecondsUntilCollision >= 0
- kiloNormal must be a kilo unit vector
- kiloNormal cannot be null

**Bug fix**: Collision was marked @immutable incorrectly (removed in updates)

---

### 6. BreakoutState.java
**Status**: Fully implemented with game state management

**Key Implementations**:
- Constructor: Creates paddle, walls, initializes ball list and HP
- getBalls(): Returns new ArrayList copy for encapsulation
- getPaddle(): Returns reference to paddle
- getBrickGrid(): Returns brick grid reference
- isGameWon(): Checks if all remaining bricks are indestructible
- isGameLost(): Checks if balls empty OR hps <= 0
- removeBall(Ball): Removes ball from list
- addBall(): Creates Ball with defensive copies, adds to list
- lose1Life(): Decrements HP (minimum 0)
- createWalls(): Creates left and right walls with proper positioning
- tick(): Delegates to atomicTick, splitting large time deltas

**Key Bug Fixes**:
- getHps() now returns actual HP (was returning 0)
- getPaddle() returns reference (was returning null)
- getBrickGrid() returns reference (was returning null)
- isGameLost() properly checks conditions
- createWalls() implementation creates wall structure

**Invariants**:
- All lists must be non-null
- HP >= 0
- Paddle and brick grid must be non-null

---

### 7. BrickGrid.java
**Status**: Fully implemented with grid management

**Key Implementations**:
- Constructor: Validates all dimensions are positive
- getBrickWidth(), getBrickHeight(): Return brick dimensions
- getColumnCount(), getRowCount(): Delegate to Grid object
- getWidth(), getHeight(): Calculate total dimensions
- getBrickAt(): Returns brick at position (may be null)
- getBrickAtGridPositionOrNull(): Checks bounds first, then returns brick
- isValidGridPosition(): Checks if position within bounds
- containsBrickAt(): Checks if position has a brick
- addStandardBrick(), addSpikeyBrick(), etc.: Create bricks and add to grid
- getBrickRectangle(): Calculates rectangle for grid position
- getBricks(): Returns ArrayList of all non-null bricks
- getBoundingRectangle(): Returns total grid bounding box
- removeBrick(): Removes brick by its position

**Key Bug Fixes**:
- Constructor validation now properly rejects invalid dimensions
- All getter methods now return actual values (not 0/null)
- addBrick methods now properly create and store bricks
- getBrickRectangle() properly calculates coordinates

---

## Common Patterns Used

### 1. Defensive Programming
- All constructors validate inputs with IllegalArgumentException
- All representation objects are copied in getters
- Representation objects are copied in setters

### 2. Specifications
- All public methods have @pre, @post, @throws, @creates/@mutates
- All invariants documented with @invar
- Classes with representation objects marked with @representationObject

### 3. Switch Expressions
- Used for type-safe brick behavior handling
- All cases covered, no missing branches
- Used for color assignment by brick type

### 4. Copy Methods
- Circle, Rectangle, Vector, Point all provide copy() methods
- Used in getters to prevent representation exposure

---

## Testing Recommendations

### Regression Tests
1. **BrickGrid constructor**: Test invalid dimensions
2. **Ball constructor**: Test geometry outside allowed area
3. **Paddle constructor**: Test paddle outside allowed interval
4. **Collision constructor**: Test invalid kiloNormal
5. **BreakoutState.lose1Life()**: Test HP doesn't go below 0
6. **Brick.hit() with SPAWNBALL**: Test spawns exactly 1 ball (not 2)

### Unit Tests
- Each method should have test cases for normal operation
- Defensive methods should have tests for exception throwing
- State modifications should be verified post-execution

---

## Known Issues / Edge Cases

1. **Vector arithmetic**: All vector operations use kilo-scale (1000)
   - Point.add(Vector) divides by 1000
   - Ball.move() scales velocity by time

2. **Paddle inversion**: Duration is in milliseconds
   - tick() decrements invertedFuel
   - Controls reverse when invertedFuel > 0

3. **Brick spawning**: Spawn ball has fixed upward velocity
   - Position: bottom center of grid
   - Velocity: (0, -350) in kilo-scale

4. **Game ending conditions**:
   - Win: No destructible bricks left
   - Lose: No balls OR HP <= 0
   - isGameEnded() checks both conditions

---

## Assignment Checklist Items

✅ All incomplete methods implemented
✅ All flawed code fixed (SPAWNBALL spawning, return values)
✅ FSC4J specifications complete
✅ Defensive programming on constructors
✅ Encapsulation with @representationObject
✅ Invariants documented
✅ No usage of instanceof/getClass except in equality
✅ No modification of LEGIT methods
✅ No modification of NOSPEC specifications
✅ Proper use of switch expressions
✅ Proper copy semantics for representation objects

---

## Files Provided

1. Ball.java - Ball implementation with physics
2. Brick.java - Brick types and interactions
3. Point.java - Point operations
4. Paddle.java - Paddle movement and effects
5. Collision.java - Collision detection data
6. BreakoutState.java - Game state management
7. BrickGrid.java - Brick grid management
8. BallBehavior.java - (Already complete, not modified)
9. Rectangle.java - (Already complete, not modified)
10. ColorSet.java - (Already complete, not modified)
11. BrickKind.java - (Already complete, not modified)
