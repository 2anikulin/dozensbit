package net.dozensbit.cache;

import net.dozensbit.cache.query.QueryBuilder;
import org.apache.commons.collections.map.MultiValueMap;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class BenchMarkTest
{
    @Test
    public void allInclusiveTest()
    {
        Cache<Object> cache = new FullScanCache<Object>();

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

        int OBJECTS_COUNT = 100000;
        int TEST_COUNT = 10000;

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
                    .get();

            List<Object> result = cache.find(query);

            long time = System.nanoTime() - start;

            System.out.println(String.format("%d ns, %f ms", time, time / 1000000.0));
            avg+= time;

            if (result.size() != OBJECTS_COUNT) {
                System.out.println("Error!");
                break;
            }
        }

        System.out.println(String.format("average %d ns, %f ms", avg / TEST_COUNT, (avg / TEST_COUNT) / 1000000.0));
    }

    @Test
    public void multiThreadTest() throws InterruptedException
    {
        final Cache<Object> cache = new FullScanCache<Object>();

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

        final int OBJECTS_COUNT = 100000;
        final int TEST_COUNT = 10000;
        final int THREADS_COUNT = 4;

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
                                        .get();

                                List<Object> result = cache.find(query);

                                long time = System.nanoTime() - start;
                                avg+= time;

                                if (result.size() != OBJECTS_COUNT) {
                                    System.out.println("Error!");
                                    break;
                                }
                            }

                            System.out.println(String.format("Finished. Thread id: %d ", Thread.currentThread().getId()));
                            System.out.println(String.format("average %d ns, %f ms", avg / TEST_COUNT, (avg / TEST_COUNT) / 1000000.0));
                            latch.countDown();
                        }
                    }
            );
        }

        latch.await();
    }
}
