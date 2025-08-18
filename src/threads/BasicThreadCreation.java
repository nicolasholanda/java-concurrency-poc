package threads;

public class BasicThreadCreation {

    public static void main(String[] args) {
        System.out.println("=== Basic Thread Creation Examples ===\n");

        exampleExtendingThread();
        exampleImplementingRunnable();
        exampleUsingLambda();
        exampleWithAnonymousClass();
    }

    private static void exampleExtendingThread() {
        System.out.println("1. Extending Thread class:");

        class MyThread extends Thread {
            private String name;

            public MyThread(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                for (int i = 1; i <= 3; i++) {
                    System.out.println(name + " executing step " + i);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println(name + " finished");
            }
        }

        MyThread thread1 = new MyThread("ExtendedThread-1");
        MyThread thread2 = new MyThread("ExtendedThread-2");

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();
    }

    private static void exampleImplementingRunnable() {
        System.out.println("2. Implementing Runnable interface:");

        class MyTask implements Runnable {
            private String name;

            public MyTask(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                for (int i = 1; i <= 3; i++) {
                    System.out.println(name + " executing step " + i);
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println(name + " finished");
            }
        }

        Thread thread1 = new Thread(new MyTask("RunnableTask-1"));
        Thread thread2 = new Thread(new MyTask("RunnableTask-2"));

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();
    }

    private static void exampleUsingLambda() {
        System.out.println("3. Using Lambda expressions:");

        Thread thread1 = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("LambdaThread-1 executing step " + i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println("LambdaThread-1 finished");
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("LambdaThread-2 executing step " + i);
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println("LambdaThread-2 finished");
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();
    }

    private static void exampleWithAnonymousClass() {
        System.out.println("4. Using Anonymous Runnable:");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 3; i++) {
                    System.out.println("AnonymousThread executing step " + i);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println("AnonymousThread finished");
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("All thread creation examples completed!");
    }
}
