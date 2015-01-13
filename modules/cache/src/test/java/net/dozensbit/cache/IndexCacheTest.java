package net.dozensbit.cache;

import net.dozensbit.cache.query.QueryBuilder;
import org.junit.Test;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class IndexCacheTest
{

    @Test
    public void simpleQueryTest()
    {
        Cache<String> cache = new IndexCache<String>();

        cache.put("1", TestUtils.toMap("city:omsk,gender:male,lang:ru"));
        cache.put("2", TestUtils.toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", TestUtils.toMap("city:ny,gender:male,lang:en"));
        cache.put("4", TestUtils.toMap("city:berlin,gender:female,lang:de"));
        cache.put("5", TestUtils.toMap("city:omsk,gender:female,lang:ru"));
        cache.put("6", TestUtils.toMap("city:omsk,gender:female,lang:en"));
        cache.put("7", TestUtils.toMap("city:berlin,gender:female,lang:en"));

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
        assertTrue(result.get(0).equals("7"));
        assertTrue(result.get(1).equals("5"));
        assertTrue(result.get(2).equals("4"));
    }



    @Test
    public void multiValueQueryTest()
    {
        Cache<String> cache = new IndexCache<String>();

        cache.put("1", TestUtils.toMap("city:omsk,city:novosibirsk,city:tomsk,city:novokuznetsk,gender:male,lang:ru"));
        cache.put("2", TestUtils.toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", TestUtils.toMap("city:ny,gender:male,lang:en"));
        cache.put("4", TestUtils.toMap("city:berlin,city:tomsk,gender:female,lang:de"));
        cache.put("5", TestUtils.toMap("city:omsk,city:novokuznetsk,gender:female,lang:ru"));
        cache.put("6", TestUtils.toMap("city:omsk,city:novosibirsk,city:tomsk,gender:female,lang:en"));
        cache.put("7", TestUtils.toMap("city:berlin,gender:female,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start("city", "omsk")
                .and("city", "novokuznetsk")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 2);
        assertTrue(result.get(0).equals("1"));
        assertTrue(result.get(1).equals("5"));

        builder = cache.createQuery();

        query = builder
                .start("city", "omsk")
                .or("city", "tomsk")
                .get();

        result = cache.find(query);

        assertTrue(result.size() == 4);
        assertTrue(result.get(0).equals("1"));
        assertTrue(result.get(1).equals("6"));
        assertTrue(result.get(2).equals("5"));
        assertTrue(result.get(3).equals("4"));

    }

    @Test
    public void complexQueryTest()
    {
        Cache<String> cache = new IndexCache<String>();

        cache.put("1", TestUtils.toMap("city:omsk,city:novosibirsk,city:tomsk,city:novokuznetsk,gender:male,lang:ru"));
        cache.put("2", TestUtils.toMap("city:moscow,gender:male,lang:ru"));
        cache.put("3", TestUtils.toMap("city:ny,gender:male,lang:en"));
        cache.put("4", TestUtils.toMap("city:berlin,city:tomsk,gender:male,lang:de"));
        cache.put("5", TestUtils.toMap("city:omsk,city:novokuznetsk,gender:female,lang:ru"));
        cache.put("6", TestUtils.toMap("city:omsk,city:novosibirsk,city:tomsk,gender:female,lang:en"));
        cache.put("7", TestUtils.toMap("city:berlin,gender:female,lang:en"));

        cache.rebuild();

        QueryBuilder builder = cache.createQuery();

        QueryBuilder.Query query = builder
                .start(
                        cache.createQuery()
                                .start("city", "berlin")
                                .or("city", "moscow")
                                .get()
                ).and(
                        cache.createQuery()
                                .start("lang", "ru")
                                .or("lang", "en")
                                .get()
                )
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 2);
        assertTrue(result.get(0).equals("2"));
        assertTrue(result.get(1).equals("7"));

    }

}
