import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class DepartmentEditor extends JFrame {
    private JLabel lblId, lblSchId, lblCode, lblName, lblLocation, lblEmail;
    private JTextField txtId, txtSchId, txtCode, txtName, txtLocation, txtEmail;
    private JButton btnSave, btnDelete, btnCancel;
    private Connection conn;
    private DepartmentListWindow parentView;
    private boolean isEditMode;
    private int editingId;
    
    // Constructor for new department
    public DepartmentEditor(DepartmentListWindow parent) {
        try {
            this.parentView = parent;
            this.isEditMode = false;
            this.conn = DatabaseConnector.getConnection();
            initComponents("Add New Department");
            // Remove delete button when in insert mode
            btnDelete.setVisible(false);
            setLocationRelativeTo(parent);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    
    // Constructor for editing existing department
    public DepartmentEditor(DepartmentListWindow parent, int deptId) {
        try {
            this.parentView = parent;
            this.isEditMode = true;
            this.editingId = deptId;
            this.conn = DatabaseConnector.getConnection();
            initComponents("Edit Department");
            loadDepartmentData(deptId);
            setLocationRelativeTo(parent);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    
    private void initComponents(String title) {
        setTitle(title);
        setSize(400, 350);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        
        // Creating labels
        lblId = new JLabel("ID:");
        lblId.setBounds(30, 30, 100, 25);
        add(lblId);
        
        lblSchId = new JLabel("School ID:");
        lblSchId.setBounds(30, 70, 100, 25);
        add(lblSchId);
        
        lblCode = new JLabel("Dept Code:");
        lblCode.setBounds(30, 110, 100, 25);
        add(lblCode);
        
        lblName = new JLabel("Dept Name:");
        lblName.setBounds(30, 150, 100, 25);
        add(lblName);
        
        lblLocation = new JLabel("Location:");
        lblLocation.setBounds(30, 190, 100, 25);
        add(lblLocation);
        
        lblEmail = new JLabel("Email:");
        lblEmail.setBounds(30, 230, 100, 25);
        add(lblEmail);
        
        // Creating text fields
        txtId = new JTextField();
        txtId.setBounds(140, 30, 200, 25);
        add(txtId);
        
        txtSchId = new JTextField();
        txtSchId.setBounds(140, 70, 200, 25);
        add(txtSchId);
        
        txtCode = new JTextField();
        txtCode.setBounds(140, 110, 200, 25);
        add(txtCode);
        
        txtName = new JTextField();
        txtName.setBounds(140, 150, 200, 25);
        add(txtName);
        
        txtLocation = new JTextField();
        txtLocation.setBounds(140, 190, 200, 25);
        add(txtLocation);
        
        txtEmail = new JTextField();
        txtEmail.setBounds(140, 230, 200, 25);
        add(txtEmail);
        
        // ID field should be editable only for new departments
        if (isEditMode) {
            txtId.setEditable(false);
        }
        
        // Creating buttons
        btnSave = new JButton("Save");
        btnSave.setBounds(60, 270, 80, 30);
        add(btnSave);
        
        btnDelete = new JButton("Delete");
        btnDelete.setBounds(150, 270, 80, 30);
        add(btnDelete);
        
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(240, 270, 80, 30);
        add(btnCancel);
        
        // Adding action listeners
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDepartment();
            }
        });
        
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteDepartment();
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void loadDepartmentData(int id) {
        try {
            String query = "SELECT * FROM DEPARTMENTS WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                txtId.setText(String.valueOf(rs.getInt("id")));
                txtSchId.setText(String.valueOf(rs.getInt("sch_id")));
                txtCode.setText(rs.getString("dept_code"));
                txtName.setText(rs.getString("dept_name"));
                txtLocation.setText(rs.getString("dept_location"));
                txtEmail.setText(rs.getString("dept_email"));
            } else {
                JOptionPane.showMessageDialog(this, "Department not found!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading department: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveDepartment() {
        // Validate inputs
        if (txtId.getText().trim().isEmpty() || txtSchId.getText().trim().isEmpty() ||
            txtCode.getText().trim().isEmpty() || txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID, School ID, Code and Name are required fields",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            int schId = Integer.parseInt(txtSchId.getText().trim());
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();
            String location = txtLocation.getText().trim();
            String email = txtEmail.getText().trim();
            
            // Check if ID exists (for new departments)
            if (!isEditMode) {
                String checkQuery = "SELECT id FROM DEPARTMENTS WHERE id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setInt(1, id);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Department ID already exists!",
                            "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Prepare SQL statement
            String query;
            PreparedStatement pstmt;
            
            if (isEditMode) {
                query = "UPDATE DEPARTMENTS SET sch_id=?, dept_code=?, dept_name=?, dept_location=?, dept_email=? WHERE id=?";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, schId);
                pstmt.setString(2, code);
                pstmt.setString(3, name);
                pstmt.setString(4, location);
                pstmt.setString(5, email);
                pstmt.setInt(6, id);
            } else {
                query = "INSERT INTO DEPARTMENTS (id, sch_id, dept_code, dept_name, dept_location, dept_email) VALUES (?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, id);
                pstmt.setInt(2, schId);
                pstmt.setString(3, code);
                pstmt.setString(4, name);
                pstmt.setString(5, location);
                pstmt.setString(6, email);
            }
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    isEditMode ? "Department updated successfully!" : "Department added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                parentView.loadDepartmentData(); // Refresh the parent view
                dispose(); // Close this form
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save department!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID and School ID must be numbers!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteDepartment() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this department?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                
                String query = "DELETE FROM DEPARTMENTS WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, id);
                
                int result = pstmt.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Department deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    parentView.loadDepartments(); // Refresh the parent view
                    dispose(); // Close this form
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete department!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}