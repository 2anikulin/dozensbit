package net.dozensbit.cache.core;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class IndexService
{
    public static final int BIT_COUNT = 64;
    private static final long[] masks = new long[BIT_COUNT];

    private final Map<String, BitSet> rawIndex = new HashMap<String, BitSet>();
    private final int length;

    private long maskNegative[];
    private long maskPositive[];

    private final Map<String, Index> preparedIndex = new ConcurrentHashMap<String, Index>();

    static
    {
        long mask = Long.MIN_VALUE;
        masks[0] = mask;

        for (int i = 1; i < BIT_COUNT; i++) {
            mask = mask >>> 1;
            masks[i] = mask;
        }
    }

    public static long[] getMasks()
    {
        return masks;
    }

    public IndexService(final int length)
    {
        this.length = length;

        int size = getIndexSize(length);

        maskNegative = new long[size];
        maskPositive = new long[size];

        Arrays.fill(maskPositive, -1);
    }

    public Index getIndex(final String key, final String value)
    {
        return preparedIndex.get(getKey(key, value));
    }

    public int getIndexLength()
    {
        return length;
    }

    public int getIndexSize()
    {
        return getIndexSize(length);
    }

    private int getIndexSize(final int length)
    {
        int mod = (length % BIT_COUNT) == 0 ? 0 : 1;
        return (length / BIT_COUNT) + mod;
    }

    public long[] getIndexPositive()
    {
        return maskPositive;
    }

    public long[] getIndexNegative()
    {
        return maskNegative;
    }

    public void addToIndex(final int position, final Map<String, String> tags)
    {
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            String key = getKey(entry.getKey(), entry.getValue());

            BitSet bitSet = rawIndex.get(key);
            if (bitSet == null) {
                bitSet = new BitSet(length);
                rawIndex.put(key, bitSet);
            }

            bitSet.set(position);
        }
    }

    public void build()
    {
        for (Map.Entry<String, BitSet> entry : rawIndex.entrySet()) {
            BitSet bitSet = entry.getValue();

            long[] mask = new long[getIndexSize()];

            for (int i = 0; i < bitSet.size(); i++) {
                if (bitSet.get(i)) {
                    int arrayPos  = i / BIT_COUNT;
                    int maskPos  = i % BIT_COUNT;

                    mask[arrayPos] = mask[arrayPos] | masks[maskPos];
                }
            }

            Index index = new Index(mask, entry.getKey());
            preparedIndex.put(entry.getKey(), index);
        }
    }

    private String getKey(final String key, final String value)
    {
        return key + ":" + value;
    }
}
