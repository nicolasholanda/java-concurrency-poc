# Threads Fundamentals

This package demonstrates the basic concepts of Java threads and concurrent execution.

## Concepts Covered

1. **Thread Creation** - Different ways to create and start threads
2. **Thread Lifecycle** - Understanding thread states and transitions
3. **Race Conditions** - Problems that occur without proper synchronization
4. **Thread Coordination** - Using join(), sleep(), and interrupt()

## How to Run

Each demo has its own main method. Run them individually:

```bash
javac src/threads/*.java
java -cp src threads.BasicThreadCreation
java -cp src threads.ThreadLifecycle
java -cp src threads.RaceConditionDemo
java -cp src threads.ThreadCoordination
```

## Demo Files

- `BasicThreadCreation.java` - Shows different ways to create threads
- `ThreadLifecycle.java` - Demonstrates thread states and transitions
- `RaceConditionDemo.java` - Shows what happens without synchronization
- `ThreadCoordination.java` - Examples of coordinating threads with join/sleep/interrupt

## Key Takeaways

- Threads allow concurrent execution but introduce complexity
- Race conditions occur when multiple threads access shared data
- Proper coordination is essential for predictable behavior
- Understanding thread states helps debug concurrent programs
