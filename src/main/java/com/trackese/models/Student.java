package com.trackese.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing a student.
 */
public class Student {
    private String id;
    private Map<String, Boolean> attendanceRecords; // Date -> Present/Absent

    public Student(String id) {
        this.id = id;
        this.attendanceRecords = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Boolean> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void setAttendanceRecords(Map<String, Boolean> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
    }

    public void addAttendanceRecord(String date, boolean isPresent) {
        attendanceRecords.put(date, isPresent);
    }

    public Boolean getAttendanceForDate(String date) {
        return attendanceRecords.getOrDefault(date, null);
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                '}';
    }
} 