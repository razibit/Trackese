# University Student Attendance Tracking System

A Java Swing application for tracking student attendance in university classrooms.

## Features

- **Student Management**: Add student IDs individually or in ranges
- **Attendance Tracking**: Mark students as present or absent with a visual sliding interface
- **Attendance History**: View and edit past attendance records
- **Batch & Section Support**: Manage multiple batches and sections with separate attendance records
- **CSV Storage**: All attendance data is stored in CSV files for easy access and backup

## Screenshots

(Screenshots will be available once the application is running)

## Requirements

- Java 8 or higher
- A graphical environment that supports Java Swing

## Getting Started

### Compilation

To compile the application, run the following command from the project root:

```bash
javac -d bin -cp src src/main/java/com/trackese/ui/MainFrame.java
```

### Running the Application

To run the application, use the following command:

```bash
java -cp bin com.trackese.ui.MainFrame
```

## Usage Guide

### 1. Managing Batches and Sections

- Use the dropdown in the top-right corner to select or add a batch & section
- Each batch & section has its own separate attendance records

### 2. Adding Student IDs

- Navigate to the "Student Management" tab
- Add individual ID ranges (e.g., 231115080 - 231115120)
- Add multiple ranges at once (e.g., 231115080-231115120, 231115122-231115139)
- Student IDs are displayed in the list on the right

### 3. Taking Attendance

- Navigate to the "Take Attendance" tab
- Select a date using the dropdown
- Each student ID will appear one at a time
- Click the green "Present" button or red "Absent" button
- The interface will automatically slide to show the next student
- The last 3 days of attendance history for each student is shown at the bottom

### 4. Viewing/Editing Attendance History

- Navigate to the "Attendance History" tab
- Select "All Dates" or a specific date from the dropdown
- View and edit attendance records in the table
- Click "Save Changes" to update the attendance data

## Project Structure

```
src/main/java/com/trackese/
├── models/
│   ├── BatchSection.java
│   └── Student.java
├── ui/
│   ├── AttendanceHistoryPanel.java
│   ├── AttendancePanel.java
│   ├── IconUtil.java
│   ├── MainFrame.java
│   └── StudentManagementPanel.java
└── utils/
    ├── BatchSectionManager.java
    ├── CSVHandler.java
    └── DateUtils.java
```

## Data Storage

- Batch sections are stored in a `batch_sections.dat` file
- Attendance records are stored in CSV files in the `attendance_data/` directory
- Each batch & section has its own CSV file (e.g., `58_C.csv`)

## License

This project is available for educational purposes. 