package net.dozensbit.cache;

import net.dozensbit.cache.query.QueryBuilder;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class SoftEqualTest {

    @Test
    public void softEqualTest()
    {
        Cache<String> cache = new IndexedCache<String>();

        cache.put("1", TestUtils.toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", TestUtils.toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", TestUtils.toMap("city:ny,gender:male,lang:en"));
        cache.put("4", TestUtils.toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", TestUtils.toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", TestUtils.toMap("city:omsk,gender:female,lang:en"));
        cache.put("7", TestUtils.toMap("city:omsk,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "omsk")
                .softEqual("gender", "female")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 3);
        assertTrue(result.get(0).equals("5"));
        assertTrue(result.get(1).equals("6"));
        assertTrue(result.get(2).equals("7"));
    }

    @Test
    public void softEqualIndexNotExistTest()
    {
        Cache<String> cache = new IndexedCache<String>();

        cache.put("1", TestUtils.toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", TestUtils.toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", TestUtils.toMap("city:ny,gender:male,lang:en"));
        cache.put("4", TestUtils.toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", TestUtils.toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", TestUtils.toMap("city:omsk,gender:female,lang:en"));
        cache.put("7", TestUtils.toMap("city:omsk,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "omsk")
                .softEqual("not_exists", "female")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 4);
        assertTrue(result.get(0).equals("1"));
        assertTrue(result.get(1).equals("5"));
        assertTrue(result.get(2).equals("6"));
        assertTrue(result.get(3).equals("7"));

        builder = cache.createQuery();

        query = builder
                .start("city", "omsk")
                .softEqual("gender", "not_exists")
                .get();

        result = cache.find(query);

        assertTrue(result.size() == 1);
        assertTrue(result.get(0).equals("7"));

    }

}
