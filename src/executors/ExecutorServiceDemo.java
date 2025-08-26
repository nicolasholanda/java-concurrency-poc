package executors;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class ExecutorServiceDemo {
    
    public static void main(String[] args) {
        System.out.println("=== ExecutorService Demo ===\n");
        
        demoBasicSubmission();
        demoFutureHandling();
        demoInvokeAll();
        demoInvokeAny();
        demoGracefulShutdown();
    }
    
    static void demoBasicSubmission() {
        System.out.println("1. Basic Task Submission:");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        executor.submit(() -> {
            System.out.println("Runnable task executed by " + Thread.currentThread().getName());
        });
        
        Future<String> future = executor.submit(() -> {
            Thread.sleep(500);
            return "Callable task result from " + Thread.currentThread().getName();
        });
        
        try {
            System.out.println("Result: " + future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        
        executor.shutdown();
        System.out.println();
    }
    
    static void demoFutureHandling() {
        System.out.println("2. Future Handling with Timeout:");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        Future<Integer> quickTask = executor.submit(() -> {
            Thread.sleep(100);
            return 42;
        });
        
        Future<Integer> slowTask = executor.submit(() -> {
            Thread.sleep(2000);
            return 100;
        });
        
        try {
            System.out.println("Quick task result: " + quickTask.get(500, TimeUnit.MILLISECONDS));
            
            System.out.println("Slow task result: " + slowTask.get(500, TimeUnit.MILLISECONDS));
        } catch (TimeoutException e) {
            System.out.println("Slow task timed out!");
            slowTask.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        
        executor.shutdown();
        System.out.println();
    }
    
    static void demoInvokeAll() {
        System.out.println("3. InvokeAll - Execute Multiple Tasks:");
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            final int taskId = i;
            tasks.add(() -> {
                Thread.sleep(taskId * 200);
                return "Task " + taskId + " completed by " + Thread.currentThread().getName();
            });
        }
        
        try {
            List<Future<String>> results = executor.invokeAll(tasks);
            
            for (Future<String> result : results) {
                System.out.println(result.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        
        executor.shutdown();
        System.out.println();
    }
    
    static void demoInvokeAny() {
        System.out.println("4. InvokeAny - Get First Completed Task:");
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        List<Callable<String>> tasks = new ArrayList<>();
        tasks.add(() -> {
            Thread.sleep(800);
            return "Slow task completed";
        });
        tasks.add(() -> {
            Thread.sleep(300);
            return "Medium task completed";
        });
        tasks.add(() -> {
            Thread.sleep(100);
            return "Fast task completed";
        });
        
        try {
            String result = executor.invokeAny(tasks);
            System.out.println("First completed: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        
        executor.shutdown();
        System.out.println();
    }
    
    static void demoGracefulShutdown() {
        System.out.println("5. Graceful Shutdown:");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    Thread.sleep(1000);
                    System.out.println("Task " + taskId + " completed");
                } catch (InterruptedException e) {
                    System.out.println("Task " + taskId + " was interrupted");
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        System.out.println("Initiating shutdown...");
        executor.shutdown();
        
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                System.out.println("Forcing shutdown...");
                List<Runnable> pendingTasks = executor.shutdownNow();
                System.out.println("Pending tasks: " + pendingTasks.size());
                
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    System.out.println("Executor did not terminate gracefully");
                }
            } else {
                System.out.println("All tasks completed gracefully");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
