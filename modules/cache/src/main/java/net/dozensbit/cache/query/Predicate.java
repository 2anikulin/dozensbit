package net.dozensbit.cache.query;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public interface Predicate
{
    long reduce(int pos, long value);
}
