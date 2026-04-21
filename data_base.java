import java.sql.*;

public class data_base {
    
    // This method creates the connection and hands it back to whoever asked for it
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection using your specific credentials
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/apachedatabase", "root", "123098"
            );
            
            System.out.println("Database connected successfully!");
            
        } catch (Exception e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
        }
        
        // Returns the open connection (or null if it failed)
        return connection;
    }
}