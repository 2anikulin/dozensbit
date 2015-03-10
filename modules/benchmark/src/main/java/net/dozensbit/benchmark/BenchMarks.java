package net.dozensbit.benchmark;

/**
 * Set of benchmarks.
 *
 */
public class BenchMarks
{
    private static final int OBJECTS_COUNT = 100000;
    private static final int TEST_COUNT = 10000;
    private static final int THREADS_COUNT = 4;

    /**
     * Main!
     *
     * @param args input.
     * @throws InterruptedException .
     */
    public static void main(final String[] args ) throws InterruptedException
    {
        DozensBitTest dozensBitTest = new DozensBitTest(OBJECTS_COUNT, TEST_COUNT, THREADS_COUNT);

        dozensBitTest.allInclusiveSingleThread();

        dozensBitTest.allInclusiveMultiThread();

        dozensBitTest.allInclusiveMultiThreadWithListener();

        H2Test h2Test = new H2Test(OBJECTS_COUNT, TEST_COUNT);

        h2Test.allInclusiveSingleThread();
    }

}
