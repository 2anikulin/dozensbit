package net.dozensbit.cache;

import net.dozensbit.cache.query.QueryBuilder;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * Created by anikulin on 1/13/15.
 */
public class OrTest {

    @Test
    public void orTest()
    {
        Cache<String> cache = new IndexCache<String>();

        cache.put("1", TestUtils.toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", TestUtils.toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", TestUtils.toMap("city:ny,gender:male,lang:en"));
        cache.put("4", TestUtils.toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", TestUtils.toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", TestUtils.toMap("city:omsk,gender:female,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "omsk")
                .or("gender", "female")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 4);
        assertTrue(result.get(0).equals("1"));
        assertTrue(result.get(1).equals("4"));
        assertTrue(result.get(2).equals("5"));
        assertTrue(result.get(3).equals("6"));
    }

    @Test
    public void orIndexNotExistsTest()
    {
        Cache<String> cache = new IndexCache<String>();

        cache.put("1", TestUtils.toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", TestUtils.toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", TestUtils.toMap("city:ny,gender:male,lang:en"));
        cache.put("4", TestUtils.toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", TestUtils.toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", TestUtils.toMap("city:omsk,gender:female,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "omsk")
                .or("gender", "NOT_EXIST")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 3);
        assertTrue(result.get(0).equals("1"));
        assertTrue(result.get(1).equals("5"));
        assertTrue(result.get(2).equals("6"));
    }


    @Test
    public void orNotTest()
    {
        Cache<String> cache = new IndexCache<String>();

        cache.put("1", TestUtils.toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", TestUtils.toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", TestUtils.toMap("city:ny,gender:male,lang:en"));
        cache.put("4", TestUtils.toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", TestUtils.toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", TestUtils.toMap("city:omsk,gender:female,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "moscow")
                .orNot("gender", "male")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 4);
        assertTrue(result.get(0).equals("2"));
        assertTrue(result.get(1).equals("4"));
        assertTrue(result.get(2).equals("5"));
        assertTrue(result.get(3).equals("6"));
    }

    @Test
    public void orNotIndexNotExistsTest()
    {
        Cache<String> cache = new IndexCache<String>();

        cache.put("1", TestUtils.toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", TestUtils.toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", TestUtils.toMap("city:ny,gender:male,lang:en"));
        cache.put("4", TestUtils.toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", TestUtils.toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", TestUtils.toMap("city:omsk,gender:female,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "moscow")
                .orNot("gender", "NOT_EXISTS")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 6);

    }

}
