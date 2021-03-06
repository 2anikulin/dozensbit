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
public class AndTest {

    @Test
    public void andTest()
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
                .and("gender", "female")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 2);
        assertTrue(result.get(0).equals("5"));
        assertTrue(result.get(1).equals("6"));
    }

    @Test
    public void andIndexNotExistsTest()
    {
        Cache<String> cache = new IndexedCache<String>();

        cache.put("1", Utils.toMap("{city:omsk,gender:male,lang:ru}"));
        cache.put("2", Utils.toMap("{city:moscow,gender:male,lang:ru}"));
        cache.put("3", Utils.toMap("{city:ny,gender:male,lang:en}"));
        cache.put("4", Utils.toMap("{city:berlin,gender:female,lang:de}"));
        cache.put("5", Utils.toMap("{city:omsk,gender:female,lang:ru}"));
        cache.put("6", Utils.toMap("{city:omsk,gender:female,lang:en}"));
        cache.put("7", Utils.toMap("{city:berlin,gender:female,lang:en}"));

        cache.commit();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "omsk")
                .and("gender", "GENDER_NOT_EXISTS")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 0);

        query = builder
                .start("city", "omsk")
                .and("tag_not_exists", "GENDER_NOT_EXISTS")
                .get();

        result = cache.find(query);

        assertTrue(result.size() == 0);

    }

    @Test
    public void andNotTest()
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
                .startNot("city", "omsk")
                .andNot("gender", "female")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 2);
        assertTrue(result.get(0).equals("2"));
        assertTrue(result.get(1).equals("3"));
    }

    @Test
    public void andNotIndexNotExistsTest()
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
                .startNot("city", "omsk")
                .andNot("gender", "NOT_EXISTS")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 3);
        assertTrue(result.get(0).equals("2"));
        assertTrue(result.get(1).equals("3"));
        assertTrue(result.get(2).equals("4"));

        query = builder
                .startNot("city", "omsk")
                .andNot("tag_not_exists", "NOT_EXISTS")
                .get();

        result = cache.find(query);

        assertTrue(result.size() == 0);
    }

}
