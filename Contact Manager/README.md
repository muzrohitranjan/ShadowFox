# Simple Contact Management System

A command-line contact management system built in Java with full CRUD operations.

## Features

### Core CRUD Operations
- **Add Contact**: Create new contacts with name, phone, email
- **View Contacts**: Display all contacts sorted by name
- **Update Contact**: Modify existing contact information
- **Delete Contact**: Remove contacts with confirmation

### Advanced Features
- **Search**: Find contacts by keyword (name, phone, or email)
- **Duplicate Prevention**: Prevents duplicate phone numbers
- **Export**: Save contacts to text file
- **Input Validation**: Handles invalid inputs gracefully

## How to Run

1. Compile: `javac *.java`
2. Run: `java ContactManager`

## Technical Details

- **OOP Design**: Separate Contact class with encapsulation
- **Data Structure**: ArrayList for dynamic contact storage
- **Search Algorithm**: Linear search with keyword matching
- **Sorting**: Alphabetical sorting by name
- **File I/O**: Export functionality to save contacts

## Menu Options

1. Add Contact
2. View All Contacts
3. Update Contact
4. Delete Contact
5. Search Contacts
6. Export to File
7. Exit