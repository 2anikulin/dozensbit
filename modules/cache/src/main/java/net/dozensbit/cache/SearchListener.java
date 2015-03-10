package net.dozensbit.cache;

/**
 * Search event listener. Here you can implement additional business logic.
 *
 * Called each time when object found.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public interface SearchListener<T>
{

    /**
     * Object found event.
     *
     * @param object Found object.
     * @return True - object added to the output set, False - not.
     */
    boolean objectFoundEvent(final T object);
}
