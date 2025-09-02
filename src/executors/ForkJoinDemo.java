package executors;

import java.util.concurrent.*;
import java.util.Arrays;

public class ForkJoinDemo {

    public static void main(String[] args) {
        System.out.println("=== ForkJoinPool Demo ===\n");

        demoRecursiveTask();
        demoRecursiveAction();
        demoParallelStream();
    }

    static void demoRecursiveTask() {
        System.out.println("1. RecursiveTask - Parallel Sum Calculation:");

        int[] array = new int[10];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1;
        }

        System.out.println("Array: " + Arrays.toString(array));

        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.nanoTime();
        long parallelSum = pool.invoke(new SumTask(array, 0, array.length));
        long parallelTime = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        long sequentialSum = Arrays.stream(array).sum();
        long sequentialTime = System.nanoTime() - startTime;

        System.out.println("Parallel sum: " + parallelSum + " (time: " + parallelTime / 1000 + " μs)");
        System.out.println("Sequential sum: " + sequentialSum + " (time: " + sequentialTime / 1000 + " μs)");
        System.out.println("Pool parallelism: " + pool.getParallelism());

        pool.shutdown();
        System.out.println();
    }

    static void demoRecursiveAction() {
        System.out.println("2. RecursiveAction - Parallel Array Processing:");

        int[] array = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        System.out.println("Original array: " + Arrays.toString(array));

        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new IncrementTask(array, 0, array.length));

        System.out.println("After increment: " + Arrays.toString(array));

        pool.shutdown();
        System.out.println();
    }

    static void demoParallelStream() {
        System.out.println("3. ForkJoinPool with Parallel Streams:");

        int[] numbers = new int[1000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1;
        }

        ForkJoinPool customPool = new ForkJoinPool(2);

        try {
            long sum = customPool.submit(() ->
                Arrays.stream(numbers)
                    .parallel()
                    .mapToLong(i -> {
                        System.out.println("Processing " + i + " in " + Thread.currentThread().getName());
                        return i * i;
                    })
                    .limit(10)
                    .sum()
            ).get();

            System.out.println("Sum of first 10 squares: " + sum);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            customPool.shutdown();
        }
    }

    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 3;
        private final int[] array;
        private final int start;
        private final int end;

        public SumTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;

            if (length <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                System.out.println("Direct computation [" + start + "," + end + ") = " + sum +
                                 " by " + Thread.currentThread().getName());
                return sum;
            } else {
                int mid = start + length / 2;

                System.out.println("Splitting [" + start + "," + end + ") into [" + start + "," + mid +
                                 ") and [" + mid + "," + end + ") by " + Thread.currentThread().getName());

                SumTask leftTask = new SumTask(array, start, mid);
                SumTask rightTask = new SumTask(array, mid, end);

                leftTask.fork();

                long rightResult = rightTask.compute();
                long leftResult = leftTask.join();

                return leftResult + rightResult;
            }
        }
    }

    static class IncrementTask extends RecursiveAction {
        private static final int THRESHOLD = 3;
        private final int[] array;
        private final int start;
        private final int end;

        public IncrementTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            int length = end - start;

            if (length <= THRESHOLD) {
                for (int i = start; i < end; i++) {
                    array[i]++;
                }
                System.out.println("Incremented elements [" + start + "," + end + ") by " +
                                 Thread.currentThread().getName());
            } else {
                int mid = start + length / 2;

                IncrementTask leftTask = new IncrementTask(array, start, mid);
                IncrementTask rightTask = new IncrementTask(array, mid, end);

                invokeAll(leftTask, rightTask);
            }
        }
    }
}
