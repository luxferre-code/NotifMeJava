package fr.luxferrecode.notifme;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("NotifMe");
        logger.info("~ NotifMe started ~");
        Set<Client> clients = new HashSet<>();
        Set<String> notValid = new HashSet<>();

        try(Connection con = DS.getConnection()) {
            logger.info("Connection to database established");
            Statement stmt = con.createStatement();
            String query = "SELECT apikey, ical FROM client WHERE isVerified = 1;";

            ResultSet result = stmt.executeQuery(query);
            while(result.next()) {
                String apikey = result.getString("apikey");
                String ical = result.getString("ical");
                try { clients.add(new Client(apikey, ical)); }
                catch(Exception e) {
                    logger.severe(e.getMessage());
                    notValid.add(apikey);
                }
            }

            logger.info("Sending notifications...");
            for(Client client : clients) {
                try {
                    if(!client.pushTomorrow()) {
                        logger.warning("Failed to send notification to " + client.getApiKey());
                    }
                }
                catch(Exception e) { logger.severe(e.getMessage()); }
            }

            logger.info("Sending notifications done");
            logger.info("Updating database...");
            con.setAutoCommit(false);
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            for(String s : notValid) {
                try {
                    query = "DELETE FROM client WHERE apikey = '" + s + "'";
                    System.out.println(query);
                    stmt.executeUpdate(query);
                } catch(Exception ignored) {}
            }

            con.commit();
            logger.info("Updating database done");
            logger.info("~ NotifMe ended ~");

        } catch(SQLException e) {
            logger.severe(e.getMessage());
        }
    }

}
