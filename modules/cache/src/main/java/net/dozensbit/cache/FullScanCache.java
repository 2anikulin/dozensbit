package net.dozensbit.cache;

import net.dozensbit.cache.core.Index;
import net.dozensbit.cache.core.IndexService;
import net.dozensbit.cache.core.Searcher;
import net.dozensbit.cache.query.And;
import net.dozensbit.cache.query.OrNot;
import net.dozensbit.cache.query.Predicate;
import net.dozensbit.cache.query.QueryBuilder;
import org.apache.commons.collections.map.MultiValueMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class FullScanCache<T> implements Cache<T>
{
    private static final long POSITIVE = -1;

    private final Searcher searcher = new Searcher();
    private final Map<T, MultiValueMap> rawObjects = new ConcurrentHashMap<T, MultiValueMap>();
    private volatile Container container;


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

    //A and B and C
    //!A and !B and !C -> 0 and 0 and 0 -> true, 1 and 0 and 0 -> false, ~[ A or B or C]
    //A or B or C
    //!A or !B or !C -> 0 or 0 or 0 -> true, 1 or 0 or 0 -> true, 1 or 1 or 1 -> false, ~[ A and B and C]
    //(A and B) or (C and D)
    @Override
    public List<T> find(final QueryBuilder.Query query)
    {
        if (searcher == null) {
            return Collections.<T>emptyList();
        }

        final Container localContainer = container;



        List<T> foundObjects = new ArrayList<T>();
        T[] objects = container.getIndexedObjects();

        long[] masks = IndexService.getMasks();
        int maskLen = localContainer.getIndexService().getIndexLength();
        if (maskLen > masks.length) {
            maskLen = masks.length;
        }

        int size = localContainer.getIndexService().getIndexSize();
        List<Predicate> predicates = query.getPredicates();
        long initValue = ~0; //(predicates.get(0) instanceof And) || (predicates.get(0) instanceof OrNot) ? ~0 : 0;


        for (int i = 0; i < size; i++) {
            long result = initValue;

            for (Predicate p : predicates) {
                result = p.reduce(i,result);
            }

            if (result == 0) {
                continue;
            }

            int offset = getOffset(i);
            for (int j = 0; j < maskLen; j++ ) {
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
        if (searcher == null) {
            return null;
        }

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
