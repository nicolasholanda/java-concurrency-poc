package synchronization;

public class SynchronizedBlocks {
    
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    private int sharedResource1 = 0;
    private int sharedResource2 = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Synchronized Blocks Demo ===\n");
        
        SynchronizedBlocks demo = new SynchronizedBlocks();
        demo.demonstrateFineGrainedLocking();
        demo.demonstrateDifferentLockObjects();
        demo.demonstrateClassLevelLocking();
    }
    
    private void demonstrateFineGrainedLocking() {
        System.out.println("1. Fine-grained Locking vs Method Locking:");
        
        class DataProcessor {
            private int data1 = 0;
            private int data2 = 0;
            private final Object lock1 = new Object();
            private final Object lock2 = new Object();
            
            public void processData1() {
                synchronized (lock1) {
                    data1++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("Data1 processed: " + data1);
                }
            }
            
            public void processData2() {
                synchronized (lock2) {
                    data2++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("Data2 processed: " + data2);
                }
            }
            
            public synchronized void processDataSynchronized() {
                data1++;
                data2++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Both data processed together: " + data1 + ", " + data2);
            }
        }
        
        DataProcessor processor = new DataProcessor();
        
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                processor.processData1();
            }
        }, "Data1-Worker");
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                processor.processData2();
            }
        }, "Data2-Worker");
        
        long startTime = System.currentTimeMillis();
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Fine-grained locking completed in: " + (endTime - startTime) + "ms");
        System.out.println();
    }
    
    private void demonstrateDifferentLockObjects() {
        System.out.println("2. Different Lock Objects:");
        
        Thread worker1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("Worker1 acquired lock1");
                sharedResource1++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Worker1 releasing lock1, resource1: " + sharedResource1);
            }
        });
        
        Thread worker2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("Worker2 acquired lock2");
                sharedResource2++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Worker2 releasing lock2, resource2: " + sharedResource2);
            }
        });
        
        Thread worker3 = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            synchronized (lock1) {
                System.out.println("Worker3 acquired lock1 (had to wait)");
                sharedResource1++;
                System.out.println("Worker3 releasing lock1, resource1: " + sharedResource1);
            }
        });
        
        worker1.start();
        worker2.start();
        worker3.start();
        
        try {
            worker1.join();
            worker2.join();
            worker3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println();
    }
    
    private void demonstrateClassLevelLocking() {
        System.out.println("3. Class-level Locking:");
        
        class SharedCounter {
            private static int globalCounter = 0;
            
            public static void incrementGlobal() {
                synchronized (SharedCounter.class) {
                    globalCounter++;
                    System.out.println(Thread.currentThread().getName() + " incremented global to: " + globalCounter);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            
            public static int getGlobalCounter() {
                synchronized (SharedCounter.class) {
                    return globalCounter;
                }
            }
        }
        
        Thread[] threads = new Thread[3];
        
        for (int i = 0; i < threads.length; i++) {
            final int threadNum = i + 1;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 2; j++) {
                    SharedCounter.incrementGlobal();
                }
            }, "Thread-" + threadNum);
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Final global counter: " + SharedCounter.getGlobalCounter());
        System.out.println("Synchronized blocks demo completed!");
    }
}
