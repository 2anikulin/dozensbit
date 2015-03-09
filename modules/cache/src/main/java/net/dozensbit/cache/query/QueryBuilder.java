package net.dozensbit.cache.query;

import net.dozensbit.cache.core.Index;
import net.dozensbit.cache.core.IndexService;

import java.util.ArrayList;
import java.util.List;

/**
 * Query builder implementation.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class QueryBuilder
{
    private final Query query;

    /**
     * Constructor.
     *
     * @param indexService Instance of IndexService.
     */
    public QueryBuilder(final IndexService indexService) {
        query  = new Query(indexService);
    }

    /**
     * Begin of query.
     *
     * @param key Key (attribute).
     * @param value Value (attribute).
     * @return Builder.
     */
    public QueryBuilder start(final String key, final String value)
    {
        query.putAnd(key, value);
        return this;
    }

    /**
     * Begin of query.
     *
     * @param query Sub query.
     * @return Builder.
     */
    public QueryBuilder start(final Query query)
    {
        this.query.putAnd(query);
        return this;
    }

    /**
     * Begin of query with NOT.
     *
     * @param key Key (attribute).
     * @param value Value (attribute).
     * @return Builder.
     */
    public QueryBuilder startNot(final String key, final String value)
    {
        query.putNotAnd(key, value);
        return this;
    }

    /**
     * Begin of query with NOT.
     *
     * @param query Sub query.
     * @return Builder.
     */
    public QueryBuilder startNot(final Query query)
    {
        this.query.putNotAnd(query);
        return this;
    }

    /**
     * AND operation.
     *
     * @param key Key (attribute).
     * @param value Vakue (attribute).
     * @return Builder.
     */
    public QueryBuilder and(final String key, final String value)
    {
        query.putAnd(key, value);
        return this;
    }

    /**
     * AND operation.
     *
     * @param query Sub query.
     * @return Builder.
     */
    public QueryBuilder and(final Query query)
    {
        this.query.putAnd(query);
        return this;
    }

    /**
     * AND NOT operation.
     *
     * @param key Key (attribute).
     * @param value Value (attribute).
     * @return Builder.
     */
    public QueryBuilder andNot(final String key, final String value)
    {
        query.putNotAnd(key, value);
        return this;
    }

    /**
     * AND NOT operation.
     * @param query Sub query.
     * @return Builder.
     */
    public QueryBuilder andNot(final Query query)
    {
        this.query.putNotAnd(query);
        return this;
    }

    /**
     * OR operation.
     *
     * @param key Key (attribute).
     * @param value Value (attribute).
     * @return Builder.
     */
    public QueryBuilder or(final String key, final String value)
    {
        query.putOr(key, value);
        return this;
    }

    /**
     * OR operation.
     *
     * @param query Sub quiery.
     * @return Builder.
     */
    public QueryBuilder or(final Query query)
    {
        this.query.putOr(query);
        return this;
    }

    /**
     * OR NOT operation.
     *
     * @param key Key (attribute).
     * @param value Value (attribute).
     * @return Builder.
     */
    public QueryBuilder orNot(final String key, final String value)
    {
        query.putNotOr(key, value);
        return this;
    }

    /**
     * OR NOT operation.
     *
     * @param query Sub query.
     * @return Builder.
     */
    public QueryBuilder orNot(final Query query)
    {
        this.query.putNotOr(query);
        return this;
    }

    /**
     * Soft equal. This operation return TRUE if:
     * 1. attribute doesn't exist for object.
     * 2. attribute exists for object and equal to given value.
     *
     * @param key Key (attribute).
     * @param value Value (attribute).
     * @return Builder.
     */
    public QueryBuilder softEqual(final String key, final String value)
    {
        query.putSoftEqual(key, value);
        return this;
    }

    /**
     * Soft equal. This operation return TRUE if:
     * 1. attribute doesn't exist for object.
     * 2. attribute exists for object and equal to given value.
     *
     * @param query Sub query.
     * @return Builder.
     */
    public QueryBuilder softEqual(final Query query)
    {
        this.query.putSoftEqual(query);
        return this;
    }

    /**
     * Get constructed query.
     *
     * @return Query.
     */
    public Query get()
    {
        return query;
    }

    /**
     * Query implementation.
     */
    public static class Query
    {
        private List<Predicate> predicates = new ArrayList<Predicate>();

        private final IndexService indexService;

        /**
         * Constructor.
         *
         * @param indexService Instance of IndexService.
         */
        private Query(final IndexService indexService)
        {
            this.indexService = indexService;
        }

        /**
         * Get list of predicates.
         *
         * @return List of predicates.
         */
        public List<Predicate> getPredicates()
        {
            return predicates;
        }

        /**
         * Put AND predicate.
         *
         * @param key Key (attribute).
         * @param value Value (attribute).
         */
        public void putAnd(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                predicates.add(new And(index.getIndex()));
            } else {
                predicates.add(new And(indexService.getIndexNegative()));
            }
        }

        /**
         * Put AND predicate.
         *
         * @param query Sub query.
         */
        public void putAnd(final Query query)
        {
             predicates.add(new And(query));
        }

        /**
         * Put AND NOT predicate.
         *
         * @param key Key (attribute).
         * @param value Value (attribute).
         */
        public void putNotAnd(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                predicates.add(new AndNot(index.getIndex()));
            } else {
                Index rootIndex = indexService.getIndex(key, null);
                if (rootIndex != null) {
                    predicates.add(new AndNot(indexService.getIndexNegative()));
                } else {
                    predicates.add(new AndNot(indexService.getIndexPositive()));
                }
            }
        }

        /**
         * Put AND NOT predicate.
         *
         * @param query Sub query.
         */
        public void putNotAnd(final Query query)
        {
            predicates.add(new AndNot(query));
        }

        /**
         * Put OR predicate.
         *
         * @param key Key (attribute).
         * @param value Value (attribute).
         */
        public void putOr(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                predicates.add(new Or(index.getIndex()));
            } else {
                predicates.add(new Or(indexService.getIndexNegative()));
            }
        }

        /**
         * Put OR predicate.
         *
         * @param query Sub query.
         */
        public void putOr(final Query query)
        {
            predicates.add(new Or(query));
        }

        /**
         * Put OR NOT predicate.
         *
         * @param key Key (attribute).
         * @param value Value (attribute).
         */
        public void putNotOr(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            if (index != null) {
                predicates.add(new OrNot(index.getIndex()));
            } else {
                Index rootIndex = indexService.getIndex(key, null);
                if (rootIndex != null) {
                    predicates.add(new OrNot(indexService.getIndexNegative()));
                } else {
                    predicates.add(new OrNot(indexService.getIndexPositive()));
                }
            }
        }

        /**
         * Put OR NOT predicate.
         *
         * @param query Sub query.
         */
        public void putNotOr(final Query query)
        {
           predicates.add(new OrNot(query));
        }

        /**
         * Put soft equal predicate.
         *
         * @param key Key (attribute).
         * @param value Value (attribute).
         */
        public void putSoftEqual(final String key, final String value)
        {
            Index index = indexService.getIndex(key, value);
            Index rootIndex = indexService.getIndex(key, null);

            if (rootIndex == null) {
                predicates.add(new SoftEqual(indexService.getIndexNegative(), indexService.getIndexNegative()));
            } else {
                if (index != null) {
                    predicates.add(new SoftEqual(index.getIndex(), rootIndex.getIndex()));
                } else {
                    predicates.add(new SoftEqual(indexService.getIndexNegative(), rootIndex.getIndex()));
                }
            }
        }

        /**
         * Put Soft equal predicate.
         *
         * @param query Sub query.
         */
        public void putSoftEqual(final Query query)
        {
            predicates.add(new SoftEqual(query));
        }
    }

}
