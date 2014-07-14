package net.dozensbit.benchmark;

import net.dozensbit.cache.Cache;
import net.dozensbit.cache.FullScanCache;
import net.dozensbit.cache.query.QueryBuilder;
import org.apache.commons.collections.map.MultiValueMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;

/**
 * Set of benchmarks.
 *
 */
public class BenchMarks
{

    private static final int OBJECTS_COUNT = 100000;
    private static final int TEST_COUNT = 10000;
    private static final int THREADS_COUNT = 4;

    public static void main( String[] args ) throws InterruptedException
    {
        DozensBitTest dozensBitTest = new DozensBitTest(OBJECTS_COUNT, TEST_COUNT, THREADS_COUNT);

        dozensBitTest.allInclusiveSingleThread();

        dozensBitTest.allInclusiveMultiThread();

        H2Test h2Test = new H2Test(OBJECTS_COUNT, TEST_COUNT);

        h2Test.allInclusiveSingleThread();
    }

}
