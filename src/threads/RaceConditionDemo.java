package threads;

public class RaceConditionDemo {

    private static int counter = 0;
    private static int synchronizedCounter = 0;

    public static void main(String[] args) {
        System.out.println("=== Race Condition Demonstration ===\n");

        demonstrateRaceCondition();
        demonstrateSafeAccess();
        demonstrateBankAccountProblem();
    }

    private static void demonstrateRaceCondition() {
        System.out.println("1. Race Condition with Shared Counter:");
        counter = 0;

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter++;
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter++;
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

        System.out.println("Expected result: 2000");
        System.out.println("Actual result: " + counter);
        System.out.println("Data corrupted: " + (counter != 2000));
        System.out.println();
    }

    private static void demonstrateSafeAccess() {
        System.out.println("2. Safe Access with Synchronization:");
        synchronizedCounter = 0;

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (RaceConditionDemo.class) {
                    synchronizedCounter++;
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (RaceConditionDemo.class) {
                    synchronizedCounter++;
                }
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

        System.out.println("Expected result: 2000");
        System.out.println("Actual result: " + synchronizedCounter);
        System.out.println("Data corrupted: " + (synchronizedCounter != 2000));
        System.out.println();
    }

    private static void demonstrateBankAccountProblem() {
        System.out.println("3. Bank Account Transfer Problem:");

        class BankAccount {
            private double balance;

            public BankAccount(double initialBalance) {
                this.balance = initialBalance;
            }

            public void withdraw(double amount) {
                if (balance >= amount) {
                    System.out.println(Thread.currentThread().getName() + " checking balance: " + balance);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    balance -= amount;
                    System.out.println(Thread.currentThread().getName() + " withdrew " + amount + ", new balance: " + balance);
                } else {
                    System.out.println(Thread.currentThread().getName() + " insufficient funds for " + amount);
                }
            }

            public double getBalance() {
                return balance;
            }
        }

        BankAccount account = new BankAccount(100.0);

        Thread user1 = new Thread(() -> {
            account.withdraw(75.0);
        }, "User1");

        Thread user2 = new Thread(() -> {
            account.withdraw(50.0);
        }, "User2");

        System.out.println("Initial balance: " + account.getBalance());

        user1.start();
        user2.start();

        try {
            user1.join();
            user2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final balance: " + account.getBalance());
        System.out.println("Balance should not be negative!");
        System.out.println("Race condition demo completed!");
    }
}
