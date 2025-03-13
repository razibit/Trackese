package com.trackese.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.trackese.models.Student;

/**
 * Model class representing a batch and section combination.
 */
public class BatchSection implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String batchName;
    private String section;
    private transient List<Student> students;

    public BatchSection(String batchName, String section) {
        this.batchName = batchName;
        this.section = section;
        this.students = new ArrayList<>();
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public List<Student> getStudents() {
        if (students == null) {
            students = new ArrayList<>();
        }
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void addStudent(Student student) {
        if (students == null) {
            students = new ArrayList<>();
        }
        this.students.add(student);
    }
    
    public String getFullName() {
        return batchName + " " + section;
    }
    
    public String getFileName() {
        // Create a valid filename from the batch and section
        return batchName.replaceAll("\\s+", "_") + "_" + 
               section.replaceAll("\\s+", "_") + ".csv";
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BatchSection that = (BatchSection) obj;
        return batchName.equals(that.batchName) && section.equals(that.section);
    }
    
    @Override
    public int hashCode() {
        return 31 * batchName.hashCode() + section.hashCode();
    }
} 