package synchronization;

public class WaitNotifyDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Wait/Notify Mechanism Demo ===\n");
        
        demonstrateBasicWaitNotify();
        demonstrateNotifyAll();
        demonstrateWaitWithTimeout();
    }
    
    private static void demonstrateBasicWaitNotify() {
        System.out.println("1. Basic Wait/Notify:");
        
        class SharedData {
            private boolean dataReady = false;
            private String message;
            
            public synchronized void waitForData() {
                while (!dataReady) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " waiting for data...");
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println(Thread.currentThread().getName() + " received: " + message);
            }
            
            public synchronized void setData(String message) {
                this.message = message;
                this.dataReady = true;
                System.out.println("Data provider set message: " + message);
                notify();
            }
        }
        
        SharedData sharedData = new SharedData();
        
        Thread consumer = new Thread(() -> {
            sharedData.waitForData();
        }, "Consumer");
        
        Thread producer = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            sharedData.setData("Hello from producer!");
        }, "Producer");
        
        consumer.start();
        producer.start();
        
        try {
            consumer.join();
            producer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println();
    }
    
    private static void demonstrateNotifyAll() {
        System.out.println("2. NotifyAll with Multiple Waiters:");
        
        class BroadcastMessage {
            private boolean messageReady = false;
            private String message;
            
            public synchronized void waitForBroadcast() {
                while (!messageReady) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " waiting for broadcast...");
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println(Thread.currentThread().getName() + " received broadcast: " + message);
            }
            
            public synchronized void broadcast(String message) {
                this.message = message;
                this.messageReady = true;
                System.out.println("Broadcasting message: " + message);
                notifyAll();
            }
        }
        
        BroadcastMessage broadcaster = new BroadcastMessage();
        
        Thread[] listeners = new Thread[3];
        for (int i = 0; i < listeners.length; i++) {
            final int listenerId = i + 1;
            listeners[i] = new Thread(() -> {
                broadcaster.waitForBroadcast();
            }, "Listener-" + listenerId);
        }
        
        Thread sender = new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            broadcaster.broadcast("Emergency alert!");
        }, "Broadcaster");
        
        for (Thread listener : listeners) {
            listener.start();
        }
        sender.start();
        
        try {
            for (Thread listener : listeners) {
                listener.join();
            }
            sender.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println();
    }
    
    private static void demonstrateWaitWithTimeout() {
        System.out.println("3. Wait with Timeout:");
        
        class TimedResource {
            private boolean available = false;
            
            public synchronized boolean waitForResource(long timeoutMs) {
                long startTime = System.currentTimeMillis();
                
                while (!available) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    long remaining = timeoutMs - elapsed;
                    
                    if (remaining <= 0) {
                        System.out.println(Thread.currentThread().getName() + " timed out waiting for resource");
                        return false;
                    }
                    
                    try {
                        System.out.println(Thread.currentThread().getName() + " waiting for resource (timeout in " + remaining + "ms)");
                        wait(remaining);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
                
                System.out.println(Thread.currentThread().getName() + " got the resource!");
                return true;
            }
            
            public synchronized void makeAvailable() {
                this.available = true;
                System.out.println("Resource is now available");
                notifyAll();
            }
        }
        
        TimedResource resource = new TimedResource();
        
        Thread quickWaiter = new Thread(() -> {
            resource.waitForResource(1000);
        }, "QuickWaiter");
        
        Thread patientWaiter = new Thread(() -> {
            resource.waitForResource(3000);
        }, "PatientWaiter");
        
        Thread resourceProvider = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            resource.makeAvailable();
        }, "ResourceProvider");
        
        quickWaiter.start();
        patientWaiter.start();
        resourceProvider.start();
        
        try {
            quickWaiter.join();
            patientWaiter.join();
            resourceProvider.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Wait/Notify demo completed!");
    }
}
