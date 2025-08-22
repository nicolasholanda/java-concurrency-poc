package synchronization;

import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumer {
    
    public static void main(String[] args) {
        System.out.println("=== Producer-Consumer Pattern Demo ===\n");
        
        demonstrateBasicProducerConsumer();
        demonstrateMultipleProducersConsumers();
        demonstrateBoundedBuffer();
    }
    
    private static void demonstrateBasicProducerConsumer() {
        System.out.println("1. Basic Producer-Consumer:");
        
        class SimpleBuffer {
            private final Queue<Integer> buffer = new LinkedList<>();
            private final int capacity = 5;
            
            public synchronized void produce(int item) throws InterruptedException {
                while (buffer.size() == capacity) {
                    System.out.println("Buffer full, producer waiting...");
                    wait();
                }
                
                buffer.offer(item);
                System.out.println("Produced: " + item + " (buffer size: " + buffer.size() + ")");
                notifyAll();
            }
            
            public synchronized int consume() throws InterruptedException {
                while (buffer.isEmpty()) {
                    System.out.println("Buffer empty, consumer waiting...");
                    wait();
                }
                
                int item = buffer.poll();
                System.out.println("Consumed: " + item + " (buffer size: " + buffer.size() + ")");
                notifyAll();
                return item;
            }
        }
        
        SimpleBuffer buffer = new SimpleBuffer();
        
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 7; i++) {
                    buffer.produce(i);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");
        
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 7; i++) {
                    buffer.consume();
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");
        
        producer.start();
        consumer.start();
        
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println();
    }
    
    private static void demonstrateMultipleProducersConsumers() {
        System.out.println("2. Multiple Producers and Consumers:");
        
        class SharedBuffer {
            private final Queue<String> buffer = new LinkedList<>();
            private final int capacity = 3;
            
            public synchronized void produce(String item) throws InterruptedException {
                while (buffer.size() == capacity) {
                    wait();
                }
                
                buffer.offer(item);
                System.out.println(Thread.currentThread().getName() + " produced: " + item);
                notifyAll();
            }
            
            public synchronized String consume() throws InterruptedException {
                while (buffer.isEmpty()) {
                    wait();
                }
                
                String item = buffer.poll();
                System.out.println(Thread.currentThread().getName() + " consumed: " + item);
                notifyAll();
                return item;
            }
        }
        
        SharedBuffer buffer = new SharedBuffer();
        
        Thread producer1 = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    buffer.produce("P1-Item" + i);
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer1");
        
        Thread producer2 = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    buffer.produce("P2-Item" + i);
                    Thread.sleep(180);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer2");
        
        Thread consumer1 = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    buffer.consume();
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer1");
        
        Thread consumer2 = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    buffer.consume();
                    Thread.sleep(250);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer2");
        
        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();
        
        try {
            producer1.join();
            producer2.join();
            consumer1.join();
            consumer2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println();
    }
    
    private static void demonstrateBoundedBuffer() {
        System.out.println("3. Bounded Buffer with Statistics:");
        
        class BoundedBuffer {
            private final Queue<Integer> buffer = new LinkedList<>();
            private final int capacity;
            private int totalProduced = 0;
            private int totalConsumed = 0;
            
            public BoundedBuffer(int capacity) {
                this.capacity = capacity;
            }
            
            public synchronized void produce(int item) throws InterruptedException {
                while (buffer.size() == capacity) {
                    System.out.println("Buffer at capacity (" + capacity + "), producer blocked");
                    wait();
                }
                
                buffer.offer(item);
                totalProduced++;
                System.out.println("Produced: " + item + " [" + buffer.size() + "/" + capacity + "] Total: " + totalProduced);
                notifyAll();
            }
            
            public synchronized int consume() throws InterruptedException {
                while (buffer.isEmpty()) {
                    System.out.println("Buffer empty, consumer blocked");
                    wait();
                }
                
                int item = buffer.poll();
                totalConsumed++;
                System.out.println("Consumed: " + item + " [" + buffer.size() + "/" + capacity + "] Total: " + totalConsumed);
                notifyAll();
                return item;
            }
            
            public synchronized void printStats() {
                System.out.println("Final Stats - Produced: " + totalProduced + ", Consumed: " + totalConsumed + ", Remaining: " + buffer.size());
            }
        }
        
        BoundedBuffer buffer = new BoundedBuffer(2);
        
        Thread fastProducer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.produce(i * 10);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "FastProducer");
        
        Thread slowConsumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.consume();
                    Thread.sleep(400);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "SlowConsumer");
        
        fastProducer.start();
        slowConsumer.start();
        
        try {
            fastProducer.join();
            slowConsumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        buffer.printStats();
        System.out.println("Producer-Consumer demo completed!");
    }
}
