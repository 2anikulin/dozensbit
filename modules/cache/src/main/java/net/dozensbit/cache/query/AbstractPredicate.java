package net.dozensbit.cache.query;

import java.util.List;

import static net.dozensbit.cache.core.IndexService.POSITIVE;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public abstract class AbstractPredicate implements Predicate
{
    private final long[] index;
    private final List<Predicate> predicates;

    public AbstractPredicate()
    {
        this.index = null;
        this.predicates = null;
    }

    public AbstractPredicate(final long[] index, final QueryBuilder.Query query)
    {
        this.index = index;
        this.predicates = query == null ? null : query.getPredicates();
    }

    protected long[] getIndex()
    {
        return index;
    }

    protected long reduceQuery(int pos)
    {
        long result = POSITIVE;

        for (Predicate p : predicates) {
            result = p.reduce(pos, result);
        }

        return result;
    }
}
