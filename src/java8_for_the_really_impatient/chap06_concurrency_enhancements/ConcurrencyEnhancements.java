package java8_for_the_really_impatient.chap06_concurrency_enhancements;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.StampedLock;

/**
 * Created by sofia on 1/1/17.
 */
public class ConcurrencyEnhancements {

    private static class Vector {
        private int size;
        private Object[] elements;
        private StampedLock lock = new StampedLock();

        public Object get(int n) {
            long stamp = lock.tryOptimisticRead();
            Object[] currentElements = elements;
            int currentSize = size;

            if (!lock.validate(stamp)) { // someone else had a write lock
                stamp = lock.readLock(); // get a pessimistic lock
                currentElements = elements;
                currentSize = size;
                lock.unlockRead(stamp);
            }

            return n < currentSize ? currentElements[n] : null;
        }
    }


    public static void main(String... args) {

        /**
         * Atomic Values
         */

        // Java 7
        AtomicLong largest = new AtomicLong();
        Long observed = 10L;
        Long oldValue, newValue;

        do {
            oldValue = largest.get();
            newValue = Math.max(oldValue, observed);
        } while (!largest.compareAndSet(oldValue, newValue));

        // Java 8
        largest.updateAndGet(x -> Math.max(x, observed));
        largest.accumulateAndGet(observed, Math::max);

        final LongAdder adder = new LongAdder();
//        for (...) {
//            pool.submit(() -> {
//                while (...) {
//                    ...
//                }
//                if (...) {
//                    adder.increment();
//                }
//            }
//        });
        long total = adder.sum();

        LongAccumulator accumulator = new LongAccumulator(Long::sum, 0);
        long value = 10L;
        accumulator.accumulate(value);


        /**
         * Concurrent Hash Map
         */

        // Java 7
        ConcurrentHashMap<String, Long> map1 = new ConcurrentHashMap<>();
        String word = "word";
        do {
            oldValue = map1.get(word);
            newValue = oldValue == null ? 1 : oldValue + 1;
        } while (!map1.replace(word, oldValue, newValue));
        map1.put(word, newValue);

        ConcurrentHashMap<String, AtomicLong> map2 = new ConcurrentHashMap<>();
        AtomicLong oldVal = map2.get(word);
        AtomicLong newVal = oldVal == null ? new AtomicLong(1) : new AtomicLong(oldVal.longValue() + 1);
        map2.put(word, newVal);

        // Java 8
        ConcurrentHashMap<String, LongAdder> map3 = new ConcurrentHashMap<>();

        map3.putIfAbsent(word, new LongAdder());
        map3.get(word).increment();

        map3.putIfAbsent(word, new LongAdder()).increment();

        map3.computeIfAbsent(word, k -> new LongAdder()).increment();

        ConcurrentHashMap<String, Long> map4 = new ConcurrentHashMap<>();

        map4.compute(word, (k, v) -> v == null ? 1 : v + 1);

        map4.merge(word, 1L, (v1, v2) -> v1 + v2);
        map4.merge(word, 1L, Long::sum);

        int threshold = 10; // threshold for processing by one thread
        String result = map4.search(threshold, (k, v) -> v > 1000 ? k : null);

        map4.forEach(threshold,
                (k, v) -> System.out.println(k + " -> " + v));

        map4.forEach(threshold,
                (k, v) -> k + " -> " + v, // transformer
                System.out::println); // consumer

        map4.forEach(threshold,
                (k, v) -> v > 1000 ? k + " -> " + v : null, // filter and transformer
                System.out::println); // consumer

        Long sum = map4.reduceValues(threshold, Long::sum);

        Integer maxLength = map4.reduceKeys(threshold,
                String::length, // transformer
                Integer::max); // accumulator

        Long count = map4.reduceValues(threshold,
                v -> v > 1000 ? 1L : null, // filter and transformer
                Long::sum); // accumulator

        long sum2 = map4.reduceValuesToLong(threshold,
                Long::longValue, // transformer to primitive type
                0, // default value for empty map
                Long::sum); // primitive type accumulator

        Set<String> words = ConcurrentHashMap.<String>newKeySet();

        Set<String> words2 = map4.keySet(1L);
        words2.add("Java");






































    }

}
