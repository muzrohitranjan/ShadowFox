# Student Information System with GUI

A comprehensive Java Swing-based GUI application for managing student records with advanced features.

## Features

### Core CRUD Operations
- **Add Student**: Create new student records with validation
- **View Students**: Display all students in a sortable table
- **Update Student**: Modify existing student information
- **Delete Student**: Remove students with confirmation dialog

### Student Information Fields
- Student ID (Auto-generated)
- Full Name
- Email Address
- Course/Program
- Grade (0-100 with letter grade conversion)
- Phone Number

### Advanced Features
- **Grade Calculator**: Weighted grade calculation tool
  - Assignments, Exams, Participation weights
  - Automatic letter grade assignment
  - Color-coded results
- **Statistics Dashboard**: Real-time stats display
  - Total student count
  - Average grade calculation
- **Interactive Table**: Click to select and edit students
- **Input Validation**: Comprehensive error checking
- **Professional UI**: Modern Swing interface

## Grade System
- A: 90-100
- B: 80-89
- C: 70-79
- D: 60-69
- F: Below 60

## How to Run

1. Compile: `javac *.java`
2. Run: `java StudentInfoGUI`

## Technical Details

- **Framework**: Java Swing
- **Architecture**: MVC pattern with separate Student model
- **Data Storage**: In-memory ArrayList
- **GUI Components**: JTable, JTextField, JButton, JDialog
- **Event Handling**: ActionListener implementations
- **Layout Managers**: BorderLayout, GridBagLayout, FlowLayout

## Key Learning Outcomes

- GUI development with Java Swing
- Event-driven programming
- Layout management
- Table model implementation
- Dialog creation and management
- Input validation and error handling
- Object-oriented design principles