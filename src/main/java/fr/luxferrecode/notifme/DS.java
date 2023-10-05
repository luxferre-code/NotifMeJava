package fr.luxferrecode.notifme;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DS {

    private String driver, url, user, password;
    private static DS instance = null;

    private DS() {
        Properties p = new Properties();
        if(!new File("config.prop").exists()) initFile();
        try {
            p.load(new FileInputStream("config.prop"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        this.driver = p.getProperty("driver");
        this.url = p.getProperty("url");
        this.user = p.getProperty("login");
        this.password = p.getProperty("password");
        try { Class.forName(this.driver); }
        catch(ClassNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void initFile() {
        try(BufferedWriter br = new BufferedWriter(new FileWriter(new File("config.prop")))) {
            br.write("driver=org.mariadb.jdbc.Driver\n");
            br.write("url=JDBC\n");
            br.write("login=LOGIN\n");
            br.write("password=PASSWORD\n");
        } catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static Connection getConnection() throws SQLException {
        if(instance == null) instance = new DS();
        return DriverManager.getConnection(instance.url, instance.user, instance.password);
    }
}