package com.alterdekim.freedom.reseeder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DB {

    private String URL = "jdbc:mysql://";

    private String properties = "?useSSL=false&autoReconnect=true&serverTimezone=UTC&verifyServerCertificate=false";

    private Connection con;

    public void connect( String domain, String port, String db, String username, String password ) {
        try {
            con = DriverManager.getConnection(URL + domain + ":" + port + "/" + db + properties, username, password);
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public Statement getS() {
        try { return con.createStatement(); } catch( Exception e ) { e.printStackTrace(); }
        return null;
    }

    public ResultSet query(String query ) {
        try { return getS().executeQuery(query); } catch(Exception e) { e.printStackTrace(); }
        return null;
    }
}
