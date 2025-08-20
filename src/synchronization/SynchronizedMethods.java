package synchronization;

public class SynchronizedMethods {
    
    public static void main(String[] args) {
        System.out.println("=== Synchronized Methods Demo ===\n");
        
        demonstrateInstanceMethodSynchronization();
        demonstrateStaticMethodSynchronization();
        demonstrateBankAccountFixed();
    }
    
    private static void demonstrateInstanceMethodSynchronization() {
        System.out.println("1. Instance Method Synchronization:");
        
        class Counter {
            private int count = 0;
            
            public synchronized void increment() {
                count++;
            }
            
            public synchronized void decrement() {
                count--;
            }
            
            public synchronized int getCount() {
                return count;
            }
        }
        
        Counter counter = new Counter();
        
        Thread incrementer = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        });
        
        Thread decrementer = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.decrement();
            }
        });
        
        incrementer.start();
        decrementer.start();
        
        try {
            incrementer.join();
            decrementer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Final count: " + counter.getCount());
        System.out.println("Expected: 0 (should always be 0 with synchronization)");
        System.out.println();
    }
    
    private static void demonstrateStaticMethodSynchronization() {
        System.out.println("2. Static Method Synchronization:");
        
        class StaticCounter {
            private static int staticCount = 0;
            
            public static synchronized void incrementStatic() {
                staticCount++;
            }
            
            public static synchronized int getStaticCount() {
                return staticCount;
            }
        }
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 500; i++) {
                StaticCounter.incrementStatic();
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 500; i++) {
                StaticCounter.incrementStatic();
            }
        });
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Final static count: " + StaticCounter.getStaticCount());
        System.out.println("Expected: 1000");
        System.out.println();
    }
    
    private static void demonstrateBankAccountFixed() {
        System.out.println("3. Bank Account with Synchronized Methods:");
        
        class SafeBankAccount {
            private double balance;
            
            public SafeBankAccount(double initialBalance) {
                this.balance = initialBalance;
            }
            
            public synchronized boolean withdraw(double amount) {
                if (balance >= amount) {
                    System.out.println(Thread.currentThread().getName() + " checking balance: " + balance);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                    balance -= amount;
                    System.out.println(Thread.currentThread().getName() + " withdrew " + amount + ", new balance: " + balance);
                    return true;
                } else {
                    System.out.println(Thread.currentThread().getName() + " insufficient funds for " + amount);
                    return false;
                }
            }
            
            public synchronized void deposit(double amount) {
                balance += amount;
                System.out.println(Thread.currentThread().getName() + " deposited " + amount + ", new balance: " + balance);
            }
            
            public synchronized double getBalance() {
                return balance;
            }
        }
        
        SafeBankAccount account = new SafeBankAccount(100.0);
        
        Thread user1 = new Thread(() -> {
            account.withdraw(75.0);
        }, "User1");
        
        Thread user2 = new Thread(() -> {
            account.withdraw(50.0);
        }, "User2");
        
        Thread depositor = new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            account.deposit(25.0);
        }, "Depositor");
        
        System.out.println("Initial balance: " + account.getBalance());
        
        user1.start();
        user2.start();
        depositor.start();
        
        try {
            user1.join();
            user2.join();
            depositor.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Final balance: " + account.getBalance());
        System.out.println("Balance is never negative with synchronization!");
        System.out.println("Synchronized methods demo completed!");
    }
}
