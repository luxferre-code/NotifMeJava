package fr.luxferrecode.notifme.pushbullet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.management.openmbean.KeyAlreadyExistsException;

class TestJSONObject {

    static JSONObject js = new JSONObject();

    private TestJSONObject() {
    }

    @Test
    void test_add() {
        js.add("test", "test");
        Assertions.assertTrue(js.isIn("test"));
        try {
            js.add("test", "testModified");
            Assertions.fail();
        } catch(KeyAlreadyExistsException e) {
            // It's ok
        }
        try {
            js.add(null, "test");
            Assertions.fail();
        } catch(NullPointerException e) {
            // It's ok
        }
        try {
            js.add("test", null);
            Assertions.fail();
        } catch(NullPointerException e) {
            // It's ok
        }
        js = new JSONObject();
    }

    @Test
    void test_modify() {
        js.add("test", "test");
        try {
            js.modify("test", "testModified");
        } catch(Exception e) {
            Assertions.fail();
        }
        try {
            js.modify(null, "test");
            Assertions.fail();
        } catch(NullPointerException e) {
            // It's ok
        } catch(KeyNotFoundException e) {
            Assertions.fail();
        }
        try {
            js.modify("test", null);
            Assertions.fail();
        } catch(NullPointerException e) {
            // It's ok
        } catch(KeyNotFoundException e) {
            Assertions.fail();
        }
        try {
            js.modify("testModified", "test");
            Assertions.fail();
        } catch(NullPointerException e) {
            Assertions.fail();
        } catch(KeyNotFoundException e) {
            // It's ok
        }
        js = new JSONObject();
    }

    @Test
    void test_remove() {
        js.add("test", "test");
        try {
            js.delete("test");
        } catch(Exception e) {
            Assertions.fail();
        }
        try {
            js.delete(null);
            Assertions.fail();
        } catch(NullPointerException e) {
            // It's ok
        } catch(KeyNotFoundException e) {
            Assertions.fail();
        }
        try {
            js.delete("test");
            Assertions.fail();
        } catch(NullPointerException e) {
            Assertions.fail();
        } catch(KeyNotFoundException e) {
            // It's ok
        }
        js = new JSONObject();
    }

    @Test
    void test_toString() {
        js.add("test", "test");
        Assertions.assertEquals("{\"test\":\"test\"}", js.toString());
        js.add("test2", "test2");
        Assertions.assertEquals("{\"test2\":\"test2\",\"test\":\"test\"}", js.toString());
        js = new JSONObject();
    }

    @Test
    void test_isIn() {
        js.add("test", "test");
        Assertions.assertTrue(js.isIn("test"));
        Assertions.assertFalse(js.isIn("test2"));
        js = new JSONObject();
    }

}
