import javax.swing.*;

/**
 * Main application class
 */
public class DepartmentManagementSystem {
    /**
     * Main method to launch application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize database connection
        DatabaseConnector.initializeDatabase();
        
        // Launch login window
        SwingUtilities.invokeLater(() -> new LoginWindow());
    }
}
