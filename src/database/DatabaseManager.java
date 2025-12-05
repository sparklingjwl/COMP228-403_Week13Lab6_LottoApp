package database;

import java.sql.*;

// DatabaseManager class to handle database operations
public class DatabaseManager {
    
	// Method to save lottery result to the database
    public static void saveResult(int drawNumber, String numbers) {
        try {
            // Connect to the MySQL database
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/lotto_db", 
                "root",                                 
                "DreamCloud77"                          
            );
            
            // Create the SQL command to insert data
            String sql = "INSERT INTO lotto_results (draw_number, numbers) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            // Insert values into the statement
            stmt.setInt(1, drawNumber);
            stmt.setString(2, numbers);
            
            // Run the insert command
            stmt.executeUpdate();
            
            // Close resources
            stmt.close();
            conn.close();
            
            // Print confirmation
            System.out.println("Saved Draw " + drawNumber + " to MySQL database.");
            
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}