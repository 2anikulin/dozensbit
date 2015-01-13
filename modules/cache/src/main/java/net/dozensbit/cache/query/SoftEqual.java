package net.dozensbit.cache.query;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class SoftEqual extends AbstractPredicate {

    private final long[] rootIndex;

    public SoftEqual(final long[] index, final long[] rootIndex)
    {
        super(index, null);
        this.rootIndex = rootIndex;
    }

    public SoftEqual(final QueryBuilder.Query query)
    {
        super(null, query);
        this.rootIndex = null;
    }

    @Override
    public long reduce(int pos, long value)
    {
        long[] index = getIndex();
        if (index != null) {
            return value & ~(index[pos] ^ rootIndex[pos]);
        } else {
            return value & ~(reduceQuery(pos) ^ rootIndex[pos]);
        }
    }
}