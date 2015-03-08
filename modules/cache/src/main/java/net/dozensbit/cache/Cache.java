package net.dozensbit.cache;

import net.dozensbit.cache.query.QueryBuilder;
import org.apache.commons.collections.map.MultiValueMap;

import java.util.List;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public interface Cache<T>
{
    void put(final T object, final MultiValueMap tags);

    void remove(final T object);

    List<T> getAll();

    long size();

    void deleteAll();

    void rebuild();

    List<T> find(final QueryBuilder.Query query);

    List<T> find(final QueryBuilder.Query query, SearchListener<T> listener);

    QueryBuilder createQuery();
}
