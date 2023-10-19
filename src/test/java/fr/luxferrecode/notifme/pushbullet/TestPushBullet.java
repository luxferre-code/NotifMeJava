package fr.luxferrecode.notifme.pushbullet;

import fr.luxferrecode.notifme.TestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestPushBullet {

    static PushBullet p;

    static {
        try {
            p = new PushBullet(TestClient.APIKEY);
        } catch(InvalidApiKeyException e) {
            throw new RuntimeException(e);
        }
    }

    ;

    @Test
    void test_constructor() {
        try {
            p = new PushBullet(TestClient.APIKEY);
        } catch(Exception e) {
            Assertions.fail();
        }
        try {
            PushBullet p2 = new PushBullet("");
            Assertions.fail();
        } catch(Exception e) {
            Assertions.assertEquals("Invalid API Key", e.getMessage());
        }
    }

    @Test
    void test_is_valid() {
        Assertions.assertTrue(p.isValid());
    }

}
