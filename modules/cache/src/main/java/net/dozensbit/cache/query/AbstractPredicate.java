package net.dozensbit.cache.query;

import java.util.List;

import static net.dozensbit.cache.core.IndexService.POSITIVE;

/**
 * Predicate abstract implementation.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public abstract class AbstractPredicate implements Predicate
{
    private final long[] index;
    private final List<Predicate> predicates;

    /**
     * Default Constructor.
     */
    public AbstractPredicate()
    {
        this.index = null;
        this.predicates = null;
    }

    /**
     * Constructor.
     *
     * @param index Bit mask (may be result of previous calculation).
     * @param query Initialized query builder.
     */
    public AbstractPredicate(final long[] index, final QueryBuilder.Query query)
    {
        this.index = index;
        this.predicates = query == null ? null : query.getPredicates();
    }

    /**
     * Method to override for predicate implementation.
     *
     * @return current bit mask.
     */
    protected long[] getIndex()
    {
        return index;
    }

    /**
     * Method to override for predicate implementation.
     *
     * @param pos index of object in global object set.
     * @return calculated set of bids.
     */
    protected long reduceQuery(final int pos)
    {
        long result = POSITIVE;

        for (Predicate p : predicates) {
            result = p.reduce(pos, result);
        }

        return result;
    }
}
