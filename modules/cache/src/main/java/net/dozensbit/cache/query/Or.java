package net.dozensbit.cache.query;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class Or extends AbstractPredicate
{
    public Or(final long[] index)
    {
        super(index, null);
    }

    public Or(final QueryBuilder.Query query)
    {
        super(null, query);
    }

    @Override
    public long reduce(final int pos, final long value)
    {
        long[] index = getIndex();
        if (index != null) {
            return index[pos] | value;
        } else {
            return reduceQuery(pos) | value;
        }
    }
}
