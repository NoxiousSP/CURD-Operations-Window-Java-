import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Database connectivity class that handles all database operations
 */
public class DatabaseConnector {
    // Database connection details - UPDATED PATH
    private static final String DB_PATH = "C:\\Users\\dkg19\\OneDrive\\Desktop\\APP\\javaapp.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_PATH;
    
    /**
     * Initialize database and check connection
     */
    public static void initializeDatabase() {
        try {
            // Load JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Test connection
            try (Connection conn = getConnection()) {
                System.out.println("Database connection successful!");
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "SQLite JDBC Driver not found. Make sure sqlite-jdbc-3.49.1.0.jar is in classpath.", 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Database connection error: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }
    
    /**
     * Validate user login
     * @param username Username to validate
     * @param password Password to validate
     * @return true if credentials are valid, false otherwise
     */
    public static boolean validateLogin(String username, String password) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE uname = ? AND pwd = ?")) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Return true if user exists with matching credentials
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Database error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Get all departments from database
     * @return ResultSet containing departments data
     * @throws SQLException if query fails
     */
    public static ResultSet getAllDepartments() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM departments ORDER BY id");
    }
    
    /**
     * Search for department by ID
     * @param id Department ID to search for
     * @return ResultSet containing the department data
     * @throws SQLException if query fails
     */
    public static ResultSet getDepartmentById(int id) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM departments WHERE id = ?");
        pstmt.setInt(1, id);
        return pstmt.executeQuery();
    }
    
   /**
 * Insert a new department with a specific ID
 * @param id Department ID
 * @param schId School ID
 * @param deptCode Department code
 * @param name Department name
 * @param location Department location
 * @param email Department email
 * @return Number of rows affected
 * @throws SQLException if database error occurs
 */
public static int insertDepartmentWithId(int id, int schId, int deptCode, String name, String location, String email) throws SQLException {
    Connection conn = null;
    PreparedStatement stmt = null;
    
    try {
        conn = getConnection();
        
        // The SQL needs to specify the ID column and value
        String sql = "INSERT INTO departments (id, sch_id, dept_code, dept_name, dept_location, dept_email) VALUES (?, ?, ?, ?, ?, ?)";
        
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.setInt(2, schId);
        stmt.setInt(3, deptCode);
        stmt.setString(4, name);
        stmt.setString(5, location != null && !location.isEmpty() ? location : null);
        stmt.setString(6, email != null && !email.isEmpty() ? email : null);
        
        return stmt.executeUpdate();
    } finally {
        closeResources(conn, stmt, null);
    }
} 

    /**
     * Insert a new department with auto-generated ID
     * @param schId School ID
     * @param deptCode Department code
     * @param name Department name
     * @param location Department location
     * @param email Department email
     * @return Number of rows affected
     * @throws SQLException if query fails
     */
    public static int insertDepartment(int schId, int deptCode, String name, String location, String email) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO departments (sch_id, dept_code, dept_name, dept_location, dept_email) VALUES (?, ?, ?, ?, ?)")) {
            
            pstmt.setInt(1, schId);
            pstmt.setInt(2, deptCode);
            pstmt.setString(3, name);
            pstmt.setString(4, location);
            pstmt.setString(5, email);
            
            return pstmt.executeUpdate();
        }
    }
    /**
     * Update existing department
     * @param id Department ID to update
     * @param schId Updated school ID
     * @param deptCode Updated department code
     * @param name Updated department name
     * @param location Updated department location
     * @param email Updated department email
     * @return Number of rows affected
     * @throws SQLException if query fails
     */
    public static int updateDepartment(int id, int schId, int deptCode, String name, String location, String email) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE departments SET sch_id = ?, dept_code = ?, dept_name = ?, dept_location = ?, dept_email = ? WHERE id = ?")) {
            
            pstmt.setInt(1, schId);
            pstmt.setInt(2, deptCode);
            pstmt.setString(3, name);
            pstmt.setString(4, location);
            pstmt.setString(5, email);
            pstmt.setInt(6, id);
            
            return pstmt.executeUpdate();
        }
    }
    
    /**
     * Delete department by ID
     * @param id Department ID to delete
     * @return Number of rows affected
     * @throws SQLException if query fails
     */
    public static int deleteDepartment(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM departments WHERE id = ?")) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();
        }
    }
    
    /**
     * Utility method to safely close database resources
     * @param conn Database connection to close
     * @param stmt Statement to close
     * @param rs ResultSet to close
     */
    private static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
