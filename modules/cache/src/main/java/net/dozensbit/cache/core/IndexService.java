package net.dozensbit.cache.core;

import org.apache.commons.collections.map.MultiValueMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IndexService implementation.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class IndexService
{
    /**
     * Bit count in long type.
     */
    public static final int BIT_COUNT = 64;

    /**
     * Positive value.
     */
    public static final long POSITIVE = ~0;

    private static final long[] masks = new long[BIT_COUNT];

    private final Map<String, BitSet> rawIndex = new HashMap<String, BitSet>();
    private final int length;

    private final long[] maskNegative;
    private final long[] maskPositive;

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

    /**
     * Constructor.
     *
     * @param length Length (bit counts) of a bit mask. Equals to count of objects in cache.
     */
    public IndexService(final int length)
    {
        this.length = length;

        int size = getIndexSize(length);

        maskNegative = new long[size];
        maskPositive = new long[size];

        Arrays.fill(maskPositive, -1);
    }

    /**
     * Get bit mask.
     *
     * @return Bit mask as array of long.
     */
    public static long[] getMasks()
    {
        return masks;
    }

    /**
     * Get search index.
     *
     * @param key Key.
     * @param value Value.
     * @return Search Index.
     */
    public Index getIndex(final String key, final String value)
    {
        return preparedIndex.get(getKey(key, value));
    }

    /**
     * Get search index length as a bits count.
     *
     * @return Search index.
     */
    public int getIndexLength()
    {
        return length;
    }

    /**
     * Get search index size as count of long.
     *
     * @return Search index size.
     */
    public int getIndexSize()
    {
        return getIndexSize(length);
    }

    private int getIndexSize(final int length)
    {
        int mod = (length % BIT_COUNT) == 0 ? 0 : 1;
        return (length / BIT_COUNT) + mod;
    }

    /**
     * Get positive bit mask.
     *
     * @return Bit mask.
     */
    public long[] getIndexPositive()
    {
        return maskPositive;
    }

    /**
     * Get negative bit mask.
     *
     * @return Bit mask.
     */
    public long[] getIndexNegative()
    {
        return maskNegative;
    }

    /**
     * Add search attributes to index.
     *
     * @param position index of object in a global object set.
     * @param tags Search attributes.
     */
    public void addToIndex(final int position, final MultiValueMap tags)
    {
        for (Object key : tags.keySet()) {

            BitSet rootBitSet = rawIndex.get(key);
            if (rootBitSet == null) {
                rootBitSet = createIndex(key.toString());
            }

            rootBitSet.set(position);

            Collection values = tags.getCollection(key);
            for (Object val : values) {
                String indexName = getKey(key.toString(), val.toString());

                BitSet bitSet = rawIndex.get(indexName);
                if (bitSet == null) {
                    bitSet = createIndex(indexName);
                }

                bitSet.set(position);
            }
        }
    }

    private BitSet createIndex(final String indexName)
    {
        BitSet bitSet = rawIndex.get(indexName);
        if (bitSet == null) {
            bitSet = new BitSet(length);
            rawIndex.put(indexName, bitSet);
        }

        return bitSet;
    }

    /**
     * Build and activate search attributes for engine.
     * Since this method called all objects and attributes were added to Cache are available for search.
     */
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
        return value == null ? key : key + ":" + value;
    }
}
