package net.dozensbit.cache;

import com.sun.swing.internal.plaf.metal.resources.metal_sv;
import net.dozensbit.cache.core.Index;
import net.dozensbit.cache.core.IndexService;
import net.dozensbit.cache.core.Searcher;
import net.dozensbit.cache.query.QueryBuilder;

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
    private final Map<T, Object> rawObjects = new ConcurrentHashMap<T, Object>();
    private volatile Container container;


    @Override
    public void put(final T object, final Map<String, String> tags)
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
        Map<T, Object> objects = new HashMap<T, Object>(rawObjects);

        List<T> indexed = new ArrayList<T>();

        IndexService newIndexService = new IndexService(objects.size());

        int position = 0;

        for (Map.Entry<T, Object> entry : objects.entrySet()) {
            indexed.add(entry.getKey());
            newIndexService.addToIndex(position++, (Map<String, String>) entry.getValue());
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

        List<Index> orList = query.getOrMap();
        List<Index> orNotList = query.getOrNotMap();
        List<Index> andList = query.getAndMap();
        List<Index> andNotList = query.getAndNotMap();

        long[] orReduced = null;
        long[] orNotReduced = null;

        long[] andReduced = null;
        long[] andNotReduced = null;

        int size = localContainer.getIndexService().getIndexSize();
        long[] positive = localContainer.getIndexService().getIndexPositive();
        long[] negative = localContainer.getIndexService().getIndexNegative();

        if (orList.size() == 0) {
            orReduced = negative;
        } else {
            orReduced = new long[size];
            for (int i = 0; i < size; i++) {
                for (Index index : orList) {
                    orReduced[i] = orReduced[i] | index.getIndex()[i];
                }
            }
        }

        if (orNotList.size() == 0) {
            orNotReduced = negative;
        } else {
            orNotReduced = new long[size];
            for (int i = 0; i < size; i++) {
                for (Index index : orNotList) {
                    orNotReduced[i] = ~(orNotReduced[i] | index.getIndex()[i]);
                }
            }
        }

        if (andList.size() == 0) {
            andReduced = positive;
        } else {
            andReduced = new long[size];
            for (int i = 0; i < size; i++) {
                andReduced[i] = POSITIVE;
                for (Index index : andList) {
                    andReduced[i] = andReduced[i] & index.getIndex()[i];
                }
            }
        }

        if (andNotList.size() == 0) {
            andNotReduced = positive;
        } else {
            andNotReduced = new long[size];
            for (int i = 0; i < size; i++) {
                andNotReduced[i] = POSITIVE;
                for (Index index : andNotList) {
                    andNotReduced[i] = andNotReduced[i] & (~index.getIndex()[i]);
                }
            }
        }

        List<T> foundObjects = new ArrayList<T>();
        T[] objects = container.getIndexedObjects();

        long[] masks = IndexService.getMasks();
        int maskLen = localContainer.getIndexService().getIndexLength();
        if (maskLen > masks.length) {
            maskLen = masks.length;
        }

        for (int i = 0; i < size; i++) {
            long result = (orReduced[i] | orNotReduced[i]) | (andReduced[i] & andNotReduced[i]);
            if (result == 0){
                break;
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
