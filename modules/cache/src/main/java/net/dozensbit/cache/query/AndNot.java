package net.dozensbit.cache.query;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class AndNot extends AbstractPredicate
{
    public AndNot(final long[] index)
    {
        super(index, null);
    }

    public AndNot(final QueryBuilder.Query query)
    {
        super(null, query);
    }

    @Override
    public long reduce(int pos, long value)
    {
        long[] index = getIndex();
        if (index != null) {
            return ~index[pos] & value;
        } else {
            return ~reduceQuery(pos) & value;
        }
    }
}
