package net.dozensbit.cache;

import net.dozensbit.cache.core.IndexService;
import net.dozensbit.cache.query.Predicate;
import net.dozensbit.cache.query.QueryBuilder;
import org.apache.commons.collections.map.MultiValueMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static net.dozensbit.cache.core.IndexService.POSITIVE;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class IndexCache<T> implements Cache<T>
{
    private final Map<T, MultiValueMap> rawObjects = new ConcurrentHashMap<T, MultiValueMap>();
    private final long[] masks;
    private volatile Container container;


    public IndexCache()
    {
        masks = IndexService.getMasks();
    }

    @Override
    public void put(final T object, final MultiValueMap tags)
    {
        rawObjects.put(object, tags);
    }

    @Override
    public void remove(final T object)
    {
        rawObjects.remove(object);
    }

    @Override
    public List<T> getAll()
    {
        return container != null ? Arrays.asList(container.getIndexedObjects()) : Collections.<T>emptyList();
    }

    @Override
    public long size()
    {
        return container != null ? container.getIndexedObjects().length : 0;
    }

    @Override
    public void deleteAll()
    {
        rawObjects.clear();
    }

    @Override
    public synchronized void rebuild()
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
            long result = POSITIVE;

            for (Predicate p : predicates) {
                result = p.reduce(i,result);
            }

            if (result == 0) {
                continue;
            }

            if (i == last) {
                masksLen = localContainer.getIndexService().getIndexLength() % IndexService.BIT_COUNT;
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

    @Override
    public T findOne(final QueryBuilder.Query query)
    {
        return null;
    }

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
