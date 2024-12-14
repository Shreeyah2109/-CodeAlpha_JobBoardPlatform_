/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package job.board.platform;

/**
 *
 * @author DELL
 */


import java.sql.*;

public class Conn {
    // Variables for Connection and Statement
    Connection c;
    Statement s;

    public Conn() {
        try {
            // Database connection details
            String url = "jdbc:mysql://localhost:3306/jbp"; // Replace 'JobBoardDB' with your actual database name
            String user = "root"; // Replace 'root' with your database username
            String password = "Shreeya@1234h"; // Replace with your database password

            // Establishing the connection
            c = DriverManager.getConnection(url, user, password);

            // Initializing the Statement object
            s = c.createStatement();

            // Test query to check connection
            String testQuery = "SELECT 1";
            ResultSet rs = s.executeQuery(testQuery);
            if (rs.next()) {
                System.out.println("Database connected successfully! Test query result: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        new Conn(); // This will test the connection when executed
    }
}
