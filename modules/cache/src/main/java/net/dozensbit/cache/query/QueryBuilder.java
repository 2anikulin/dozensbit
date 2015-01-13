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
        this.query.putAnd(query);
        return this;
    }

    public QueryBuilder startNot(final String key, final String value)
    {
        query.putNotAnd(key, value);
        return this;
    }

    public QueryBuilder startNot(final Query query)
    {
        this.query.putNotAnd(query);
        return this;
    }

    public QueryBuilder and(final String key, final String value)
    {
        query.putAnd(key, value);
        return this;
    }

    public QueryBuilder and(final Query query)
    {
        this.query.putAnd(query);
        return this;
    }

    public QueryBuilder andNot(final String key, final String value)
    {
        query.putNotAnd(key, value);
        return this;
    }

    public QueryBuilder andNot(final Query query)
    {
        this.query.putNotAnd(query);
        return this;
    }

    public QueryBuilder or(final String key, final String value)
    {
        query.putOr(key, value);
        return this;
    }

    public QueryBuilder or(final Query query)
    {
        this.query.putOr(query);
        return this;
    }

    public QueryBuilder orNot(final String key, final String value)
    {
        query.putNotOr(key, value);
        return this;
    }

    public QueryBuilder orNot(final Query query)
    {
        this.query.putNotOr(query);
        return this;
    }

    public QueryBuilder softEqual(final String key, final String value)
    {
        query.putSoftEqual(key, value);
        return this;
    }

    public QueryBuilder softEqual(final Query query)
    {
        this.query.putSoftEqual(query);
        return this;
    }

    public Query get()
    {
        return query;
    }

    public static class Query
    {
        private List<Predicate> predicates = new ArrayList<Predicate>();

        private final IndexService indexService;

        private Query(final IndexService indexService)
        {
            this.indexService = indexService;
        }

        public List<Predicate> getPredicates()
        {
            return predicates;
        }

        public void putAnd(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                predicates.add(new And(index.getIndex()));
            } else {
                predicates.add(new And(indexService.getIndexNegative()));
            }
        }

        public void putAnd(final Query query)
        {
             predicates.add(new And(query));
        }

        public void putNotAnd(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                predicates.add(new AndNot(index.getIndex()));
            } else {
                predicates.add(new AndNot(indexService.getIndexNegative()));
            }
        }

        public void putNotAnd(final Query query)
        {
            predicates.add(new AndNot(query));
        }

        public void putOr(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                predicates.add(new Or(index.getIndex()));
            } else {
                predicates.add(new Or(indexService.getIndexNegative()));
            }
        }

        public void putOr(final Query query)
        {
            predicates.add(new Or(query));
        }

        public void putNotOr(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                predicates.add(new OrNot(index.getIndex()));
            } else {
                predicates.add(new OrNot(indexService.getIndexNegative()));
            }
        }

        public void putNotOr(final Query query)
        {
           predicates.add(new OrNot(query));
        }

        public void putSoftEqual(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                predicates.add(new SoftEqual(index.getIndex()));
            } else {
                predicates.add(new SoftEqual(indexService.getIndexPositive()));
            }
        }

        public void putSoftEqual(final Query query)
        {
            predicates.add(new SoftEqual(query));
        }
    }

}
