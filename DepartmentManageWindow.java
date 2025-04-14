import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;
import java.sql.*;

/**
 * Window for managing departments (insert, update, delete)
 */
public class DepartmentManageWindow extends JDialog {
    private JTextField idField, schIdField, deptCodeField, nameField, locationField, emailField;
    private DepartmentListWindow parentWindow;
    
    /**
     * Constructor for department management window
     * @param parent Parent window reference
     */
    public DepartmentManageWindow(DepartmentListWindow parent) {
        super(parent, "Manage Departments", true);
        this.parentWindow = parent;
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        
        formPanel.add(new JLabel("ID (for update/delete/specific insert):"));
        idField = new JTextField(10);
        formPanel.add(idField);
        
        formPanel.add(new JLabel("School ID:"));
        schIdField = new JTextField(10);
        formPanel.add(schIdField);
        
        formPanel.add(new JLabel("Department Code:"));
        deptCodeField = new JTextField(10);
        formPanel.add(deptCodeField);
        
        formPanel.add(new JLabel("Department Name:"));
        nameField = new JTextField(20);
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Location:"));
        locationField = new JTextField(20);
        formPanel.add(locationField);
        
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        formPanel.add(emailField);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton insertButton = new JButton("Insert");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        
        buttonPanel.add(insertButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        insertButton.addActionListener(e -> {
            if (validateInputFields(false)) {
                insertDepartment();
            }
        });
        
        updateButton.addActionListener(e -> {
            if (validateInputFields(true)) {
                updateDepartment();
            }
        });
        
        deleteButton.addActionListener(e -> {
            if (!idField.getText().trim().isEmpty()) {
                deleteDepartment();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please enter the ID of the department to delete", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        clearButton.addActionListener(e -> {
            clearFields();
        });
        
        add(mainPanel);
        setVisible(true);
    }
    
    /**
     * Validate input fields
     * @param requireId true if ID field is required
     * @return true if validation passes, false otherwise
     */
    private boolean validateInputFields(boolean requireId) {
        // For update and delete, ID is required
        if (requireId && idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter the ID of the department", 
                "Missing Information", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Check required fields for insert and update
        if (schIdField.getText().trim().isEmpty() || 
            deptCodeField.getText().trim().isEmpty() || 
            nameField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "School ID, Department Code and Name are required", 
                "Missing Information", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validate numeric fields
        try {
            if (!schIdField.getText().trim().isEmpty()) {
                Integer.parseInt(schIdField.getText().trim());
            }
            if (!deptCodeField.getText().trim().isEmpty()) {
                Integer.parseInt(deptCodeField.getText().trim());
            }
            if (!idField.getText().trim().isEmpty()) {
                Integer.parseInt(idField.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "School ID, Department Code and ID must be numeric", 
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Insert new department
     */
    private void insertDepartment() {
        try {
            Integer id = null;
            if (!idField.getText().trim().isEmpty()) {
                id = Integer.parseInt(idField.getText().trim());
            }
            
            int schId = Integer.parseInt(schIdField.getText().trim());
            int deptCode = Integer.parseInt(deptCodeField.getText().trim());
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String email = emailField.getText().trim();
            
            int rowsAffected;
            
            // If ID is provided, use it for insertion
            if (id != null) {
                rowsAffected = DatabaseConnector.insertDepartmentWithId(id, schId, deptCode, name, location, email);
            } else {
                rowsAffected = DatabaseConnector.insertDepartment(schId, deptCode, name, location, email);
            }
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Department added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh parent window and clear fields
                clearFields();
                parentWindow.loadDepartmentData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error adding department: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update existing department
     */
    private void updateDepartment() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            int schId = Integer.parseInt(schIdField.getText().trim());
            int deptCode = Integer.parseInt(deptCodeField.getText().trim());
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String email = emailField.getText().trim();
            
            int rowsAffected = DatabaseConnector.updateDepartment(id, schId, deptCode, name, location, email);
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Department updated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh parent window and clear fields
                clearFields();
                parentWindow.loadDepartmentData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No department found with ID " + idField.getText().trim(), 
                    "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error updating department: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Delete department
     */
    private void deleteDepartment() {
        int confirmResult = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the department with ID " + idField.getText().trim() + "?",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirmResult == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                
                int rowsAffected = DatabaseConnector.deleteDepartment(id);
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Department deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh parent window and clear fields
                    clearFields();
                    parentWindow.loadDepartmentData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No department found with ID " + idField.getText().trim(), 
                        "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error deleting department: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Clear all input fields
     */
    private void clearFields() {
        idField.setText("");
        schIdField.setText("");
        deptCodeField.setText("");
        nameField.setText("");
        locationField.setText("");
        emailField.setText("");
    }
}