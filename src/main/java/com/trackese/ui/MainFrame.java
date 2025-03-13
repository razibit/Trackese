package com.trackese.ui;

import com.trackese.models.BatchSection;
import com.trackese.utils.BatchSectionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main application frame.
 */
public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JComboBox<Object> batchSectionComboBox;
    private BatchSection currentBatchSection;

    // UI panels
    private StudentManagementPanel studentManagementPanel;
    private AttendancePanel attendancePanel;
    private AttendanceHistoryPanel attendanceHistoryPanel;

    public MainFrame() {
        super("University Student Attendance Tracking System");
        
        // Initialize icons
        IconUtil.initializeIcons();
        
        initializeUI();
    }

    private void initializeUI() {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create main panel with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create top panel with navigation
        JPanel topPanel = createTopPanel();

        // Create the panels
        studentManagementPanel = new StudentManagementPanel(this);
        attendancePanel = new AttendancePanel(this);
        attendanceHistoryPanel = new AttendanceHistoryPanel(this);

        // Add panels to main panel
        mainPanel.add(studentManagementPanel, "STUDENT_MANAGEMENT");
        mainPanel.add(attendancePanel, "ATTENDANCE");
        mainPanel.add(attendanceHistoryPanel, "ATTENDANCE_HISTORY");

        // Create layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Show initial panel
        cardLayout.show(mainPanel, "STUDENT_MANAGEMENT");
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 240, 240));

        // Create left side with title
        JLabel titleLabel = new JLabel("University Student Attendance Tracking System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.WEST);

        // Create right side with combo box and buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);

        // Batch section combo box
        batchSectionComboBox = new JComboBox<>();
        updateBatchSectionComboBox();
        batchSectionComboBox.addActionListener(e -> {
            Object selected = batchSectionComboBox.getSelectedItem();
            if (selected instanceof BatchSection) {
                currentBatchSection = (BatchSection) selected;
                studentManagementPanel.updateBatchSection(currentBatchSection);
                attendancePanel.updateBatchSection(currentBatchSection);
                attendanceHistoryPanel.updateBatchSection(currentBatchSection);
            } else if (selected instanceof String && ((String) selected).equals("+ Add More")) {
                showAddBatchSectionDialog();
            }
        });
        
        controlPanel.add(new JLabel("Batch & Section:"));
        controlPanel.add(batchSectionComboBox);

        // Create navigation buttons
        JButton studentManagementButton = new JButton("Student Management");
        JButton attendanceButton = new JButton("Take Attendance");
        JButton historyButton = new JButton("Attendance History");

        // Add action listeners
        studentManagementButton.addActionListener(e -> cardLayout.show(mainPanel, "STUDENT_MANAGEMENT"));
        attendanceButton.addActionListener(e -> cardLayout.show(mainPanel, "ATTENDANCE"));
        historyButton.addActionListener(e -> cardLayout.show(mainPanel, "ATTENDANCE_HISTORY"));

        // Add buttons to panel
        controlPanel.add(studentManagementButton);
        controlPanel.add(attendanceButton);
        controlPanel.add(historyButton);

        panel.add(controlPanel, BorderLayout.EAST);

        return panel;
    }

    private void updateBatchSectionComboBox() {
        batchSectionComboBox.removeAllItems();
        
        for (BatchSection bs : BatchSectionManager.getAllBatchSections()) {
            batchSectionComboBox.addItem(bs);
        }
        
        batchSectionComboBox.addItem("+ Add More");
        
        if (batchSectionComboBox.getItemCount() > 1) {
            batchSectionComboBox.setSelectedIndex(0);
            currentBatchSection = (BatchSection) batchSectionComboBox.getItemAt(0);
        }
    }

    private void showAddBatchSectionDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Batch:"));
        JTextField batchField = new JTextField();
        panel.add(batchField);
        panel.add(new JLabel("Section:"));
        JTextField sectionField = new JTextField();
        panel.add(sectionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Batch & Section",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String batch = batchField.getText().trim();
            String section = sectionField.getText().trim();

            if (!batch.isEmpty() && !section.isEmpty()) {
                BatchSection newBS = BatchSectionManager.addBatchSection(batch, section);
                updateBatchSectionComboBox();
                batchSectionComboBox.setSelectedItem(newBS);
            } else {
                JOptionPane.showMessageDialog(this, "Batch and Section cannot be empty",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // User cancelled, reset to previously selected batch section
            batchSectionComboBox.setSelectedItem(currentBatchSection);
        }
    }

    public BatchSection getCurrentBatchSection() {
        return currentBatchSection;
    }

    /**
     * Main method to start the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Run the UI on the event dispatch thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
} 