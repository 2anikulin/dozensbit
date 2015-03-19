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
public class IndexedCacheTest
{

    @Test
    public void simpleQueryTest()
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
                .and("gender", "female")
                .andNot("lang", "en")
                .or("city", "berlin")
                .get();

        List<String> result = cache.find(query);

        assertTrue(result.size() == 3);
        assertTrue(result.get(0).equals("4"));
        assertTrue(result.get(1).equals("5"));
        assertTrue(result.get(2).equals("7"));
    }



    @Test
    public void multiValueQueryTest()
    {
        Cache<String> cache = new IndexedCache<String>();

        cache.put("1", Utils.toMap("{city:[omsk,novosibirsk,tomsk,novokuznetsk],gender:male,lang:ru}"));
        cache.put("2", Utils.toMap("{city:moscow,gender:male,lang:ru}"));
        cache.put("3", Utils.toMap("{city:ny,gender:male,lang:en}"));
        cache.put("4", Utils.toMap("{city:[berlin,tomsk],gender:female,lang:de}"));
        cache.put("5", Utils.toMap("{city:[omsk,novokuznetsk],gender:female,lang:ru}"));
        cache.put("6", Utils.toMap("{city:[omsk,novosibirsk,tomsk],gender:female,lang:en}"));
        cache.put("7", Utils.toMap("{city:berlin,gender:female,lang:en}"));

        cache.commit();

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
        assertTrue(result.get(1).equals("4"));
        assertTrue(result.get(2).equals("5"));
        assertTrue(result.get(3).equals("6"));

    }

    @Test
    public void complexQueryTest()
    {
        Cache<String> cache = new IndexedCache<String>();

        cache.put("1", Utils.toMap("{city:[omsk,novosibirsk,tomsk,novokuznetsk],gender:male,lang:ru}"));
        cache.put("2", Utils.toMap("{city:moscow,gender:male,lang:ru}"));
        cache.put("3", Utils.toMap("{city:ny,gender:male,lang:en}"));
        cache.put("4", Utils.toMap("{city:[berlin,tomsk],gender:male,lang:de}"));
        cache.put("5", Utils.toMap("{city:[omsk,novokuznetsk],gender:female,lang:ru}"));
        cache.put("6", Utils.toMap("{city:[omsk,novosibirsk,tomsk],gender:female,lang:en}"));
        cache.put("7", Utils.toMap("{city:berlin,gender:female,lang:en}"));

        cache.commit();

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

    @Test
    public void complexQueryTestWithListener()
    {
        Cache<String> cache = new IndexedCache<String>();

        cache.put("1", Utils.toMap("{city:[omsk,novosibirsk,tomsk,novokuznetsk],gender:male,lang:ru}"));
        cache.put("2", Utils.toMap("{city:moscow,gender:male,lang:ru}"));
        cache.put("3", Utils.toMap("{city:ny,gender:male,lang:en}"));
        cache.put("4", Utils.toMap("{city:[berlin,tomsk],gender:male,lang:de}"));
        cache.put("5", Utils.toMap("{city:[omsk,novokuznetsk],gender:female,lang:ru}"));
        cache.put("6", Utils.toMap("{city:[omsk,novosibirsk,tomsk],gender:female,lang:en}"));
        cache.put("7", Utils.toMap("{city:berlin,gender:female,lang:en}"));

        cache.commit();

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

        List<String> result = cache.find(query, new SearchListener<String>() {
            @Override
            public boolean objectFoundEvent(String object) {
                return true;
            }
        });

        assertTrue(result.size() == 2);
        assertTrue(result.get(0).equals("2"));
        assertTrue(result.get(1).equals("7"));

    }

}
