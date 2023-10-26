package fr.luxferrecode.notifme;

import net.fortuna.ical4j.model.component.CalendarComponent;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class Main {

    public static final Logger LOGGER = Logger.getLogger("NotifMe");

    public static void main(String[] args) {
        LOGGER.info("~ NotifMe started ~");
        if(args.length == 0) {
            LOGGER.severe("Usage: java -jar NotifMe.jar [absences|notifall]");
        } else {
            switch(args[0]) {
                case "absences":
                    absences();
                    break;
                case "notifall":
                    notifAll();
                    break;
                default:
                    LOGGER.severe("Usage: java -jar NotifMe.jar [absences|notifall]");
            }
        }
    }

    public static void removeOnDB(Connection con, Set<String> apikeys) {
        LOGGER.info("Deleting " + apikeys.size() + " apikeys from database");
        try {
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("DELETE FROM client WHERE apikey = ?");
            for(String a : apikeys) {
                ps.setString(1, a);
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
            ps.close();
        } catch(SQLException e) {
            LOGGER.severe(e.getMessage());
            try {
                con.rollback();
            } catch(Exception ignored) {
                //DO nothing
            }
        }

        LOGGER.info("Deleting done");
    }

    public static Set<Client>[] loadClients(Connection con) throws SQLException {
        Set<Client> clients = new HashSet<>();
        Set<String> notValid = new HashSet<>();
        Statement stmt = con.createStatement();
        String query = "SELECT apikey, ical FROM client WHERE isVerified = 1;";

        ResultSet result = stmt.executeQuery(query);
        while(result.next()) {
            String apikey = result.getString("apikey");
            String ical = result.getString("ical");
            try {
                clients.add(new Client(apikey, ical));
            } catch(Exception e) {
                LOGGER.severe(e.getMessage());
                notValid.add(apikey);
            }
        }
        stmt.close();
        return new Set[]{clients, notValid};
    }

    public static void notifAll() {
        Set<Client> clients;
        Set<String> notValid;

        try(Connection con = DS.getConnection()) {
            LOGGER.info("Connection to database established");

            Set[] sets = loadClients(con);
            clients = (Set<Client>) sets[0];
            notValid = (Set<String>) sets[1];

            LOGGER.info("Sending notifications...");
            for(Client client : clients) {
                try {
                    if(!client.pushTomorrow()) {
                        LOGGER.warning("Failed to send notification to " + client.getApiKey());
                    }
                } catch(Exception e) {
                    LOGGER.severe(e.getMessage());
                }
            }

            LOGGER.info("Sending notifications done");
            removeOnDB(con, notValid);

        } catch(SQLException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public static void absences() {
        Set<Client> clients;
        Set<String> notValid;
        Map<Client, List<String>> absences = new HashMap<>();

        try(Connection con = DS.getConnection()) {
            LOGGER.info("Connection to database established");

            Set[] sets = loadClients(con);
            clients = (Set<Client>) sets[0];
            notValid = (Set<String>) sets[1];

            getAbsences(clients, absences);

            LOGGER.info("Checking for absences done");
            LOGGER.info("Checking on database...");

            con.setAutoCommit(false);
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM absences WHERE apikey = ? AND cours = ? AND date = CURRENT_DATE LIMIT 1");
            PreparedStatement ps2 = con.prepareStatement("INSERT INTO absences(apikey, cours) VALUES(?, ?)");

            for(Map.Entry<Client, List<String>> entry : absences.entrySet()) {
                Client client = entry.getKey();
                List<String> cours = entry.getValue();
                for(String c : cours) {
                    try {
                        ps.setString(1, client.getApiKey());
                        ps.setString(2, c);
                        ResultSet rs = ps.executeQuery();
                        if(!rs.next()) {
                            ps2.setString(1, client.getApiKey());
                            ps2.setString(2, c);
                            ps2.addBatch();
                            client.push(
                                    "Absence !",
                                    "Le cours " + c + " est absent aujourd'hui !"
                            );
                        }
                    } catch(Exception ignored) {
                        // Do nothing
                    }
                }
            }

            ps2.executeBatch();
            con.commit();
            ps.close();
            ps2.close();

            removeOnDB(con, notValid);

        } catch(SQLException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    private static void getAbsences(Set<Client> clients, Map<Client, List<String>> absences) {
        LOGGER.info("Checking for absences...");
        for(Client client : clients) {
            List<CalendarComponent> calendar = client.getSpecifiqueCalendar(0);
            for(CalendarComponent cc : calendar) {
                String summary = cc.getProperty("SUMMARY").getValue();
                if(summary.toLowerCase().contains("absent")) {
                    if(absences.containsKey(client)) {
                        absences.get(client).add(summary);
                    } else {
                        absences.put(client, new ArrayList<>(Collections.singletonList(summary)));
                    }
                }
            }
        }
    }

}
