package net.dozensbit.cache.query;

/**
 * AND NOT Predicate implementation.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class AndNot extends AbstractPredicate
{
    /**
     * Constructor.
     *
     * @param index Bit mask (may be result of previous calculation).
     */
    public AndNot(final long[] index)
    {
        super(index, null);
    }

    /**
     * Constructor.
     *
     * @param query Initialized query builder.
     */
    public AndNot(final QueryBuilder.Query query)
    {
        super(null, query);
    }

    /**
     * Performs AND NOT operation.
     *
     * @param pos Index of object in a global set.
     * @param value Bit set.
     * @return Result of AND NOT operation.
     */
    @Override
    public long reduce(final int pos, final long value)
    {
        long[] index = getIndex();
        if (index != null) {
            return ~index[pos] & value;
        } else {
            return ~reduceQuery(pos) & value;
        }
    }
}
