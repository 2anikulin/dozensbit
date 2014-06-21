package net.dozensbit.cache.core;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class Index
{
    private final long[] index;
    private final String key;
    private final String value;

    public Index(/*BitSet*/)
    {
        index = new long[10];
        key = "";
        value="";
    }

    public long[] getIndex()
    {
        return null;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
