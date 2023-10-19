package fr.luxferrecode.notifme;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Main {

    public static final Logger LOGGER = Logger.getLogger("NotifMe");

    public static void main(String[] args) {
        LOGGER.info("~ NotifMe started ~");
        Set<Client> clients = new HashSet<>();
        Set<String> notValid = new HashSet<>();

        try(Connection con = DS.getConnection()) {
            LOGGER.info("Connection to database established");
            Statement stmt = con.createStatement();
            String query = "SELECT apikey, ical FROM client WHERE isVerified = 1;";

            ResultSet result = stmt.executeQuery(query);
            while(result.next()) {
                String apikey = result.getString("apikey");
                String ical = result.getString("ical");
                try { clients.add(new Client(apikey, ical)); }
                catch(Exception e) {
                    LOGGER.severe(e.getMessage());
                    notValid.add(apikey);
                }
            }

            LOGGER.info("Sending notifications...");
            for(Client client : clients) {
                try {
                    if(!client.pushTomorrow()) {
                        LOGGER.warning("Failed to send notification to " + client.getApiKey());
                    }
                }
                catch(Exception e) { LOGGER.severe(e.getMessage()); }
            }

            LOGGER.info("Sending notifications done");
            LOGGER.info("Updating database...");
            con.setAutoCommit(false);
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement ps = con.prepareStatement("DELETE FROM client WHERE apikey = ?");

            for(String s : notValid) {
                try {
                    ps.setString(1, s);
                    ps.addBatch();
                } catch(Exception ignored) {
                    // Do nothing
                }
            }

            ps.executeBatch();

            con.commit();
            LOGGER.info("Updating database done");
            LOGGER.info("~ NotifMe ended ~");

        } catch(SQLException e) {
            LOGGER.severe(e.getMessage());
        }
    }

}
