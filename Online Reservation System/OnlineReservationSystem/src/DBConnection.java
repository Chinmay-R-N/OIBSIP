import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/reservationdb";
    private static final String USER = "root";
    private static final String PASS = "Chinnu@01";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
