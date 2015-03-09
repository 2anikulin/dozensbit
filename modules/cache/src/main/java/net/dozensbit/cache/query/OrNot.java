package net.dozensbit.cache.query;

/**
 * OR NOT Predicate implementation.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class OrNot extends AbstractPredicate
{
    /**
     * Constructor.
     *
     * @param index Bit mask (may be result of previous calculation).
     */
    public OrNot(final long[] index)
    {
        super(index, null);
    }

    /**
     * Constructor.
     *
     * @param query Initialized query builder.
     */
    public OrNot(final QueryBuilder.Query query)
    {
        super(null, query);
    }

    /**
     * Performs OR NOT operation.
     *
     * @param pos Index of object in a global set.
     * @param value Bit set.
     * @return Result of OR NOT operation.
     */
    @Override
    public long reduce(final int pos, final long value)
    {
        long[] index = getIndex();
        if (index != null) {
            return ~index[pos] | value;
        } else {
            return ~reduceQuery(pos) | value;
        }
    }
}
