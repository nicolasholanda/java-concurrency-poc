# Executors Package

This package demonstrates Java's Executor framework, which provides high-level abstractions for managing threads and executing tasks asynchronously.

## Concepts Covered

### 1. ThreadPoolExecutor (`ThreadPoolDemo.java`)
- Fixed thread pool
- Cached thread pool
- Single thread executor
- Scheduled thread pool
- Custom thread pool configuration

### 2. ExecutorService (`ExecutorServiceDemo.java`)
- Task submission with submit()
- Future handling
- Graceful shutdown
- Timeout handling

### 3. CompletableFuture (`CompletableFutureDemo.java`)
- Asynchronous task execution
- Chaining operations
- Combining multiple futures
- Exception handling

### 4. ForkJoinPool (`ForkJoinDemo.java`)
- Work-stealing algorithm
- Recursive task decomposition
- Parallel processing

### 5. Callable and Future (`CallableFutureDemo.java`)
- Tasks that return results
- Future.get() with timeout
- Task cancellation

## Key Benefits of Executors
- Thread reuse (better performance)
- Resource management
- Task queuing
- Graceful shutdown
- Exception handling
- Monitoring capabilities

## Running Examples
Each class contains a main method that demonstrates the specific executor concept.
