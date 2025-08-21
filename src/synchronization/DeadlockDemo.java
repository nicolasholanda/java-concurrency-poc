package synchronization;

public class DeadlockDemo {
    
    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();
    
    public static void main(String[] args) {
        System.out.println("=== Deadlock Demonstration ===\n");
        
        demonstrateDeadlock();
        demonstrateDeadlockPrevention();
        demonstrateDiningPhilosophers();
    }
    
    private static void demonstrateDeadlock() {
        System.out.println("1. Creating a Deadlock Situation:");
        
        Thread thread1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("Thread1 acquired lock1");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                System.out.println("Thread1 trying to acquire lock2...");
                synchronized (lock2) {
                    System.out.println("Thread1 acquired lock2");
                }
            }
        }, "Thread1");
        
        Thread thread2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("Thread2 acquired lock2");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                System.out.println("Thread2 trying to acquire lock1...");
                synchronized (lock1) {
                    System.out.println("Thread2 acquired lock1");
                }
            }
        }, "Thread2");
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join(2000);
            thread2.join(2000);
            
            if (thread1.isAlive() || thread2.isAlive()) {
                System.out.println("DEADLOCK DETECTED! Threads are stuck.");
                thread1.interrupt();
                thread2.interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println();
    }
    
    private static void demonstrateDeadlockPrevention() {
        System.out.println("2. Preventing Deadlock with Lock Ordering:");
        
        final Object orderedLock1 = new Object();
        final Object orderedLock2 = new Object();
        
        Thread thread1 = new Thread(() -> {
            synchronized (orderedLock1) {
                System.out.println("Thread1 acquired orderedLock1");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                synchronized (orderedLock2) {
                    System.out.println("Thread1 acquired orderedLock2");
                    System.out.println("Thread1 completed successfully");
                }
            }
        }, "Thread1");
        
        Thread thread2 = new Thread(() -> {
            synchronized (orderedLock1) {
                System.out.println("Thread2 acquired orderedLock1");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                synchronized (orderedLock2) {
                    System.out.println("Thread2 acquired orderedLock2");
                    System.out.println("Thread2 completed successfully");
                }
            }
        }, "Thread2");
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("No deadlock with proper lock ordering!");
        System.out.println();
    }
    
    private static void demonstrateDiningPhilosophers() {
        System.out.println("3. Dining Philosophers Problem:");
        
        class Philosopher extends Thread {
            private final int id;
            private final Object leftFork;
            private final Object rightFork;
            
            public Philosopher(int id, Object leftFork, Object rightFork) {
                this.id = id;
                this.leftFork = leftFork;
                this.rightFork = rightFork;
                this.setName("Philosopher-" + id);
            }
            
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 2; i++) {
                        think();
                        eat();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            private void think() throws InterruptedException {
                System.out.println("Philosopher " + id + " is thinking");
                Thread.sleep(100);
            }
            
            private void eat() throws InterruptedException {
                Object firstFork = leftFork.hashCode() < rightFork.hashCode() ? leftFork : rightFork;
                Object secondFork = leftFork.hashCode() < rightFork.hashCode() ? rightFork : leftFork;
                
                synchronized (firstFork) {
                    System.out.println("Philosopher " + id + " picked up first fork");
                    
                    synchronized (secondFork) {
                        System.out.println("Philosopher " + id + " picked up second fork and is eating");
                        Thread.sleep(200);
                        System.out.println("Philosopher " + id + " finished eating and put down forks");
                    }
                }
            }
        }
        
        int numPhilosophers = 3;
        Object[] forks = new Object[numPhilosophers];
        Philosopher[] philosophers = new Philosopher[numPhilosophers];
        
        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new Object();
        }
        
        for (int i = 0; i < numPhilosophers; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % numPhilosophers];
            philosophers[i] = new Philosopher(i, leftFork, rightFork);
        }
        
        for (Philosopher philosopher : philosophers) {
            philosopher.start();
        }
        
        try {
            for (Philosopher philosopher : philosophers) {
                philosopher.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("All philosophers finished dining without deadlock!");
        System.out.println("Deadlock demo completed!");
    }
}
