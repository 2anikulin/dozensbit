package net.dozensbit.cache.query;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class OrNot extends AbstractPredicate
{
    public OrNot(final long[] index)
    {
        init(index, null);
    }

    public OrNot(final QueryBuilder.Query query)
    {
        init(null, query);
    }

    @Override
    public long reduce(int pos, long value)
    {
        long[] index = getIndex();
        if (index != null) {
            return ~index[pos] | value;
        } else {
            return ~reduceQuery(pos) | value;
        }
    }
}
