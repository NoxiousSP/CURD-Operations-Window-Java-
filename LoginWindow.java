import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Login window for user authentication
 */
public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    /**
     * Constructor for login window
     */
    public LoginWindow() {
        setTitle("Department Management System - Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel with form layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form panel with grid layout
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        formPanel.add(usernameField);
        
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listener for login button
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (DatabaseConnector.validateLogin(username, password)) {
                dispose(); // Close login window
                SwingUtilities.invokeLater(() -> new DepartmentListWindow());
            } else {
                JOptionPane.showMessageDialog(LoginWindow.this, 
                    "Invalid username or password!", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Add action listener for register button
        registerButton.addActionListener(e -> {
            showRegistrationDialog();
        });
        
        add(mainPanel);
        setVisible(true);
    }
    
    /**
     * Show registration request dialog
     */
    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Registration Request", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        formPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField(20);
        formPanel.add(emailField);
        
        formPanel.add(new JLabel("Designation:"));
        JTextField designationField = new JTextField(20);
        formPanel.add(designationField);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Submit Request");
        
        submitButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty() || 
                emailField.getText().trim().isEmpty() || 
                designationField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill all fields",
                    "Registration Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // In a real application, this would send the request to admin
            JOptionPane.showMessageDialog(dialog, 
                "Registration request sent to admin. Please wait for approval.", 
                "Request Sent", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        buttonPanel.add(submitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}
