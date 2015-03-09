package net.dozensbit.cache.query;

/**
 * Soft equal predicate implementation.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class SoftEqual extends AbstractPredicate
{
    private final long[] rootIndex;

    /**
     * Constructor.
     *
     * @param index  Bit mask (may be result of previous calculation).
     * @param rootIndex  Bit mask. Special index for this attribute.
     */
    public SoftEqual(final long[] index, final long[] rootIndex)
    {
        super(index, null);
        this.rootIndex = rootIndex;
    }

    /**
     * Constructor.
     *
     * @param query Sub query.
     */
    public SoftEqual(final QueryBuilder.Query query)
    {
        super(null, query);
        this.rootIndex = null;
    }

    /**
     * Performs Soft equal operation.
     *
     * @param pos Index of object in a global set.
     * @param value Bit set.
     * @return Result of soft equal operation.
     */
    @Override
    public long reduce(final int pos, final long value)
    {
        long[] index = getIndex();
        if (index != null) {
            return value & ~(index[pos] ^ rootIndex[pos]);
        } else {
            return value & ~(reduceQuery(pos) ^ rootIndex[pos]);
        }
    }
}