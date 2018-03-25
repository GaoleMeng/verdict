import java.sql.*;



public class VerdictJdbc {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("edu.umich.verdict.jdbc.Driver");
        String url = "jdbc:verdict:impala://salat1.eecs.umich.edu:21050/instacart100g";

        Connection conn = DriverManager.getConnection(url);
        System.out.println("connection finished");
        Statement stmt = conn.createStatement();
        String sql2 = "select count(order_number) from orders where user_id=1";
        ResultSet rs = stmt.executeQuery(sql2);

        ResultSetMetaData rsmd = rs.getMetaData();

        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            System.out.println(rsmd.getColumnName(i));
        }
//        System.out.println(rs.getString(2));

        while (rs.next()) {
//            int tmp = rs.getInt(3);
            System.out.println(rs.getString(1));
            System.out.println(rs.getString(2));
//            System.out.println(rs.getString(3));
//            System.out.println(rs.getn);
//            System.out.println(rs.getString(3));
//            System.out.println(rs.getString(4));
//            System.out.println(rs.getString(5));
//            System.out.println(rs.getString(2));
        }
        conn.close();
        rs.close();
    }
}