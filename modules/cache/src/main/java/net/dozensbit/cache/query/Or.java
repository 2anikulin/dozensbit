package net.dozensbit.cache.query;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class Or implements Predicate
{
    private final long[] index;

    public Or(final long[] index)
    {
        this.index = index;
    }

    @Override
    public long reduce(int pos, long value)
    {
        return index[pos] | value;
    }
}
