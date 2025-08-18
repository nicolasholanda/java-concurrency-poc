package threads;

public class ThreadLifecycle {

    public static void main(String[] args) {
        System.out.println("=== Thread Lifecycle and States Demo ===\n");

        demonstrateThreadStates();
        demonstrateThreadPriority();
        demonstrateDaemonThreads();
    }

    private static void demonstrateThreadStates() {
        System.out.println("1. Thread States Demonstration:");

        Thread worker = new Thread(() -> {
            System.out.println("Worker thread started - State: RUNNABLE");

            try {
                System.out.println("Worker going to sleep - State: TIMED_WAITING");
                Thread.sleep(2000);

                System.out.println("Worker woke up - State: RUNNABLE");

                synchronized (ThreadLifecycle.class) {
                    System.out.println("Worker in synchronized block");
                    Thread.sleep(1000);
                }

            } catch (InterruptedException e) {
                System.out.println("Worker was interrupted - State: TERMINATED");
                Thread.currentThread().interrupt();
                return;
            }

            System.out.println("Worker finishing - State: TERMINATED");
        });

        System.out.println("Before start() - State: " + worker.getState());

        worker.start();
        System.out.println("After start() - State: " + worker.getState());

        try {
            Thread.sleep(500);
            System.out.println("While running - State: " + worker.getState());

            Thread.sleep(2000);
            System.out.println("While sleeping - State: " + worker.getState());

            worker.join();
            System.out.println("After join() - State: " + worker.getState());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();
    }

    private static void demonstrateThreadPriority() {
        System.out.println("2. Thread Priority Demonstration:");

        Thread lowPriority = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                System.out.println("Low priority thread: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        Thread highPriority = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                System.out.println("High priority thread: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        lowPriority.setPriority(Thread.MIN_PRIORITY);
        highPriority.setPriority(Thread.MAX_PRIORITY);

        System.out.println("Low priority: " + lowPriority.getPriority());
        System.out.println("High priority: " + highPriority.getPriority());

        lowPriority.start();
        highPriority.start();

        try {
            lowPriority.join();
            highPriority.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();
    }

    private static void demonstrateDaemonThreads() {
        System.out.println("3. Daemon vs User Threads:");

        Thread userThread = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("User thread: " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println("User thread finished");
        });

        // Infinite loop to demonstrate daemon thread behavior
        Thread daemonThread = new Thread(() -> {
            int count = 1;
            while (true) {
                System.out.println("Daemon thread: " + count++);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        daemonThread.setDaemon(true);

        System.out.println("User thread is daemon: " + userThread.isDaemon());
        System.out.println("Daemon thread is daemon: " + daemonThread.isDaemon());

        userThread.start();
        daemonThread.start();

        try {
            userThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Main thread ending (daemon thread will stop automatically)");
        System.out.println("Thread lifecycle demo completed!");
    }
}
