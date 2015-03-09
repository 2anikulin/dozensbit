package net.dozensbit.cache.core;

/**
 * Bit-index for key+value attributes.
 *
 * @author Anatoliy Nikulin.
 *         2anikulin@gmail.com
 */
public class Index
{
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final long[] index;
    private final String key;
    private final String value;

    /**
     * Constructor.
     *
     * @param index bit mask.
     * @param key Key.
     * @param value Value.
     */
    public Index(final long[] index, final String key, final String value)
    {
        this.index = index;
        this.key = key;
        this.value = value;
    }

    /**
     * Constructor.
     *
     * @param index bit mask.
     * @param fields Key-Value in format "key:value".
     */
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

    /**
     * Get bit mask.
     *
     * @return Bit mask as array of long.
     */
    public long[] getIndex()
    {
        return index;
    }

    /**
     * Get Key.
     *
     * @return Key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Get value.
     *
     * @return value.
     */
    public String getValue()
    {
        return value;
    }
}
