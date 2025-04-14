import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
//import java.awt.event.*;
import java.sql.*;

/**
 * Window for displaying list of departments with search functionality
 */
public class DepartmentListWindow extends JFrame {
    private JTable departmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private int highlightedId = -1; // Track the highlighted ID
    
    /**
     * Constructor for department list window
     */
    public DepartmentListWindow() {
        setTitle("Department Management System - Departments");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search panel at the top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Department:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);
        
        JButton refreshButton = new JButton("Refresh");
        searchPanel.add(refreshButton);
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Table for departments
        String[] columnNames = {"ID", "School ID", "Dept Code", "Department Name", "Location", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        departmentTable = new JTable(tableModel);
        departmentTable.getTableHeader().setReorderingAllowed(false);
        departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departmentTable.setRowHeight(25);
        
        // Custom cell renderer for highlighting - UPDATED to make text visible on yellow background
        departmentTable.setDefaultRenderer(Object.class, new TableCellRenderer() {
            private final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = DEFAULT_RENDERER.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    
                    // If cell is part of the search result
                    if (table.getValueAt(row, 0) != null && 
                        highlightedId != -1 && 
                        highlightedId == Integer.parseInt(table.getValueAt(row, 0).toString())) {
                        label.setBackground(Color.YELLOW);
                        label.setForeground(Color.BLACK); // Ensure text is visible on yellow background
                        label.setOpaque(true);
                    } else if (isSelected) {
                        label.setBackground(table.getSelectionBackground());
                        label.setForeground(table.getSelectionForeground());
                        label.setOpaque(true);
                    } else {
                        label.setBackground(table.getBackground());
                        label.setForeground(table.getForeground());
                        label.setOpaque(false);
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(departmentTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton insertButton = new JButton("Manage Departments");
        buttonPanel.add(insertButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadDepartmentData();
        
        // Add action listeners
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty()) {
                try {
                    int id = Integer.parseInt(searchText);
                    searchDepartmentById(id);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a valid department ID (numeric)", 
                        "Invalid Search", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        refreshButton.addActionListener(e -> {
            loadDepartmentData();
            searchField.setText("");
            highlightedId = -1; // Clear highlighting
            departmentTable.repaint();
        });
        
        insertButton.addActionListener(e -> {
            new DepartmentManageWindow(this);
        });
        
        add(mainPanel);
        setVisible(true);
    }
    
    /**
     * Load all departments from database
     */
    public void loadDepartmentData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try (ResultSet rs = DatabaseConnector.getAllDepartments()) {
            while (rs.next()) {
                Object[] rowData = {
                    rs.getInt("id"),
                    rs.getInt("sch_id"),
                    rs.getInt("dept_code"),
                    rs.getString("dept_name"),
                    rs.getString("dept_location"),
                    rs.getString("dept_email")
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading departments: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Search department by ID
     * @param id The department ID to search for
     */
    private void searchDepartmentById(int id) {
        boolean found = false;
        highlightedId = id; // Set the ID to highlight
        
        // First try to find in the current table model
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0) != null && 
                Integer.parseInt(tableModel.getValueAt(i, 0).toString()) == id) {
                // Select the row
                departmentTable.setRowSelectionInterval(i, i);
                // Ensure the row is visible
                departmentTable.scrollRectToVisible(departmentTable.getCellRect(i, 0, true));
                found = true;
                break;
            }
        }
        
        // If not found in the table, try to load from database
        if (!found) {
            try (ResultSet rs = DatabaseConnector.getDepartmentById(id)) {
                if (rs.next()) {
                    // Clear table and add the found department
                    tableModel.setRowCount(0);
                    Object[] rowData = {
                        rs.getInt("id"),
                        rs.getInt("sch_id"),
                        rs.getInt("dept_code"),
                        rs.getString("dept_name"),
                        rs.getString("dept_location"),
                        rs.getString("dept_email")
                    };
                    tableModel.addRow(rowData);
                    departmentTable.setRowSelectionInterval(0, 0);
                    found = true;
                    
                    // Show found message
                    JOptionPane.showMessageDialog(this, 
                        "Department with ID " + id + " found!", 
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(this, 
                "Department with ID " + id + " not found.", 
                "Search Result", JOptionPane.INFORMATION_MESSAGE);
            highlightedId = -1; // Clear highlighting if not found
        } else {
            // Repaint to ensure highlighting
            departmentTable.repaint();
            
            // If found in the model, show dialog
            if (departmentTable.getSelectedRow() != -1) {
                JOptionPane.showMessageDialog(this, 
                    "Department with ID " + id + " found!", 
                    "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
