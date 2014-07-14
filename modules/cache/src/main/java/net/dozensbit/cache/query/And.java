package net.dozensbit.cache.query;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class And extends AbstractPredicate
{
    public And(final long[] index)
    {
        init(index, null);
    }

    public And(final QueryBuilder.Query query)
    {
        init(null, query);
    }

    @Override
    public long reduce(int pos, long value)
    {
        long[] index = getIndex();
        if (index != null) {
            return index[pos] & value;
        } else {
            return reduceQuery(pos) & value;
        }
    }
}
