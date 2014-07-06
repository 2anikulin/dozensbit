package net.dozensbit.cache;

import net.dozensbit.cache.query.QueryBuilder;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertTrue;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class FullScanCacheTest
{
    @Test
    public void andTest()
    {
        Cache<String> cache = new FullScanCache<String>();

        cache.put("1", toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", toMap("city:ny,gender:male,lang:en"));
        cache.put("4", toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", toMap("city:omsk,gender:female,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "omsk")
                .and("gender", "female")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 2);
        assertTrue(result.get(0).compareTo("6") == 0);
        assertTrue(result.get(1).compareTo("5") == 0);
    }

    @Test
    public void andNotTest()
    {
        Cache<String> cache = new FullScanCache<String>();

        cache.put("1", toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", toMap("city:ny,gender:male,lang:en"));
        cache.put("4", toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", toMap("city:omsk,gender:female,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .startNot("city", "omsk")
                .andNot("gender", "female")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 2);
        assertTrue(result.get(0).compareTo("3") == 0);
        assertTrue(result.get(1).compareTo("2") == 0);
    }

    @Test
    public void orTest()
    {
        Cache<String> cache = new FullScanCache<String>();

        cache.put("1", toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", toMap("city:ny,gender:male,lang:en"));
        cache.put("4", toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", toMap("city:omsk,gender:female,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "omsk")
                .or("gender", "female")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 4);
        assertTrue(result.get(0).compareTo("1") == 0);
        assertTrue(result.get(1).compareTo("6") == 0);
        assertTrue(result.get(2).compareTo("5") == 0);
        assertTrue(result.get(3).compareTo("4") == 0);
    }

    @Test
    public void orNotTest()
    {
        Cache<String> cache = new FullScanCache<String>();

        cache.put("1", toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", toMap("city:ny,gender:male,lang:en"));
        cache.put("4", toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", toMap("city:omsk,gender:female,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "moscow")
                .orNot("gender", "male")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 4);
        assertTrue(result.get(0).compareTo("2") == 0);
        assertTrue(result.get(1).compareTo("6") == 0);
        assertTrue(result.get(2).compareTo("5") == 0);
        assertTrue(result.get(3).compareTo("4") == 0);
    }

    @Test
    public void ComplexQueryTest()
    {
        Cache<String> cache = new FullScanCache<String>();

        cache.put("1", toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", toMap("city:ny,gender:male,lang:en"));
        cache.put("4", toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", toMap("city:omsk,gender:female,lang:en"));
        cache.put("7", toMap("city:berlin,gender:female,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "omsk")
                .and("gender", "female")
                .andNot("lang", "en")
                .or("city", "berlin")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 3);
        assertTrue(result.get(0).compareTo("7") == 0);
        assertTrue(result.get(1).compareTo("5") == 0);
        assertTrue(result.get(2).compareTo("4") == 0);
    }


    private Map<String, String> toMap(final  String tags)
    {
        Map<String, String> ret = new HashMap<String, String>();
        String[] pairs = tags.split(",");

        for (String pair : pairs) {
            String[] kv = pair.split(":");
            if (kv.length != 2) {
                throw  new IllegalArgumentException("Wrong input format:" + pair);
            }

            ret.put(kv[0], kv[1]);
        }

        return ret;
    }
}
