import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/reservationdb";
    private static final String USER = "root"; // Enter your MySQL user ID here
    private static final String PASS = ""; // Enter your MySQL password here

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

