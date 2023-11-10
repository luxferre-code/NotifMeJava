package fr.luxferrecode.notifme;

import fr.luxferrecode.notifme.pushbullet.InvalidApiKeyException;
import fr.luxferrecode.notifme.pushbullet.PushBullet;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.CalendarComponent;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

public class Client {

    public static final String DTSTART = "DTSTART";
    public final PushBullet pushBullet;
    public final Calendar calendar;
    public static TimeZone defaultTZ;

    public Client(PushBullet pb, Calendar calendar) {
        this.pushBullet = pb;
        this.calendar = calendar;
        //this.defaultTZ = this.calendar.get TODO
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
        } catch(InterruptedException | IOException ignored) {
            // Do nothing
        }
        return ical;
    }

    public static String toStringCalendar(List<CalendarComponent> c) {
        StringBuilder sb = new StringBuilder();
        Collections.sort(c, (o1, o2) -> {
            try {
                return o1.getProperty(DTSTART).getValue().compareTo(o2.getProperty(DTSTART).getValue());
            } catch(Exception e) {
                return 0;
            }
        });
        for(var component : c) {
            String[] summary = component.getProperty("SUMMARY").getValue().split("-");
            if(summary.length > 7) {
                sb.append(summary[0]).append("-").append(summary[1]).append("- (Amphi)");
            } else {
                sb.append(component.getProperty("SUMMARY").getValue());
            }
            sb.append("\nSalle: ").append(component.getProperty("LOCATION").getValue());
            String start = dateParsing(new Date(Long.parseLong(component.getProperty(DTSTART).getValue())), defaultTZ, TimeZone.getTimeZone("Europe/Paris"));
            String end = addHour(component.getProperty("DTEND").getValue().substring(9, 11), 2) + ":" + component.getProperty("DTEND").getValue().substring(11, 13);
            sb.append("\n").append(start).append(" - ").append(end).append("\n\n");
        }
        return sb.substring(0, sb.length() - 2);
    }

    private static String addHour(String s, int hour) {
        LocalTime time = LocalTime.of(Integer.parseInt(s.substring(0, 2)), 0);
        time = time.plusHours(hour);
        return time.toString().substring(0, 2);
    }

    public static String dateParsing(Date date, TimeZone defaultTimezone, TimeZone toTimeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(defaultTimezone);
        sdf.setTimeZone(toTimeZone);
        return sdf.format(date);
    }

    public List<CalendarComponent> getSpecifiqueCalendar(int dayTimeout) {
        java.util.Calendar tomorrow = java.util.Calendar.getInstance();
        tomorrow.add(java.util.Calendar.DAY_OF_MONTH, dayTimeout);
        return getCalendar(tomorrow);
    }

    public List<CalendarComponent> getCalendar(java.util.Calendar date) {
        List<CalendarComponent> events = new ArrayList<>();
        for(var component : calendar.getComponents()) {
            if(component.getProperty(DTSTART).getValue().contains(String.format("%04d%02d%02d", date.get(java.util.Calendar.YEAR), date.get(java.util.Calendar.MONTH) + 1, date.get(java.util.Calendar.DAY_OF_MONTH)))) {
                events.add(component);
            }
        }
        return events;
    }

    public List<CalendarComponent> getTomorrowCalendar() {
        return getSpecifiqueCalendar(1);
    }

    public boolean push(String title, String text) {
        return pushBullet.push(title, text);
    }

    public boolean pushTomorrow() {
        if(toStringCalendar(getTomorrowCalendar()).isEmpty()) return false;
        return push("Emploi du temps", toStringCalendar(getTomorrowCalendar()));
    }

    public String getApiKey() {
        return this.pushBullet.getApiKey();
    }

}