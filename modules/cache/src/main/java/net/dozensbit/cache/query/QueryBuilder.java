package net.dozensbit.cache.query;

import net.dozensbit.cache.core.Index;
import net.dozensbit.cache.core.IndexService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class QueryBuilder
{
    private final Query query;

    public QueryBuilder(final IndexService indexService) {
        query  = new Query(indexService);
    }

    public QueryBuilder start(final String key, final String value)
    {
        query.putAnd(key, value);
        return this;
    }

    public QueryBuilder start(final Query query)
    {
        throw new NotImplementedException();
    }

    public QueryBuilder startNot(final String key, final String value)
    {
        query.putNotAnd(key, value);
        return this;
    }

    public QueryBuilder and(final String key, final String value)
    {
        query.putAnd(key, value);
        return this;
    }

    public QueryBuilder and(final Query query)
    {
        throw new NotImplementedException();
    }

    public QueryBuilder andNot(final String key, final String value)
    {
        query.putNotAnd(key, value);
        return this;
    }

    public QueryBuilder or(final String key, final String value)
    {
        query.putOr(key, value);
        return this;
    }

    public QueryBuilder or(final Query query)
    {
        throw new NotImplementedException();
    }

    public QueryBuilder orNot(final String key, final String value)
    {
        query.putNotOr(key, value);
        return this;
    }

    public Query get()
    {
        return query;
    }

    public static class Query
    {
        private List<Index> andMap = new ArrayList<Index>();
        private List<Index> andNotMap = new ArrayList<Index>();

        private List<Index> orMap = new ArrayList<Index>();
        private List<Index> orNotMap = new ArrayList<Index>();

        private final IndexService indexService;

        private Query(final IndexService indexService)
        {
            this.indexService = indexService;
        }

        public List<Index> getAndMap()
        {
            return andMap;
        }

        public List<Index> getAndNotMap()
        {
            return andNotMap;
        }

        public List<Index> getOrMap()
        {
            return orMap;
        }

        public List<Index> getOrNotMap()
        {
            return orNotMap;
        }

        public void putAnd(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                andMap.add(index);
            }
        }

        public void putNotAnd(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                andNotMap.add(index);
            }

        }

        public void putOr(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                orMap.add(index);
            }
        }

        public void putNotOr(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                orNotMap.add(index);
            }
        }
    }

}
