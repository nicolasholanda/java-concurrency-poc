# Synchronization Mechanisms

This package demonstrates various synchronization mechanisms in Java to solve the race condition problems shown in the threads package.

## Concepts Covered

1. **Synchronized Methods** - Method-level synchronization for thread safety
2. **Synchronized Blocks** - Fine-grained locking with specific objects
3. **Wait/Notify Mechanism** - Thread communication and coordination
4. **Deadlock Prevention** - Understanding and avoiding deadlock scenarios
5. **Producer-Consumer Pattern** - Classic synchronization problem

## How to Run

Each demo has its own main method. Run them individually:

```bash
javac src/synchronization/*.java
java -cp src synchronization.SynchronizedMethods
java -cp src synchronization.SynchronizedBlocks
java -cp src synchronization.WaitNotifyDemo
java -cp src synchronization.DeadlockDemo
java -cp src synchronization.ProducerConsumer
```

## Demo Files

- `SynchronizedMethods.java` - Shows method-level synchronization
- `SynchronizedBlocks.java` - Demonstrates block-level synchronization
- `WaitNotifyDemo.java` - Thread communication with wait/notify
- `DeadlockDemo.java` - Deadlock scenarios and prevention
- `ProducerConsumer.java` - Classic producer-consumer implementation

## Key Takeaways

- Synchronization prevents race conditions but introduces performance overhead
- Choose the right synchronization granularity (method vs block)
- Always be aware of potential deadlocks when using multiple locks
- Wait/notify provides efficient thread communication
- Proper synchronization is essential for thread-safe applications
