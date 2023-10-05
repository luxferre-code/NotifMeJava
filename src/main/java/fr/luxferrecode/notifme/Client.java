package fr.luxferrecode.notifme;

import fr.luxferrecode.notifme.pushbullet.InvalidApiKeyException;
import fr.luxferrecode.notifme.pushbullet.PushBullet;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Client {

    public final PushBullet pushBullet;
    public final Calendar calendar;

    public Client(PushBullet pb, Calendar calendar) {
        this.pushBullet = pb;
        this.calendar = calendar;
    }

    public Client(String apikey, Calendar c) throws InvalidApiKeyException {
        this(new PushBullet(apikey), c);
    }

    public Client(String apiKey, String icalLink) throws ParserException, IOException, InvalidApiKeyException {
        this(apiKey, new CalendarBuilder().build(new StringReader(getIcal(icalLink))));
    }

    private static String getIcal(String icalLink) {
        String ical = "";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(icalLink))
                .build();
        try {
            ical = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception ignored) {}
        return ical;
    }

    public String getTomorrowCalendar() {
        java.util.Calendar tomorrow = java.util.Calendar.getInstance();
        tomorrow.add(java.util.Calendar.DAY_OF_MONTH, 1);
        return getCalendar(tomorrow);
    }

    public String getCalendar(java.util.Calendar date) {
        ArrayList<String> events = new ArrayList<>();
        System.out.println(date);
        for (var component : calendar.getComponents()) {
            if (component.getProperty("DTSTART").getValue().equals(date)) {
                events.add(component.getProperty("SUMMARY").getValue());
            }
        }
        return String.join("\n", events);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client("o.u7Ior0MWpfmmg07QBJoT8jLIrZpimaSj", "https://edt-iut.univ-lille.fr/Telechargements/ical/Edt_THUILLIER.ics?version=2018.0.3.6&idICal=34461F31CEA9FAEFEA0520DF200B4BD3&param=643d5b312e2e36325d2666683d3126663d31");
        System.out.println(client.getTomorrowCalendar());
    }

}
