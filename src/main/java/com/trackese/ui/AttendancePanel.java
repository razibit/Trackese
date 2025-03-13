package com.trackese.ui;

import com.trackese.models.BatchSection;
import com.trackese.models.Student;
import com.trackese.utils.CSVHandler;
import com.trackese.utils.DateUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Panel for taking attendance with sliding UI and history display.
 */
public class AttendancePanel extends JPanel {
    private MainFrame mainFrame;
    private BatchSection batchSection;
    
    // UI components
    private JPanel attendanceCardPanel;
    private CardLayout cardLayout;
    private JComboBox<String> dateComboBox;
    private JPanel historyPanel;
    private JLabel noStudentsLabel;
    
    // Data
    private List<Student> students;
    private int currentStudentIndex = 0;
    private String currentDate;
    private List<String> lastThreeDays;

    /**
     * Constructor for the attendance panel.
     *
     * @param mainFrame The main application frame
     */
    public AttendancePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create top panel with date selection
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Create center panel with attendance cards
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Initialize the date
        currentDate = DateUtils.getCurrentDateString();
        updateLastThreeDays();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                        "Attendance Date", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Date selection panel
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("Select Date:"));
        
        // Create date combo box with dates from the current month
        dateComboBox = new JComboBox<>();
        for (String date : DateUtils.getCurrentMonthDates()) {
            dateComboBox.addItem(DateUtils.formatDateForDisplay(date));
        }
        
        // Set current date as selected
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDateFormatted = displayFormat.format(new Date());
        dateComboBox.setSelectedItem(currentDateFormatted);
        
        dateComboBox.addActionListener(e -> {
            if (dateComboBox.getSelectedItem() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                try {
                    Date selectedDate = sdf.parse(dateComboBox.getSelectedItem().toString());
                    SimpleDateFormat storageFormat = new SimpleDateFormat("yyyy-MM-dd");
                    currentDate = storageFormat.format(selectedDate);
                    updateLastThreeDays();
                    loadStudents();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        datePanel.add(dateComboBox);
        
        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadStudents());
        datePanel.add(refreshButton);
        
        panel.add(datePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                        "Take Attendance", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Create a panel for the attendance cards with CardLayout
        cardLayout = new CardLayout();
        attendanceCardPanel = new JPanel(cardLayout);
        
        // Add a message for when there are no students
        noStudentsLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h2>No Students Available</h2>" +
                "<p>Please add students in the Student Management panel first.</p>" +
                "</div></html>", SwingConstants.CENTER);
        attendanceCardPanel.add(noStudentsLabel, "NO_STUDENTS");
        
        // Add a message for when all attendance is marked
        JLabel allMarkedLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h2>All Done!</h2>" +
                "<p>Attendance for all students has been recorded.</p>" +
                "</div></html>", SwingConstants.CENTER);
        attendanceCardPanel.add(allMarkedLabel, "ALL_MARKED");
        
        // Add the attendance card panel to the center
        panel.add(attendanceCardPanel, BorderLayout.CENTER);
        
        // Add history panel to the bottom
        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBorder(BorderFactory.createTitledBorder("Attendance History (Last 3 Days)"));
        panel.add(historyPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Create a panel for a student's attendance.
     *
     * @param student The student
     * @return The panel
     */
    private JPanel createStudentAttendancePanel(Student student) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Student ID display
        JLabel idLabel = new JLabel("Student ID: " + student.getId());
        idLabel.setFont(new Font("Arial", Font.BOLD, 24));
        idLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(idLabel, BorderLayout.NORTH);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        
        // Present button
        JButton presentButton = new JButton("Present");
        presentButton.setIcon(IconUtil.getCheckIcon());
        presentButton.setBackground(new Color(100, 200, 100));
        presentButton.setForeground(Color.WHITE);
        presentButton.setFont(new Font("Arial", Font.BOLD, 16));
        presentButton.setPreferredSize(new Dimension(150, 50));
        presentButton.addActionListener(e -> markAttendance(student, true));
        buttonPanel.add(presentButton);
        
        // Absent button
        JButton absentButton = new JButton("Absent");
        absentButton.setIcon(IconUtil.getXIcon());
        absentButton.setBackground(new Color(200, 100, 100));
        absentButton.setForeground(Color.WHITE);
        absentButton.setFont(new Font("Arial", Font.BOLD, 16));
        absentButton.setPreferredSize(new Dimension(150, 50));
        absentButton.addActionListener(e -> markAttendance(student, false));
        buttonPanel.add(absentButton);
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        // Update history panel for this student
        updateHistoryPanel(student);
        
        return panel;
    }
    
    private void updateHistoryPanel(Student student) {
        historyPanel.removeAll();
        
        if (lastThreeDays.isEmpty()) {
            historyPanel.add(new JLabel("No attendance history available"));
        } else {
            // Add a row for each of the last 3 days
            for (String date : lastThreeDays) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                
                JLabel dateLabel = new JLabel(DateUtils.formatDateForDisplay(date) + ": ");
                dateLabel.setPreferredSize(new Dimension(120, 20));
                row.add(dateLabel);
                
                Boolean isPresent = student.getAttendanceForDate(date);
                if (isPresent == null) {
                    row.add(new JLabel("Not marked"));
                } else if (isPresent) {
                    JLabel presentLabel = new JLabel("Present");
                    presentLabel.setForeground(new Color(0, 150, 0));
                    presentLabel.setFont(new Font("Arial", Font.BOLD, 12));
                    row.add(presentLabel);
                } else {
                    JLabel absentLabel = new JLabel("Absent");
                    absentLabel.setForeground(new Color(200, 0, 0));
                    absentLabel.setFont(new Font("Arial", Font.BOLD, 12));
                    row.add(absentLabel);
                }
                
                historyPanel.add(row);
            }
        }
        
        historyPanel.revalidate();
        historyPanel.repaint();
    }

    /**
     * Update the batch section and reload students.
     *
     * @param batchSection The batch section
     */
    public void updateBatchSection(BatchSection batchSection) {
        this.batchSection = batchSection;
        loadStudents();
    }

    private void updateLastThreeDays() {
        lastThreeDays = DateUtils.getLastNDays(currentDate, 3);
    }

    /**
     * Load students from the CSV file.
     */
    private void loadStudents() {
        if (batchSection == null) {
            return;
        }

        // Remove existing student panels
        for (Component comp : attendanceCardPanel.getComponents()) {
            if (!(comp == noStudentsLabel)) {
                attendanceCardPanel.remove(comp);
            }
        }
        
        // Get student data
        Map<String, Object> data = CSVHandler.loadStudentsFromCSV(batchSection);
        students = (List<Student>) data.get("students");
        
        if (students.isEmpty()) {
            // Show no students message
            cardLayout.show(attendanceCardPanel, "NO_STUDENTS");
            return;
        }
        
        // Get students who haven't had attendance marked for current date
        List<Student> unmarkedStudents = new ArrayList<>();
        for (Student student : students) {
            if (student.getAttendanceForDate(currentDate) == null) {
                unmarkedStudents.add(student);
            }
        }
        
        if (unmarkedStudents.isEmpty()) {
            // All students have attendance marked
            cardLayout.show(attendanceCardPanel, "ALL_MARKED");
            return;
        }
        
        // Set current students list to unmarked students
        students = unmarkedStudents;
        currentStudentIndex = 0;
        
        // Create attendance panels for each student
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            JPanel studentPanel = createStudentAttendancePanel(student);
            attendanceCardPanel.add(studentPanel, "STUDENT_" + i);
        }
        
        // Show the first student
        cardLayout.show(attendanceCardPanel, "STUDENT_" + currentStudentIndex);
    }

    /**
     * Mark attendance for a student and move to the next student.
     *
     * @param student   The student
     * @param isPresent Whether the student is present
     */
    private void markAttendance(Student student, boolean isPresent) {
        // Update attendance in the CSV file
        CSVHandler.updateAttendance(batchSection, currentDate, student.getId(), isPresent);
        
        // Move to the next student
        currentStudentIndex++;
        if (currentStudentIndex < students.size()) {
            cardLayout.show(attendanceCardPanel, "STUDENT_" + currentStudentIndex);
            updateHistoryPanel(students.get(currentStudentIndex));
        } else {
            // All students have been marked
            cardLayout.show(attendanceCardPanel, "ALL_MARKED");
        }
    }
} 