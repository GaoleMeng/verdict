package edu.umich.verdict.datatypes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umich.verdict.VerdictConf;
import org.junit.Test;

import java.io.IOException;
import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.apache.derby.jdbc.EmbeddedDriver;


public class VerdictResultSetTest {

    @Test
    public void testBasicRetrival() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        String dbURL = "jdbc:derby:tdbssxxrwxxz/webdb;create=true";
        Connection conn = DriverManager.getConnection(dbURL);
        Statement stmt = conn.createStatement();

//        stmt.executeUpdate("DROP TABLE NAMES");

        stmt.executeUpdate("CREATE TABLE NAMES (ID INT PRIMARY KEY, NAME VARCHAR(12))");
        System.out.println("Created Table");
        stmt.executeUpdate("INSERT INTO NAMES VALUES (1,'TOM'),(2,'BILL'),(3,'AMY'),(4,'OWEN')");
        System.out.println("Populated Table");

        ResultSet rs = stmt.executeQuery("SELECT * FROM NAMES");

        try {
            VerdictResultSet verdictResultSet;
            verdictResultSet = new VerdictResultSet(rs);
            while (verdictResultSet.next()) {
                System.out.println(verdictResultSet.getString(2));
                System.out.println(verdictResultSet.getInt(1));
            }
            stmt.executeUpdate("DROP TABLE NAMES");
        }
        catch (Exception e) {
            stmt.executeUpdate("DROP TABLE NAMES");
            throw new SQLException();
        }

        System.out.println("Done");
    }

    @Test
    public void testDeleteColumn() throws SQLException, ClassNotFoundException {

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        String dbURL = "jdbc:derby:txSqqmxxwmfzlete/webdb;create=true";
        Connection conn = DriverManager.getConnection(dbURL);
        Statement stmt = conn.createStatement();
//        stmt.executeUpdate("DROP TABLE NAMES");

        stmt.executeUpdate("CREATE TABLE NAMES (ID INT PRIMARY KEY, NAME VARCHAR(12))");
        System.out.println("Created Table");
        stmt.executeUpdate("INSERT INTO NAMES VALUES (1,'TOM'),(2,'BILL'),(3,'AMY'),(4,'OWEN')");
        System.out.println("Populated Table");

        ResultSet rs = stmt.executeQuery("SELECT * FROM NAMES");

        try {
            VerdictResultSet verdictResultSet;
            verdictResultSet = new VerdictResultSet(rs);
            System.out.println(verdictResultSet.getMetaData().getColumnCount());
            System.out.println("after delete");
            verdictResultSet.deleteColumn("NAME");
            System.out.println(verdictResultSet.getMetaData().getColumnCount());
            stmt.executeUpdate("DROP TABLE NAMES");
        }
        catch (Exception e) {
            stmt.executeUpdate("DROP TABLE NAMES");
            throw new SQLException();
        }

        System.out.println("Done");
    }

    @Test
    public void testMinimumGroup() throws SQLException, ClassNotFoundException {

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        String dbURL = "jdbc:derby:texxzppsssfnluzzzm/webdb;create=true";
        Connection conn = DriverManager.getConnection(dbURL);
        Statement stmt = conn.createStatement();

//        stmt.executeUpdate("DROP TABLE NAMES");
        stmt.executeUpdate("CREATE TABLE NAMES (ID INT PRIMARY KEY, ID_err DOUBLE, verdict_group_count INT)");
        System.out.println("Created Table");
        stmt.executeUpdate("INSERT INTO NAMES VALUES (1,0.2,100)," +
                "(2,0.2,100),(3,0.2,100),(5,0.2,100),(6,0.2,100),(7,0.2,100)");
        System.out.println("Populated Table");

//        ResultSet rs = stmt.executeQuery("SELECT AVG(ID) FROM NAMES WHERE NAME='TOM'");
        ResultSet rs = stmt.executeQuery("SELECT * FROM NAMES");
        try {
            VerdictResultSet tmp;
            tmp = new VerdictResultSet(rs);

            tmp.checkAndRevise(10, (new VerdictConf()).getTrustErrorBound());

            while (tmp.next()) {
                System.out.println(tmp.getDouble(2));
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
