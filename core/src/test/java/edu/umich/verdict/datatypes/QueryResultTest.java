package edu.umich.verdict.datatypes;

import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.verdictdb.type.module.VerdictResultSet;
import org.junit.Test;

import java.io.IOException;
import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.apache.derby.jdbc.EmbeddedDriver;



public class QueryResultTest {

    @Test
    public void testQueryResult() throws SQLException, ClassNotFoundException {

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        String dbURL = "jdbc:derby:tesd/webdb;create=true";
        Connection conn = DriverManager.getConnection(dbURL);
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("CREATE TABLE NAMES (ID INT PRIMARY KEY, NAME VARCHAR(12), GPA DOUBLE)");
        System.out.println("Created Table");
        stmt.executeUpdate("INSERT INTO NAMES VALUES (1,'TOM', 0.1),(2,'BILL', 0.2),(3,'AMY', 0.2),(4,'OWEN', 0.2)");
        System.out.println("Populated Table");

        ResultSet rs = stmt.executeQuery("SELECT * FROM NAMES");

        try {
            VerdictResultSet tmp = new VerdictResultSet(rs);

            ArrayList<HashMap<String, Object>> rows = tmp.getQueryResult().getRows();

            for (int i = 0; i < rows.size(); i++) {
                HashMap<String, Object> mp = rows.get(i);

                for (String key : mp.keySet()) {
                    System.out.println("Key = " + key + " - " + mp.get(key));
                }
            }

            while (tmp.next()) {
                System.out.println(tmp.getString(2));
                System.out.println(tmp.getInt(1));
            }
            stmt.executeUpdate("DROP TABLE NAMES");
        }
        catch (Exception e) {
            stmt.executeUpdate("DROP TABLE NAMES");
            throw new SQLException();
        }
        System.out.println("Done");
    }

}
