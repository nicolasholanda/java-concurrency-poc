package executors;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class CallableFutureDemo {

    public static void main(String[] args) {
        System.out.println("=== Callable and Future Demo ===\n");

        demoBasicCallable();
        demoFutureTimeout();
        demoFutureCancellation();
        demoMultipleCallables();
    }

    static void demoBasicCallable() {
        System.out.println("1. Basic Callable vs Runnable:");
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable runnableTask = () -> {
            System.out.println("Runnable executed by " + Thread.currentThread().getName());
        };

        Callable<String> callableTask = () -> {
            Thread.sleep(500);
            return "Callable result from " + Thread.currentThread().getName();
        };

        Future<?> runnableFuture = executor.submit(runnableTask);
        Future<String> callableFuture = executor.submit(callableTask);

        try {
            runnableFuture.get();
            System.out.println("Callable returned: " + callableFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        System.out.println();
    }

    static void demoFutureTimeout() {
        System.out.println("2. Future with Timeout:");
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Integer> longRunningTask = () -> {
            System.out.println("Starting long task...");
            Thread.sleep(3000);
            return 42;
        };

        Future<Integer> future = executor.submit(longRunningTask);

        try {
            System.out.println("Waiting for result with 1 second timeout...");
            Integer result = future.get(1, TimeUnit.SECONDS);
            System.out.println("Result: " + result);
        } catch (TimeoutException e) {
            System.out.println("Task timed out!");
            future.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        System.out.println();
    }

    static void demoFutureCancellation() {
        System.out.println("3. Future Cancellation:");
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<String> cancellableTask = () -> {
            for (int i = 1; i <= 10; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Task was interrupted at step " + i);
                    throw new InterruptedException("Task cancelled");
                }
                System.out.println("Processing step " + i);
                Thread.sleep(200);
            }
            return "Task completed successfully";
        };

        Future<String> future = executor.submit(cancellableTask);

        try {
            Thread.sleep(800);
            System.out.println("Cancelling task...");
            boolean cancelled = future.cancel(true);
            System.out.println("Cancellation successful: " + cancelled);

            if (!future.isCancelled()) {
                String result = future.get();
                System.out.println("Result: " + result);
            } else {
                System.out.println("Task was cancelled");
            }
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            System.out.println("Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        executor.shutdown();
        System.out.println();
    }

    static void demoMultipleCallables() {
        System.out.println("4. Multiple Callables with Different Processing Times:");
        ExecutorService executor = Executors.newFixedThreadPool(3);

        List<Callable<String>> tasks = new ArrayList<>();

        tasks.add(createTask("Fast", 100));
        tasks.add(createTask("Medium", 500));
        tasks.add(createTask("Slow", 1000));
        tasks.add(createTask("Very Fast", 50));

        List<Future<String>> futures = new ArrayList<>();

        for (Callable<String> task : tasks) {
            futures.add(executor.submit(task));
        }

        for (int i = 0; i < futures.size(); i++) {
            try {
                String result = futures.get(i).get();
                System.out.println("Task " + (i + 1) + " result: " + result);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Task " + (i + 1) + " failed: " + e.getMessage());
            }
        }

        executor.shutdown();
    }

    static Callable<String> createTask(String name, int sleepTime) {
        return () -> {
            System.out.println(name + " task started by " + Thread.currentThread().getName());
            Thread.sleep(sleepTime);
            return name + " task completed in " + sleepTime + "ms";
        };
    }
}
