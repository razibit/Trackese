package com.trackese.ui;

import com.trackese.models.BatchSection;
import com.trackese.models.Student;
import com.trackese.utils.CSVHandler;
import com.trackese.utils.DateUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

/**
 * Panel for viewing and editing past attendance records.
 */
public class AttendanceHistoryPanel extends JPanel {
    private MainFrame mainFrame;
    private BatchSection batchSection;
    
    // UI components
    private JComboBox<String> dateComboBox;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    
    // Data
    private List<Student> students;
    private List<String> dates;
    private String selectedDate;

    /**
     * Constructor for the attendance history panel.
     *
     * @param mainFrame The main application frame
     */
    public AttendanceHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create top panel with filter options
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Create center panel with attendance table
        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                        "Filter Options", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Date filter panel
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("Select Date:"));
        
        // Date combo box
        dateComboBox = new JComboBox<>();
        dateComboBox.addItem("All Dates");
        
        // Will be populated when batch section is updated
        dateComboBox.addActionListener(e -> {
            Object selected = dateComboBox.getSelectedItem();
            if (selected != null) {
                if ("All Dates".equals(selected)) {
                    selectedDate = null;
                } else {
                    // Convert display format to storage format
                    try {
                        java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("MMM dd, yyyy");
                        java.util.Date date = displayFormat.parse(selected.toString());
                        java.text.SimpleDateFormat storageFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        selectedDate = storageFormat.format(date);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        selectedDate = null;
                    }
                }
                updateAttendanceTable();
            }
        });
        
        datePanel.add(dateComboBox);
        
        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadData());
        datePanel.add(refreshButton);
        
        panel.add(datePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                        "Attendance Records", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Create table model with editable cells
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0; // Only date columns are editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? String.class : String.class;
            }
        };
        
        // Create table
        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(30);
        attendanceTable.setShowGrid(true);
        attendanceTable.setGridColor(Color.LIGHT_GRAY);
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Custom cell renderer to color cells based on attendance
        attendanceTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 0 || value == null) {
                    c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                } else {
                    if ("Present".equals(value)) {
                        c.setBackground(isSelected ? new Color(100, 200, 100) : new Color(220, 255, 220));
                        c.setForeground(new Color(0, 100, 0));
                    } else if ("Absent".equals(value)) {
                        c.setBackground(isSelected ? new Color(200, 100, 100) : new Color(255, 220, 220));
                        c.setForeground(new Color(100, 0, 0));
                    } else {
                        c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                        c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                    }
                }
                
                return c;
            }
        });
        
        // Add editor for attendance cells
        attendanceTable.setDefaultEditor(String.class, new DefaultCellEditor(createAttendanceComboBox()));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add save button
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveChanges());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JComboBox<String> createAttendanceComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("Present");
        comboBox.addItem("Absent");
        comboBox.addItem(""); // For not marked
        return comboBox;
    }

    /**
     * Update the batch section and reload data.
     *
     * @param batchSection The batch section
     */
    public void updateBatchSection(BatchSection batchSection) {
        this.batchSection = batchSection;
        loadData();
    }

    /**
     * Load data from the CSV file.
     */
    private void loadData() {
        if (batchSection == null) {
            return;
        }

        // Load data from CSV
        Map<String, Object> data = CSVHandler.loadStudentsFromCSV(batchSection);
        students = (List<Student>) data.get("students");
        dates = (List<String>) data.get("dates");
        
        // Update date combo box
        updateDateComboBox();
        
        // Update table with data
        updateAttendanceTable();
    }

    private void updateDateComboBox() {
        dateComboBox.removeAllItems();
        dateComboBox.addItem("All Dates");
        
        for (String date : dates) {
            dateComboBox.addItem(DateUtils.formatDateForDisplay(date));
        }
        
        selectedDate = null;
    }

    private void updateAttendanceTable() {
        // Clear the table
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        
        // Add the student ID column
        tableModel.addColumn("Student ID");
        
        if (selectedDate == null) {
            // Add all date columns
            for (String date : dates) {
                tableModel.addColumn(DateUtils.formatDateForDisplay(date));
            }
            
            // Add rows for each student
            for (Student student : students) {
                Object[] rowData = new Object[dates.size() + 1];
                rowData[0] = student.getId();
                
                // Add attendance data for each date
                for (int i = 0; i < dates.size(); i++) {
                    String date = dates.get(i);
                    Boolean isPresent = student.getAttendanceForDate(date);
                    rowData[i + 1] = (isPresent == null) ? "" : (isPresent ? "Present" : "Absent");
                }
                
                tableModel.addRow(rowData);
            }
        } else {
            // Add only the selected date column
            tableModel.addColumn(DateUtils.formatDateForDisplay(selectedDate));
            
            // Add rows for each student
            for (Student student : students) {
                Object[] rowData = new Object[2];
                rowData[0] = student.getId();
                
                // Add attendance data for the selected date
                Boolean isPresent = student.getAttendanceForDate(selectedDate);
                rowData[1] = (isPresent == null) ? "" : (isPresent ? "Present" : "Absent");
                
                tableModel.addRow(rowData);
            }
        }
    }

    private void saveChanges() {
        if (batchSection == null || students.isEmpty()) {
            return;
        }
        
        boolean changesFound = false;
        
        // Iterate through table cells and update student attendance
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String studentId = (String) tableModel.getValueAt(row, 0);
            Student student = findStudentById(studentId);
            
            if (student != null) {
                if (selectedDate == null) {
                    // Update all dates
                    for (int col = 1; col < tableModel.getColumnCount(); col++) {
                        String date = dates.get(col - 1);
                        String attendanceValue = (String) tableModel.getValueAt(row, col);
                        
                        if (attendanceValue != null && !attendanceValue.isEmpty()) {
                            boolean isPresent = "Present".equals(attendanceValue);
                            Boolean currentValue = student.getAttendanceForDate(date);
                            
                            // Update only if changed
                            if (currentValue == null || currentValue != isPresent) {
                                student.addAttendanceRecord(date, isPresent);
                                changesFound = true;
                            }
                        }
                    }
                } else {
                    // Update only selected date
                    String attendanceValue = (String) tableModel.getValueAt(row, 1);
                    
                    if (attendanceValue != null && !attendanceValue.isEmpty()) {
                        boolean isPresent = "Present".equals(attendanceValue);
                        Boolean currentValue = student.getAttendanceForDate(selectedDate);
                        
                        // Update only if changed
                        if (currentValue == null || currentValue != isPresent) {
                            student.addAttendanceRecord(selectedDate, isPresent);
                            changesFound = true;
                        }
                    }
                }
            }
        }
        
        if (changesFound) {
            // Save changes to the CSV file
            CSVHandler.saveStudentsToCSV(batchSection, students, dates);
            JOptionPane.showMessageDialog(this, "Changes saved successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No changes were made.",
                    "No Changes", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private Student findStudentById(String id) {
        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student;
            }
        }
        return null;
    }
} 