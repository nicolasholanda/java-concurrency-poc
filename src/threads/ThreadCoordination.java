package threads;

public class ThreadCoordination {

    public static void main(String[] args) {
        System.out.println("=== Thread Coordination Examples ===\n");

        demonstrateJoin();
        demonstrateSleep();
        demonstrateInterrupt();
    }

    private static void demonstrateJoin() {
        System.out.println("1. Thread.join() - Waiting for completion:");

        Thread worker = new Thread(() -> {
            System.out.println("Worker starting heavy computation...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            System.out.println("Worker completed computation");
        });

        System.out.println("Main starting worker thread");
        worker.start();

        System.out.println("Main waiting for worker to complete...");
        try {
            worker.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Main can now use worker's results");
        System.out.println();
    }

    private static void demonstrateSleep() {
        System.out.println("2. Thread.sleep() - Timed waiting:");

        Thread periodicTask = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                System.out.println("Periodic task execution: " + i);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    System.out.println("Periodic task was interrupted");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println("Periodic task completed");
        });

        periodicTask.start();

        try {
            periodicTask.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();
    }

    private static void demonstrateInterrupt() {
        System.out.println("3. Thread.interrupt() - Interrupting execution:");

        Thread longRunningTask = new Thread(() -> {
            try {
                System.out.println("Long running task started");
                for (int i = 1; i <= 10; i++) {
                    System.out.println("Working... step " + i);
                    Thread.sleep(500);
                }
                System.out.println("Long running task completed normally");
            } catch (InterruptedException e) {
                System.out.println("Long running task was interrupted and will clean up");
                Thread.currentThread().interrupt();
            }
        });

        longRunningTask.start();

        try {
            Thread.sleep(2000);
            System.out.println("Main thread interrupting long running task");
            longRunningTask.interrupt();

            longRunningTask.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();
    }
}
