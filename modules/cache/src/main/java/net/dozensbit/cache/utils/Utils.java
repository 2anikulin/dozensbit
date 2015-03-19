package net.dozensbit.cache.utils;

import org.apache.commons.collections.map.MultiValueMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Utils implementation.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class Utils
{
    /**
     * Convert json values to MultiValueMap.
     * Example: String '{city:[ny, berlin], name:john}' -> map {city:[ny, berlin], name:john}
     *
     * Internal json objects converted to simple string
     *
     * @param tagsJson Attributes in Json format.
     * @return MultiValueMap.
     */
    public static MultiValueMap toMap(final  String tagsJson)
    {
        JSONObject jsonObject = new JSONObject(tagsJson);
        MultiValueMap ret = new MultiValueMap();

        for (String key : jsonObject.keySet()) {

            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                ret.put(key, value.toString());
            } else if (value instanceof JSONArray) {
                for (int i = 0; i < ((JSONArray) value).length(); i++) {
                    ret.put(
                            key,
                            primitiveToString(((JSONArray) value).get(i))
                    );
                }
            } else {
                ret.put(
                        key,
                        primitiveToString(value)
                );
            }
        }

        return ret;
    }

    private static String primitiveToString(final Object jsonPrimitive)
    {
        String ret;

        if (jsonPrimitive instanceof Number) {
            ret = JSONObject.numberToString((Number) jsonPrimitive);
        } else if (jsonPrimitive instanceof String) {
            ret = (String) jsonPrimitive;
        } else if (jsonPrimitive instanceof Boolean) {
            ret = Boolean.toString((Boolean) jsonPrimitive);
        } else {
            ret = jsonPrimitive.toString();
        }

        return ret;
    }
}
