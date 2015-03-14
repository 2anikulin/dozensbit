package net.dozensbit.benchmark;

import net.dozensbit.cache.Cache;
import net.dozensbit.cache.IndexedCache;
import net.dozensbit.cache.SearchListener;
import net.dozensbit.cache.query.QueryBuilder;
import org.apache.commons.collections.map.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Dozensbit cache benchmark;
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class DozensBitTest
{
    private final int OBJECTS_COUNT;
    private final int TEST_COUNT;
    private final int THREADS_COUNT;

    public DozensBitTest(final int objectsCount, final int testsCount, final int threadCount)
    {
        OBJECTS_COUNT = objectsCount;
        TEST_COUNT = testsCount;
        THREADS_COUNT = threadCount;
    }

    /**
     * Single thread test.
     * all objects includes to output result.
     */
    public void allInclusiveSingleThread()
    {
        System.out.println("=========================================================");
        System.out.println("Dozensbit - all inclusive. Single thread");

        Cache<Object> cache = new IndexedCache<Object>();

        MultiValueMap tags = new MultiValueMap();

        tags.put("city", "omsk");
        tags.put("city", "tomsk");
        tags.put("city", "novosibirsk");
        tags.put("city", "moscow");
        tags.put("gender", "male");
        tags.put("lang", "ru");
        tags.put("lang", "de");
        tags.put("lang", "en");
        tags.put("lang", "au");
        tags.put("lang", "it");

        for (int i = 0; i < OBJECTS_COUNT; i++) {
            cache.put(Integer.valueOf(i), tags);
        }

        cache.rebuild();


        long avg = 0;
        for (int i = 0; i < TEST_COUNT; i++) {
            long start = System.nanoTime();

            QueryBuilder builder = cache.createQuery();

            QueryBuilder.Query query = builder
                    .start("city", "omsk")
                    .and("city", "tomsk")
                    .and("city", "novosibirsk")
                    .and("city", "moscow")
                    .and("gender", "male")
                    .and("lang", "ru")
                    .and("lang", "de")
                    .and("lang", "en")
                    .and("lang", "au")
                    .and("lang", "it")
                    .or("lang", Integer.toString(i))
                    .get();

            List<Object> result = cache.find(query);

            long time = System.nanoTime() - start;

            //System.out.println(String.format("%d ns, %f ms", time, time / 1000000.0));
            avg+= time;

            if (result.size() != OBJECTS_COUNT) {
                System.out.println("Error! size: " + result.size() );
                break;
            }
        }

        System.out.println(String.format("Average time %d ns, %f ms", avg / TEST_COUNT, (avg / TEST_COUNT) / 1000000.0));
        System.out.println("Objects in cache: " + OBJECTS_COUNT);
        System.out.println("Tests count: " + TEST_COUNT);
        System.out.println("=========================================================");

    }

    /**
     * Multi thread test.
     * all objects includes to output result.
     *
     * @throws InterruptedException
     */
    public void allInclusiveMultiThread() throws InterruptedException
    {
        System.out.println("=========================================================");
        System.out.println("Dozensbit - all inclusive. Multi-thread");

        final Cache<Object> cache = new IndexedCache<Object>();

        MultiValueMap tags = new MultiValueMap();

        tags.put("city", "omsk");
        tags.put("city", "tomsk");
        tags.put("city", "novosibirsk");
        tags.put("city", "moscow");
        tags.put("gender", "male");
        tags.put("lang", "ru");
        tags.put("lang", "de");
        tags.put("lang", "en");
        tags.put("lang", "au");
        tags.put("lang", "it");

        for (int i = 0; i < OBJECTS_COUNT; i++) {
            cache.put(Integer.valueOf(i), tags);
        }

        cache.rebuild();


        ExecutorService service = Executors.newFixedThreadPool(THREADS_COUNT);
        final CyclicBarrier barrier = new CyclicBarrier(THREADS_COUNT);
        final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);

        for (int i = 0; i < THREADS_COUNT; i++) {
            service.submit(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try {
                                barrier.await();
                                System.out.println(String.format("Started. Thread id: %d ", Thread.currentThread().getId()));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (BrokenBarrierException e) {
                                e.printStackTrace();
                            }

                            long avg = 0;
                            for (int i = 0; i < TEST_COUNT; i++) {
                                long start = System.nanoTime();

                                QueryBuilder builder = cache.createQuery();

                                QueryBuilder.Query query = builder
                                        .start("city", "omsk")
                                        .and("city", "tomsk")
                                        .and("city", "novosibirsk")
                                        .and("city", "moscow")
                                        .and("gender", "male")
                                        .and("lang", "ru")
                                        .and("lang", "de")
                                        .and("lang", "en")
                                        .and("lang", "au")
                                        .and("lang", "it")
                                        .or("lang", Integer.toString(i))
                                        .get();

                                List<Object> result = cache.find(query);

                                long time = System.nanoTime() - start;
                                avg+= time;

                                if (result.size() != OBJECTS_COUNT) {
                                    System.out.println("Error! size: " + result.size());
                                    break;
                                }
                            }

                            System.out.println(String.format("Finished. Thread id: %d ", Thread.currentThread().getId()));
                            System.out.println(String.format("Average time %d ns, %f ms", avg / TEST_COUNT, (avg / TEST_COUNT) / 1000000.0));
                            latch.countDown();
                        }
                    }
            );
        }

        latch.await();

        service.shutdown();

        System.out.println("Objects in cache: " + OBJECTS_COUNT);
        System.out.println("Tests count: " + TEST_COUNT);
        System.out.println("=========================================================");
    }

    /**
     * Multi thread test with listener. Emulates dynamic states of objects.
     * all objects includes to output result.
     *
     * @throws InterruptedException
     */
    public void allInclusiveMultiThreadWithListener() throws InterruptedException
    {
        System.out.println("=========================================================");
        System.out.println("Dozensbit - all inclusive. Multi-thread with Listener");

        final Cache<Object> cache = new IndexedCache<Object>();

        MultiValueMap tags = new MultiValueMap();

        tags.put("city", "omsk");
        tags.put("city", "tomsk");
        tags.put("city", "novosibirsk");
        tags.put("city", "moscow");
        tags.put("gender", "male");
        tags.put("lang", "ru");
        tags.put("lang", "de");
        tags.put("lang", "en");
        tags.put("lang", "au");
        tags.put("lang", "it");

        final Map<Integer, Integer> concurrentMap = new ConcurrentHashMap<>();

        for (int i = 0; i < OBJECTS_COUNT; i++) {
            cache.put(i, tags);
            concurrentMap.put(i, i);
        }

        cache.rebuild();


        ExecutorService service = Executors.newFixedThreadPool(THREADS_COUNT);
        final CyclicBarrier barrier = new CyclicBarrier(THREADS_COUNT);
        final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);

        for (int i = 0; i < THREADS_COUNT; i++) {
            service.submit(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try {
                                barrier.await();
                                System.out.println(String.format("Started. Thread id: %d ", Thread.currentThread().getId()));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (BrokenBarrierException e) {
                                e.printStackTrace();
                            }

                            long avg = 0;
                            for (int i = 0; i < TEST_COUNT; i++) {
                                long start = System.nanoTime();

                                QueryBuilder builder = cache.createQuery();

                                QueryBuilder.Query query = builder
                                        .start("city", "omsk")
                                        .and("city", "tomsk")
                                        .and("city", "novosibirsk")
                                        .and("city", "moscow")
                                        .and("gender", "male")
                                        .and("lang", "ru")
                                        .and("lang", "de")
                                        .and("lang", "en")
                                        .and("lang", "au")
                                        .and("lang", "it")
                                        .or("lang", Integer.toString(i))
                                        .get();

                                List<Object> result = cache.find(query, new SearchListener<Object>() {
                                    @Override
                                    public boolean objectFoundEvent(Object object) {
                                        return concurrentMap.containsKey(object);
                                    }
                                });

                                long time = System.nanoTime() - start;
                                avg+= time;

                                if (result.size() != OBJECTS_COUNT) {
                                    System.out.println("Error! size: " + result.size());
                                    break;
                                }
                            }

                            System.out.println(String.format("Finished. Thread id: %d ", Thread.currentThread().getId()));
                            System.out.println(String.format("Average time %d ns, %f ms", avg / TEST_COUNT, (avg / TEST_COUNT) / 1000000.0));
                            latch.countDown();
                        }
                    }
            );
        }

        latch.await();

        service.shutdown();

        System.out.println("Objects in cache: " + OBJECTS_COUNT);
        System.out.println("Tests count: " + TEST_COUNT);
        System.out.println("=========================================================");
    }
}
