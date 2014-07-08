package net.dozensbit.cache.query;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class OrNot implements Predicate
{
    private final long[] index;

    public OrNot(final long[] index)
    {
        this.index = index;
    }

    @Override
    public long reduce(int pos, long value)
    {
        return ~index[pos] | value;
    }
}
