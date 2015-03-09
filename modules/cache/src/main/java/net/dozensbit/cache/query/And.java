package net.dozensbit.cache.query;

/**
 * AND - predicate implementation.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class And extends AbstractPredicate
{
    /**
     * Constructor.
     *
     * @param index Bit mask (may be result of previous calculation).
     */
    public And(final long[] index)
    {
        super(index, null);
    }

    /**
     * Constructor.
     *
     * @param query Initialized query builder.
     */
    public And(final QueryBuilder.Query query)
    {
        super(null, query);
    }

    /**
     * Performs AND operation.
     *
     * @param pos Index of object in a global set.
     * @param value Bit set.
     * @return Result of AND operation.
     */
    @Override
    public long reduce(final int pos, final long value)
    {
        long[] index = getIndex();
        if (index != null) {
            return index[pos] & value;
        } else {
            return reduceQuery(pos) & value;
        }
    }
}
