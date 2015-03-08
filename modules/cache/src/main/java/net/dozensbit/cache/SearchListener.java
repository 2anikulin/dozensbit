package net.dozensbit.cache;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public interface SearchListener<T> {

    boolean onObjectFound(T object);
}
