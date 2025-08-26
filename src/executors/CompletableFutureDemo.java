package executors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;

public class CompletableFutureDemo {

    public static void main(String[] args) {
        System.out.println("=== CompletableFuture Demo ===\n");

        demoBasicAsync();
        demoChainingOperations();
        demoCombiningFutures();
        demoExceptionHandling();
        demoAsyncComposition();
    }

    static void demoBasicAsync() {
        System.out.println("1. Basic Asynchronous Execution:");

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return "Hello from " + Thread.currentThread().getName();
        });

        CompletableFuture<Void> voidFuture = CompletableFuture.runAsync(() -> {
            System.out.println("Running async task in " + Thread.currentThread().getName());
        });

        try {
            System.out.println("Result: " + future.get());
            voidFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    static void demoChainingOperations() {
        System.out.println("2. Chaining Operations:");

        CompletableFuture<String> result = CompletableFuture
            .supplyAsync(() -> {
                System.out.println("Step 1: Getting initial data in " + Thread.currentThread().getName());
                return "Initial Data";
            })
            .thenApply(data -> {
                System.out.println("Step 2: Processing data in " + Thread.currentThread().getName());
                return data.toUpperCase();
            })
            .thenApply(data -> {
                System.out.println("Step 3: Adding suffix in " + Thread.currentThread().getName());
                return data + " - PROCESSED";
            })
            .thenCompose(data -> CompletableFuture.supplyAsync(() -> {
                System.out.println("Step 4: Final transformation in " + Thread.currentThread().getName());
                return "Final: " + data;
            }));

        try {
            System.out.println("Final result: " + result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    static void demoCombiningFutures() {
        System.out.println("3. Combining Multiple Futures:");

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("Future 1 completed");
            return 10;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("Future 2 completed");
            return 20;
        });

        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("Future 3 completed");
            return 30;
        });

        CompletableFuture<Integer> combinedSum = future1
            .thenCombine(future2, (a, b) -> {
                System.out.println("Combining first two results: " + a + " + " + b);
                return a + b;
            })
            .thenCombine(future3, (sum, c) -> {
                System.out.println("Adding third result: " + sum + " + " + c);
                return sum + c;
            });

        CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2, future3)
            .thenRun(() -> System.out.println("All futures completed!"));

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future1, future2, future3)
            .thenApply(result -> {
                System.out.println("First completed result: " + result);
                return result;
            });

        try {
            System.out.println("Combined sum: " + combinedSum.get());
            allOf.get();
            anyOf.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    static void demoExceptionHandling() {
        System.out.println("4. Exception Handling:");

        CompletableFuture<String> futureWithError = CompletableFuture
            .supplyAsync(() -> {
                if (Math.random() > 0.5) {
                    throw new RuntimeException("Random error occurred!");
                }
                return "Success!";
            })
            .exceptionally(throwable -> {
                System.out.println("Handling exception: " + throwable.getMessage());
                return "Default value after error";
            });

        CompletableFuture<String> futureWithHandle = CompletableFuture
            .supplyAsync(() -> {
                throw new RuntimeException("Intentional error for handle demo");
            })
            .handle((result, throwable) -> {
                if (throwable != null) {
                    System.out.println("Handle caught exception: " + throwable.getMessage());
                    return "Recovered from error";
                }
                return result;
            });

        try {
            System.out.println("Result 1: " + futureWithError.get());
            System.out.println("Result 2: " + futureWithHandle.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    static void demoAsyncComposition() {
        System.out.println("5. Async Composition Pattern:");

        CompletableFuture<String> pipeline = CompletableFuture
            .supplyAsync(() -> {
                System.out.println("Fetching user ID...");
                try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return "user123";
            })
            .thenComposeAsync(userId -> {
                System.out.println("Fetching user profile for: " + userId);
                return CompletableFuture.supplyAsync(() -> {
                    try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    return "Profile data for " + userId;
                });
            })
            .thenComposeAsync(profile -> {
                System.out.println("Fetching user preferences for profile...");
                return CompletableFuture.supplyAsync(() -> {
                    try { Thread.sleep(150); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    return profile + " with preferences loaded";
                });
            })
            .thenApplyAsync(finalData -> {
                System.out.println("Formatting final response...");
                return "Final Response: " + finalData;
            });

        try {
            System.out.println(pipeline.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
