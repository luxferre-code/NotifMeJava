package fr.luxferrecode.notifme;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static fr.luxferrecode.notifme.Main.LOGGER;

public class DS {

    public static final String PATHNAME = System.getProperty("user.home") + "/.config/NotifMeConfig/config.prop";
    private String driver;
    private String url;
    private String user;
    private String password;
    private static DS instance = null;

    private DS() {
        Properties p = new Properties();
        if(!new File(PATHNAME).exists()) initFile();
        try {
            FileInputStream fis = new FileInputStream(PATHNAME);
            p.load(fis);
            fis.close();
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            System.exit(-1);
        }
        this.driver = p.getProperty("driver");
        this.url = p.getProperty("url");
        this.user = p.getProperty("login");
        this.password = p.getProperty("password");
        try { Class.forName(this.driver); }
        catch(ClassNotFoundException e) {
            LOGGER.severe(e.getMessage());
            System.exit(-1);
        }
    }

    private static void initFile() {
        if(!new File(PATHNAME).getParentFile().exists()) new File(PATHNAME).getParentFile().mkdirs();
        try(BufferedWriter br = new BufferedWriter(new FileWriter(new File(PATHNAME)))) {
            br.write("driver=org.mariadb.jdbc.Driver\n");
            br.write("url=JDBC\n");
            br.write("login=LOGIN\n");
            br.write("password=PASSWORD\n");
        } catch(IOException e) {
            LOGGER.severe(e.getMessage());
            System.exit(-1);
        }
    }

    public static Connection getConnection() throws SQLException {
        if(instance == null) instance = new DS();
        return DriverManager.getConnection(instance.url, instance.user, instance.password);
    }

}