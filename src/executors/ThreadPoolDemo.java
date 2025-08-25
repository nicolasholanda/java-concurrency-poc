package executors;

import java.util.concurrent.*;

public class ThreadPoolDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Thread Pool Types Demo ===\n");
        
        demoFixedThreadPool();
        demoCachedThreadPool();
        demoSingleThreadExecutor();
        demoScheduledThreadPool();
        demoCustomThreadPool();
    }
    
    static void demoFixedThreadPool() {
        System.out.println("1. Fixed Thread Pool (3 threads):");
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " executed by " + Thread.currentThread().getName());
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }
        
        shutdownExecutor(executor);
        System.out.println();
    }
    
    static void demoCachedThreadPool() {
        System.out.println("2. Cached Thread Pool (creates threads as needed):");
        ExecutorService executor = Executors.newCachedThreadPool();
        
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " executed by " + Thread.currentThread().getName());
                try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }
        
        shutdownExecutor(executor);
        System.out.println();
    }
    
    static void demoSingleThreadExecutor() {
        System.out.println("3. Single Thread Executor (sequential execution):");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        for (int i = 1; i <= 4; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " executed by " + Thread.currentThread().getName());
                try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }
        
        shutdownExecutor(executor);
        System.out.println();
    }
    
    static void demoScheduledThreadPool() {
        System.out.println("4. Scheduled Thread Pool:");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        
        scheduler.schedule(() -> {
            System.out.println("One-time task executed after 1 second by " + Thread.currentThread().getName());
        }, 1, TimeUnit.SECONDS);
        
        ScheduledFuture<?> periodicTask = scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Periodic task executed by " + Thread.currentThread().getName());
        }, 0, 500, TimeUnit.MILLISECONDS);
        
        try {
            Thread.sleep(3000);
            periodicTask.cancel(false);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        shutdownExecutor(scheduler);
        System.out.println();
    }
    
    static void demoCustomThreadPool() {
        System.out.println("5. Custom Thread Pool Configuration:");
        
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
            2,                          // corePoolSize
            4,                          // maximumPoolSize
            60L,                        // keepAliveTime
            TimeUnit.SECONDS,           // timeUnit
            new LinkedBlockingQueue<>(2), // workQueue with capacity 2
            new ThreadFactory() {
                private int threadCount = 0;
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "CustomThread-" + (++threadCount));
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy() // rejection policy
        );
        
        for (int i = 1; i <= 8; i++) {
            final int taskId = i;
            try {
                customExecutor.submit(() -> {
                    System.out.println("Task " + taskId + " executed by " + Thread.currentThread().getName() + 
                                     " (Active: " + customExecutor.getActiveCount() + 
                                     ", Pool: " + customExecutor.getPoolSize() + ")");
                    try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                });
            } catch (RejectedExecutionException e) {
                System.out.println("Task " + taskId + " was rejected!");
            }
        }
        
        shutdownExecutor(customExecutor);
    }
    
    static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
