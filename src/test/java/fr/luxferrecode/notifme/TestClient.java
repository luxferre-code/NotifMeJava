package fr.luxferrecode.notifme;

import fr.luxferrecode.notifme.pushbullet.InvalidApiKeyException;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

public class TestClient {

    /*Client c1 = new Client(APIKEY, ICAL);;
    Client c2 = new Client(APIKEY, ICAL);;
    public static final String APIKEY = "o.5vl5sbb7Lt4edZpm6H5bxGkD2KWTMvE6";
    public static final String ICAL = "https://edt-iut.univ-lille.fr/Telechargements/ical/Edt_THUILLIER.ics?version=2018.0.3.6&idICal=34461F31CEA9FAEFEA0520DF200B4BD3&param=643d5b312e2e36325d2666683d3126663d31";

    private TestClient() throws ParserException, IOException, InvalidApiKeyException {}

    @Test
    void test_constructor() {
        try {
            c2 = new Client(APIKEY, ICAL);
        } catch(Exception e) {
            Assertions.fail();
        }

        try {
            Client c3 = new Client("", ICAL);
        } catch(Exception e) {
            Assertions.assertEquals("Invalid API Key", e.getMessage());
        }

        try {
            Client c3 = new Client(APIKEY, "");
            Assertions.fail();
        } catch(Exception e) {
            // It's ok
        }
    }

    @Test
    void test_get_calendar() {
        Assertions.assertNotEquals(new ArrayList<CalendarComponent>(), c1.getTomorrowCalendar());
    }

    @Test
    void test_to_string_calendar() {
        Assertions.assertNotEquals("", c1.toStringCalendar(c1.getTomorrowCalendar()));
    }

    @Test
    void test_get_api_key() {
        Assertions.assertEquals(APIKEY, c1.getApiKey());
    }*/

}
