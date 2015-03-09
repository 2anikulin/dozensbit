package net.dozensbit.cache.query;

/**
 * Predicate interface.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public interface Predicate
{
    /**
     * Performs predicate operation.
     *
     * @param pos Index of object in a global set.
     * @param value Bit set.
     * @return Result of predicate operation.
     */
    long reduce(int pos, long value);
}
