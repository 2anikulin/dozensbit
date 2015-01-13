package net.dozensbit.cache;

import org.apache.commons.collections.map.MultiValueMap;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class TestUtils {

    public static MultiValueMap toMap(final  String tags)
    {
        MultiValueMap ret = new MultiValueMap();
        String[] pairs = tags.split(",");

        for (String pair : pairs) {
            String[] kv = pair.split(":");
            if (kv.length != 2) {
                throw  new IllegalArgumentException("Wrong input format:" + pair);
            }

            ret.put(kv[0], kv[1]);
        }

        return ret;
    }
}
