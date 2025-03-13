package com.trackese.ui;

import com.trackese.models.BatchSection;
import com.trackese.models.Student;
import com.trackese.utils.CSVHandler;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Panel for adding and managing students.
 */
public class StudentManagementPanel extends JPanel {
    private MainFrame mainFrame;
    private BatchSection batchSection;
    
    // UI components
    private JTextField startIdField;
    private JTextField endIdField;
    private JTextArea studentIdsTextArea;
    private JButton addButton;
    private JList<String> studentIdsList;
    private DefaultListModel<String> listModel;

    /**
     * Constructor for the student management panel.
     *
     * @param mainFrame The main application frame
     */
    public StudentManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create north panel for adding students
        JPanel northPanel = createAddStudentsPanel();
        add(northPanel, BorderLayout.NORTH);

        // Create center panel to display student IDs
        JPanel centerPanel = createStudentListPanel();
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createAddStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Add Students", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)));

        // Create top part with single ID range
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("ID Range:"));
        
        startIdField = new JTextField(10);
        endIdField = new JTextField(10);
        
        topPanel.add(startIdField);
        topPanel.add(new JLabel(" - "));
        topPanel.add(endIdField);
        
        JButton addRangeButton = new JButton("Add Range");
        addRangeButton.addActionListener(e -> addStudentRange());
        topPanel.add(addRangeButton);

        // Create center part with text area for batch input
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        centerPanel.add(new JLabel("<html>Enter multiple ID ranges (e.g., \"231115080-231115120, 231115122-231115139\")<br>or individual IDs separated by commas:</html>"), 
                BorderLayout.NORTH);
        
        studentIdsTextArea = new JTextArea(5, 40);
        studentIdsTextArea.setLineWrap(true);
        studentIdsTextArea.setWrapStyleWord(true);
        centerPanel.add(new JScrollPane(studentIdsTextArea), BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add Students");
        addButton.addActionListener(e -> addStudentBatch());
        buttonPanel.add(addButton);

        // Add components to panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStudentListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Student IDs", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)));

        // Create list model and list
        listModel = new DefaultListModel<>();
        studentIdsList = new JList<>(listModel);
        studentIdsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(studentIdsList);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(e -> loadStudentList());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Update the batch section and refresh the student list.
     *
     * @param batchSection The batch section
     */
    public void updateBatchSection(BatchSection batchSection) {
        this.batchSection = batchSection;
        loadStudentList();
    }

    private void loadStudentList() {
        if (batchSection == null) {
            return;
        }

        // Clear the list model
        listModel.clear();

        // Load students from CSV
        Map<String, Object> data = CSVHandler.loadStudentsFromCSV(batchSection);
        List<Student> students = (List<Student>) data.get("students");

        // Add student IDs to the list model
        for (Student student : students) {
            listModel.addElement(student.getId());
        }
    }

    private void addStudentRange() {
        if (batchSection == null) {
            JOptionPane.showMessageDialog(this, "Please select a batch and section first.",
                    "No Batch Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String startId = startIdField.getText().trim();
        String endId = endIdField.getText().trim();

        if (startId.isEmpty() || endId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both start and end IDs.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int start = Integer.parseInt(startId);
            int end = Integer.parseInt(endId);

            if (start > end) {
                JOptionPane.showMessageDialog(this, "Start ID must be less than or equal to End ID.",
                        "Invalid Range", JOptionPane.WARNING_MESSAGE);
                return;
            }

            CSVHandler.addStudentBatch(batchSection, startId, endId);
            JOptionPane.showMessageDialog(this, "Student IDs added successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadStudentList();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "IDs must be numeric values.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addStudentBatch() {
        if (batchSection == null) {
            JOptionPane.showMessageDialog(this, "Please select a batch and section first.",
                    "No Batch Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String text = studentIdsTextArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter student IDs or ranges.",
                    "Empty Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Split by commas and process each part
            String[] parts = text.split(",");
            for (String part : parts) {
                part = part.trim();
                
                // Check if it's a range (contains a hyphen)
                if (part.contains("-")) {
                    String[] range = part.split("-");
                    if (range.length == 2) {
                        String startId = range[0].trim();
                        String endId = range[1].trim();
                        CSVHandler.addStudentBatch(batchSection, startId, endId);
                    }
                } else if (!part.isEmpty()) {
                    // It's a single ID
                    CSVHandler.addStudentBatch(batchSection, part, part);
                }
            }
            
            JOptionPane.showMessageDialog(this, "Student IDs added successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadStudentList();
            studentIdsTextArea.setText("");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding student IDs: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 