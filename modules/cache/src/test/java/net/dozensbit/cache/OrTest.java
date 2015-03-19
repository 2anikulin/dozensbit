package net.dozensbit.cache;

import net.dozensbit.cache.query.QueryBuilder;
import net.dozensbit.cache.utils.Utils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;


/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class OrTest {

    @Test
    public void orTest()
    {
        Cache<String> cache = new IndexedCache<String>();

        cache.put("1", Utils.toMap("{city:omsk,gender:male,lang:ru}"));
        cache.put("2", Utils.toMap("{city:moscow,gender:male,lang:ru}"));
        cache.put("3", Utils.toMap("{city:ny,gender:male,lang:en}"));
        cache.put("4", Utils.toMap("{city:berlin,gender:female,lang:de}"));
        cache.put("5", Utils.toMap("{city:omsk,gender:female,lang:ru}"));
        cache.put("6", Utils.toMap("{city:omsk,gender:female,lang:en}"));

        cache.commit();

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
        Cache<String> cache = new IndexedCache<String>();

        cache.put("1", Utils.toMap("{city:omsk,gender:male,lang:ru}"));
        cache.put("2", Utils.toMap("{city:moscow,gender:male,lang:ru}"));
        cache.put("3", Utils.toMap("{city:ny,gender:male,lang:en}"));
        cache.put("4", Utils.toMap("{city:berlin,gender:female,lang:de}"));
        cache.put("5", Utils.toMap("{city:omsk,gender:female,lang:ru}"));
        cache.put("6", Utils.toMap("{city:omsk,gender:female,lang:en}"));

        cache.commit();

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

        query = builder
                .start("city", "omsk")
                .or("tag_not_exists", "NOT_EXIST")
                .get();

        result = cache.find(query);

        assertTrue(result.size() == 3);
        assertTrue(result.get(0).equals("1"));
        assertTrue(result.get(1).equals("5"));
        assertTrue(result.get(2).equals("6"));
    }


    @Test
    public void orNotTest()
    {
        Cache<String> cache = new IndexedCache<String>();

        cache.put("1", Utils.toMap("{city:omsk,gender:male,lang:ru}"));
        cache.put("2", Utils.toMap("{city:moscow,gender:male,lang:ru}"));
        cache.put("3", Utils.toMap("{city:ny,gender:male,lang:en}"));
        cache.put("4", Utils.toMap("{city:berlin,gender:female,lang:de}"));
        cache.put("5", Utils.toMap("{city:omsk,gender:female,lang:ru}"));
        cache.put("6", Utils.toMap("{city:omsk,gender:female,lang:en}"));

        cache.commit();

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
        Cache<String> cache = new IndexedCache<String>();

        cache.put("1", Utils.toMap("{city:omsk,gender:male,lang:ru}"));
        cache.put("2", Utils.toMap("{city:moscow,gender:male,lang:ru}"));
        cache.put("3", Utils.toMap("{city:ny,gender:male,lang:en}"));
        cache.put("4", Utils.toMap("{city:berlin,gender:female,lang:de}"));
        cache.put("5", Utils.toMap("{city:omsk,gender:female,lang:ru}"));
        cache.put("6", Utils.toMap("{city:omsk,gender:female,lang:en}"));

        cache.commit();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "moscow")
                .orNot("gender", "NOT_EXISTS")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 6);

        query = builder
                .start("city", "moscow")
                .orNot("tag_not_exists", "NOT_EXISTS")
                .get();

        result = cache.find(query);

        assertTrue(result.size() == 1);

    }

}
