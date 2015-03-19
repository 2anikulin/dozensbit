package net.dozensbit.cache.utils;

import org.apache.commons.collections.map.MultiValueMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class UtilsTest {

    private static final String JSON_1 = "{\n" +
            "  \"name\" : \"john\",\n" +
            "  \"age\" : 32,\n" +
            "  \"id\" : 65465465465,\n" +
            "  \"factor\" : 0.99995,\n" +
            "  \"city\" : [\"berlin\", \"dresden\"],\n" +
            "  \"geo\" : {\"a\":54.1, \"l\":54.1}\n" +
            "}";

    @Test
    public void toMapTest() {
        MultiValueMap ret =Utils.toMap(JSON_1);
        Collection names = ret.getCollection("name");
        assertTrue("john".compareTo((String) names.toArray()[0]) == 0);

        Collection ages = ret.getCollection("age");
        assertTrue("32".compareTo((String) ages.toArray()[0]) == 0);

        Collection ids = ret.getCollection("id");
        assertTrue("65465465465".compareTo((String) ids.toArray()[0]) == 0);

        Collection factors = ret.getCollection("factor");
        assertTrue("0.99995".compareTo((String) factors.toArray()[0]) == 0);

        Collection cities = ret.getCollection("city");
        assertTrue("berlin".compareTo((String) cities.toArray()[0]) == 0);
        assertTrue("dresden".compareTo((String) cities.toArray()[1]) == 0);

        Collection geo = ret.getCollection("geo");
        assertTrue("{\"a\":54.1,\"l\":54.1}".compareTo((String) geo.toArray()[0]) == 0);

    }

}