package net.dozensbit.cache.core;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class Index
{
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final long[] index;
    private final String key;
    private final String value;

    public Index(final long[] index, final String key, final String value)
    {
        this.index = index;
        this.key = key;
        this.value = value;
    }

    public Index(final long[] index, final String fields)
    {
        this.index = index;
        String[] f = fields.split(":");

        if (f.length == 2) {
            this.key = f[KEY];
            this.value = f[VALUE];
        } else if (f.length == 1) {
            this.key = f[KEY];
            this.value = null;
        } else {
            throw new IllegalArgumentException("Wrong fields format: " + fields);
        }
    }

    public long[] getIndex()
    {
        return index;
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
