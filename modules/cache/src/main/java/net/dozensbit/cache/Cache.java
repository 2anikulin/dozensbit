package net.dozensbit.cache;

import net.dozensbit.cache.query.QueryBuilder;
import org.apache.commons.collections.map.MultiValueMap;

import java.util.List;

/**
 * Cache interface.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public interface Cache<T>
{
    /**
     * Add object and their search-tags to cache.
     * New object will be available only after commit() method called.
     *
     * @param object Object
     * @param tags Search-tags.
     */
    void put(final T object, final MultiValueMap tags);

    /**
     * Remove object and their search-tags from cache.
     * Changes will be available only after commit() method called.
     *
     * @param object Object to remove.
     */
    void remove(final T object);

    /**
     * Get all cached objects.
     * @return List of objects.
     */
    List<T> getAll();

    /**
     * Get size of cached object set.
     *
     * @return Size.
     */
    long size();

    /**
     * Removes all objects from cache.
     * Changes will be available only after commit() method called.
     */
    void deleteAll();

    /**
     * Rebuild all indexes so all changes be available for search operations.
     */
    void commit();

    /**
     * Find objects.
     *
     * @param query Input query. Use QueryBuilder.
     * @return List of found objects.
     */
    List<T> find(final QueryBuilder.Query query);

    /**
     * Find objects.
     *
     * @param query Input query. Use QueryBuilder.
     * @param listener Listener. Call every time when object found.
     *                 If Listener return False then found object doesn't add to output result.
     * @return List of found objects.
     */
    List<T> find(final QueryBuilder.Query query, SearchListener<T> listener);

    /**
     * Creates new QueryBuilder instance.
     *
     * @return QueryBuilder.
     */
    QueryBuilder createQuery();
}
