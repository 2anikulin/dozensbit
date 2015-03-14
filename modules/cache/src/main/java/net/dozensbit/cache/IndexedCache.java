package net.dozensbit.cache;

import net.dozensbit.cache.core.IndexService;
import net.dozensbit.cache.query.Predicate;
import net.dozensbit.cache.query.QueryBuilder;
import org.apache.commons.collections.map.MultiValueMap;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Indexed Cache implementation.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class IndexedCache<T> implements Cache<T>
{
    private final Map<T, MultiValueMap> rawObjects = new ConcurrentHashMap<T, MultiValueMap>();
    private final long[] masks;
    private volatile Container container;


    /**
     * Constructor.
     */
    public IndexedCache()
    {
        masks = IndexService.getMasks();
    }

    /**
     * Add object and their search-tags to cache.
     * New object will be available only after commit() method called.
     *
     * @param object Object
     * @param tags Search-tags.
     */
    @Override
    public void put(final T object, final MultiValueMap tags)
    {
        rawObjects.put(object, tags);
    }

    /**
     * Remove object and their search-tags from cache.
     * Changes will be available only after commit() method called.
     *
     * @param object Object to remove.
     */
    @Override
    public void remove(final T object)
    {
        rawObjects.remove(object);
    }

    /**
     * Get all cached objects.
     * @return List of objects.
     */
    @Override
    public List<T> getAll()
    {
        return container != null ? Arrays.asList(container.getIndexedObjects()) : Collections.<T>emptyList();
    }

    /**
     * Get size of cached object set.
     *
     * @return Size.
     */
    @Override
    public long size()
    {
        return container != null ? container.getIndexedObjects().length : 0;
    }

    /**
     * Removes all objects from cache.
     * Changes will be available only after commit() method called.
     */
    @Override
    public void deleteAll()
    {
        rawObjects.clear();
    }

    /**
     * Rebuild all indexes so all changes be available for search operations.
     */
    @Override
    public synchronized void commit()
    {
        Map<T, MultiValueMap> objects = new HashMap<T, MultiValueMap>(rawObjects);

        List<T> indexed = new ArrayList<T>();

        IndexService newIndexService = new IndexService(objects.size());

        int position = 0;

        for (Map.Entry<T, MultiValueMap> entry : objects.entrySet()) {
            indexed.add(entry.getKey());
            newIndexService.addToIndex(position++, entry.getValue());
        }

        newIndexService.build();

        T[] newIndexedObjects = (T[]) indexed.toArray();

        Container newContainer = new Container(newIndexedObjects, newIndexService);

        this.container = newContainer;
    }

    /**
     * Find objects.
     *
     * @param query Input query. Use QueryBuilder.
     * @return List of found objects.
     */
    @Override
    public List<T> find(final QueryBuilder.Query query)
    {
        final Container localContainer = container;

        List<T> foundObjects = new ArrayList<T>();
        T[] objects = localContainer.getIndexedObjects();

        int size = localContainer.getIndexService().getIndexSize();
        List<Predicate> predicates = query.getPredicates();

        int last = size - 1;
        int masksLen = masks.length;

        for (int i = 0; i < size; i++) {
            long result = IndexService.POSITIVE;

            for (Predicate p : predicates) {
                result = p.reduce(i,result);
            }

            if (result == 0) {
                continue;
            }

            if (i == last) {
                masksLen = localContainer.getIndexService().getIndexLength() % IndexService.BIT_COUNT;
                if (masksLen == 0) {
                    masksLen = IndexService.BIT_COUNT;

                }
            }

            int offset = getOffset(i);
            for (int j = 0; j < masksLen; j++ ) {
                if ((masks[j] & result) != 0) {
                    foundObjects.add(
                            objects[offset + j]
                    );
                }
            }
        }

        return foundObjects;
    }

    /**
     * Find objects.
     *
     * @param query Input query. Use QueryBuilder.
     * @param listener Listener. Call every time when object found.
     *                 If Listener return False then found object doesn't add to output result.
     * @return List of found objects.
     */
    @Override
    public List<T> find(final QueryBuilder.Query query, final SearchListener<T> listener)
    {
        final Container localContainer = container;

        List<T> foundObjects = new ArrayList<T>();
        T[] objects = localContainer.getIndexedObjects();

        int size = localContainer.getIndexService().getIndexSize();
        List<Predicate> predicates = query.getPredicates();

        int last = size - 1;
        int masksLen = masks.length;

        for (int i = 0; i < size; i++) {
            long result = IndexService.POSITIVE;

            for (Predicate p : predicates) {
                result = p.reduce(i,result);
            }

            if (result == 0) {
                continue;
            }

            if (i == last) {
                masksLen = localContainer.getIndexService().getIndexLength() % IndexService.BIT_COUNT;
                if (masksLen == 0) {
                    masksLen = IndexService.BIT_COUNT;
                }
            }

            int offset = getOffset(i);
            for (int j = 0; j < masksLen; j++ ) {
                if ((masks[j] & result) != 0) {
                    T object = objects[offset + j];
                    if (listener.objectFoundEvent(object)) {
                        foundObjects.add(
                                objects[offset + j]
                        );
                    }
                }
            }
        }

        return foundObjects;
    }

    /**
     * Creates new QueryBuilder instance.
     *
     * @return QueryBuilder.
     */
    @Override
    public QueryBuilder createQuery()
    {
        return new QueryBuilder(container.getIndexService());
    }

    private int getOffset(final int longPosition)
    {
        return longPosition / IndexService.BIT_COUNT;
    }

    private class Container
    {
        private final T[] indexedObjects;
        private final IndexService indexService;

        public Container(final T[] indexedObjects, final IndexService indexService)
        {
            this.indexService = indexService;
            this.indexedObjects = indexedObjects;
        }

        public T[] getIndexedObjects()
        {
            return indexedObjects;
        }

        public IndexService getIndexService()
        {
            return indexService;
        }
    }
}
