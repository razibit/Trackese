package com.trackese.utils;

import com.trackese.models.Student;
import com.trackese.models.BatchSection;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility class for handling CSV operations.
 */
public class CSVHandler {
    private static final String CSV_DIRECTORY = "attendance_data";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    // Ensure the directory exists
    static {
        try {
            Files.createDirectories(Paths.get(CSV_DIRECTORY));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save students to a CSV file.
     *
     * @param batchSection The batch and section
     * @param students     List of students
     * @param dates        List of dates for attendance
     */
    public static void saveStudentsToCSV(BatchSection batchSection, List<Student> students, List<String> dates) {
        String filePath = CSV_DIRECTORY + File.separator + batchSection.getFileName();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header row with dates
            StringBuilder header = new StringBuilder("Student ID");
            for (String date : dates) {
                header.append(",").append(date);
            }
            writer.println(header.toString());

            // Write student data
            for (Student student : students) {
                StringBuilder line = new StringBuilder(student.getId());
                for (String date : dates) {
                    Boolean isPresent = student.getAttendanceForDate(date);
                    String attendanceValue = (isPresent == null) ? "" : (isPresent ? "Present" : "Absent");
                    line.append(",").append(attendanceValue);
                }
                writer.println(line.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load students from a CSV file.
     *
     * @param batchSection The batch and section
     * @return Map containing students and dates
     */
    public static Map<String, Object> loadStudentsFromCSV(BatchSection batchSection) {
        String filePath = CSV_DIRECTORY + File.separator + batchSection.getFileName();
        List<Student> students = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return Map.of("students", students, "dates", dates);
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) {
                    // Parse header row to get dates
                    String[] headers = line.split(",");
                    for (int i = 1; i < headers.length; i++) { // Skip the first column (Student ID)
                        dates.add(headers[i]);
                    }
                }

                // Read student data
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length > 0) {
                        Student student = new Student(data[0]);
                        
                        // Parse attendance data
                        for (int i = 1; i < data.length && i - 1 < dates.size(); i++) {
                            String date = dates.get(i - 1);
                            if (!data[i].isEmpty()) {
                                boolean isPresent = "Present".equalsIgnoreCase(data[i]);
                                student.addAttendanceRecord(date, isPresent);
                            }
                        }
                        
                        students.add(student);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return Map.of("students", students, "dates", dates);
    }

    /**
     * Add or update attendance for a specific date.
     *
     * @param batchSection The batch and section
     * @param date         The date for attendance
     * @param studentId    The student ID
     * @param isPresent    Whether the student is present
     */
    public static void updateAttendance(BatchSection batchSection, String date, String studentId, boolean isPresent) {
        Map<String, Object> data = loadStudentsFromCSV(batchSection);
        List<Student> students = (List<Student>) data.get("students");
        List<String> dates = (List<String>) data.get("dates");

        // Add date if it doesn't exist
        if (!dates.contains(date)) {
            dates.add(date);
            // Sort dates chronologically
            Collections.sort(dates);
        }

        // Find student or create if not exists
        Student targetStudent = null;
        for (Student student : students) {
            if (student.getId().equals(studentId)) {
                targetStudent = student;
                break;
            }
        }

        if (targetStudent == null) {
            targetStudent = new Student(studentId);
            students.add(targetStudent);
        }

        // Update attendance
        targetStudent.addAttendanceRecord(date, isPresent);

        // Save updated data
        saveStudentsToCSV(batchSection, students, dates);
    }

    /**
     * Get the current date string in the specified format.
     *
     * @return Current date string
     */
    public static String getCurrentDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(new Date());
    }

    /**
     * Add a batch of student IDs.
     *
     * @param batchSection The batch and section
     * @param startId      Starting student ID
     * @param endId        Ending student ID
     */
    public static void addStudentBatch(BatchSection batchSection, String startId, String endId) {
        try {
            int start = Integer.parseInt(startId);
            int end = Integer.parseInt(endId);
            
            Map<String, Object> data = loadStudentsFromCSV(batchSection);
            List<Student> students = (List<Student>) data.get("students");
            List<String> dates = (List<String>) data.get("dates");
            
            // Create set of existing IDs for fast lookup
            Set<String> existingIds = new HashSet<>();
            for (Student student : students) {
                existingIds.add(student.getId());
            }
            
            // Add new students
            for (int i = start; i <= end; i++) {
                String id = String.valueOf(i);
                if (!existingIds.contains(id)) {
                    students.add(new Student(id));
                }
            }
            
            // Save updated data
            saveStudentsToCSV(batchSection, students, dates);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
} 