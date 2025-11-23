# Library Management System with SQLite

A comprehensive library management system built with Java and SQLite, featuring user authentication, book management, loan tracking, and intelligent book recommendations.

## 🚀 Features

### Core Functionality
- ✅ **User Authentication**: Secure login system with role-based access
- ✅ **Book Management**: Complete CRUD operations for books
- ✅ **Loan Management**: Issue, return, and renew books with due date tracking
- ✅ **Fine Calculation**: Automatic fine calculation for overdue books
- ✅ **Search & Filter**: Advanced search by title, author, ISBN, category

### Web Frontend Features
- 🌐 **Modern Web Interface**: Responsive design with professional UI
- 📊 **Interactive Dashboard**: Real-time statistics and user activity
- 🔐 **User Registration**: Self-service account creation
- 📱 **Mobile Responsive**: Works seamlessly on all devices
- ⭐ **Rating System**: Rate and review books
- 🎯 **Smart Recommendations**: Personalized book suggestions

### Advanced Features
- ✅ **Book Recommendations**: AI-powered recommendations based on reading history
- ✅ **User Preferences**: Customizable category and author preferences
- ✅ **Book Ratings**: User rating and review system
- ✅ **Overdue Tracking**: Automatic overdue detection and fine calculation
- ✅ **Admin Dashboard**: Administrative tools for managing users and books
- ✅ **Data Persistence**: SQLite database with JDBC integration

### User Management
- 🔐 **Role-based Access**: Admin and regular user roles
- 👤 **User Profiles**: Complete user information management
- 📊 **Activity Tracking**: Track user borrowing history
- 🔍 **User Search**: Find users by name, username, or email

### Loan System
- 📅 **Issue/Return Dates**: Automatic date tracking
- ⏰ **Due Date Management**: Configurable loan periods
- 💰 **Fine System**: $1/day overdue fine calculation
- 🔄 **Loan Renewal**: Extend loan periods
- 📈 **Loan History**: Complete borrowing history

## 🏗️ Project Structure

```
LibraryManagementSystem/
├── src/
│   ├── main/java/
│   │   ├── DatabaseManager.java          # SQLite database management
│   │   ├── User.java                     # User model
│   │   ├── Book.java                     # Book model
│   │   ├── Loan.java                     # Loan model
│   │   ├── UserService.java              # User operations
│   │   ├── BookService.java              # Book operations
│   │   ├── LoanService.java              # Loan operations
│   │   ├── RecommendationService.java    # Book recommendations
│   │   └── LibraryManagementApp.java     # Main application
│   └── test/java/
│       ├── UserServiceTest.java          # User service tests (25+ cases)
│       └── BookServiceTest.java          # Book service tests (25+ cases)
├── database/                             # SQLite database files
├── pom.xml                              # Maven configuration
├── run-app.bat                          # Application launcher
└── README.md                            # This file
```

## 🛠️ Technologies Used

- **Java 11+**: Core programming language
- **SQLite**: Lightweight database for data persistence
- **JDBC**: Database connectivity
- **Maven**: Build and dependency management
- **JUnit 5**: Unit testing framework

## 📊 Database Schema

### Users Table
- `id` (Primary Key)
- `username` (Unique)
- `password`
- `email` (Unique)
- `full_name`
- `role` (USER/ADMIN)
- `created_at`
- `is_active`

### Books Table
- `id` (Primary Key)
- `isbn` (Unique)
- `title`
- `author`
- `publisher`
- `publication_year`
- `category`
- `total_copies`
- `available_copies`
- `description`

### Loans Table
- `id` (Primary Key)
- `user_id` (Foreign Key)
- `book_id` (Foreign Key)
- `issue_date`
- `due_date`
- `return_date`
- `status`
- `fine_amount`

### Additional Tables
- `user_preferences`: User reading preferences
- `book_ratings`: User ratings and reviews

## 🚀 How to Run

### Option 1: Using Maven (Recommended)
```bash
# Clone or download the project
cd LibraryManagementSystem

# Run the application
mvn clean compile exec:java
```

### Option 2: Using Batch File
```bash
# Double-click run-app.bat or run in command prompt
run-app.bat
```

### Option 3: Manual Setup
```bash
# Create lib directory and download SQLite JDBC driver
mkdir lib
# Download sqlite-jdbc-3.42.0.0.jar to lib/

# Compile
javac -cp "lib/*" -d target/classes src/main/java/*.java

# Run
java -cp "target/classes;lib/*" LibraryManagementApp
```

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest
mvn test -Dtest=BookServiceTest
```

## 📋 Test Coverage (50+ Test Cases)

### UserServiceTest.java (25+ test cases)
- **Authentication**: Valid/invalid credentials, inactive users
- **Registration**: User creation, duplicate validation
- **User Management**: CRUD operations, role management
- **Search & Validation**: User search, username/email validation
- **Password Management**: Password changes, security

### BookServiceTest.java (25+ test cases)
- **Book Management**: Add, update, delete books
- **Search Operations**: Title, author, ISBN, category search
- **Inventory Management**: Copy tracking, availability
- **Data Validation**: ISBN uniqueness, required fields
- **Category Management**: Category listing, filtering

## 🎯 Key Learning Outcomes

1. **SQLite Integration**: Database design, JDBC operations, SQL queries
2. **Data Persistence**: File-based database, transaction management
3. **User Authentication**: Login systems, role-based access control
4. **Business Logic**: Library operations, fine calculations, recommendations
5. **Testing**: Comprehensive unit testing with JUnit 5
6. **Software Architecture**: Service layer pattern, separation of concerns
7. **Database Design**: Relational modeling, foreign keys, constraints

## 👤 Default Users

The system comes with pre-configured users:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| admin | admin123 | ADMIN | System administrator |
| john_doe | password123 | USER | Regular user |
| jane_smith | password123 | USER | Regular user |
| bob_wilson | password123 | USER | Regular user |

## 📚 Sample Books

Pre-loaded with programming and computer science books:
- Effective Java by Joshua Bloch
- Clean Code by Robert C. Martin
- Head First Design Patterns by Eric Freeman
- Learning SQL by Alan Beaulieu
- Algorithms by Robert Sedgewick
- And more...

## 🔧 System Requirements

- **Java 11 or higher**
- **Maven 3.6+** (optional, for build management)
- **SQLite JDBC Driver** (included in dependencies)
- **Windows/Linux/macOS** (cross-platform)

## 🎨 Application Features

### User Interface
- **Console-based Menu System**: Intuitive navigation
- **Role-based Menus**: Different options for admin/users
- **Input Validation**: Robust error handling
- **Formatted Output**: Clean data presentation

### Admin Features
- **User Management**: View, search, update users
- **Book Management**: Add, edit, delete books
- **Loan Management**: View all loans, overdue tracking
- **Reports**: System statistics, popular books

### User Features
- **Book Browsing**: View available books
- **Search System**: Find books by multiple criteria
- **Loan Management**: View personal loans, renewals
- **Recommendations**: Personalized book suggestions
- **Rating System**: Rate and review books

## 🚀 Advanced Features Implemented

### Recommendation Engine
- **Collaborative Filtering**: Based on user reading patterns
- **Content-based Filtering**: Category and author preferences
- **Popularity-based**: Trending and highly-rated books
- **Hybrid Approach**: Combines multiple recommendation strategies

### Fine Management
- **Automatic Calculation**: $1 per day overdue
- **Grace Period**: Configurable fine policies
- **Payment Tracking**: Fine amount recording
- **Overdue Alerts**: System notifications

### Data Analytics
- **Reading Patterns**: User behavior analysis
- **Popular Books**: Most borrowed titles
- **Category Trends**: Popular genres
- **User Statistics**: Borrowing frequency

## 🔮 Future Enhancements

- [ ] **Web Interface**: Modern web-based UI
- [ ] **REST API**: RESTful services for mobile apps
- [ ] **Email Notifications**: Overdue reminders, new book alerts
- [ ] **Barcode Integration**: Barcode scanning for books
- [ ] **Advanced Search**: Full-text search, filters
- [ ] **Book Reservations**: Queue system for popular books
- [ ] **Digital Books**: E-book management
- [ ] **Multi-library Support**: Branch management
- [ ] **Advanced Analytics**: Detailed reporting dashboard
- [ ] **Integration APIs**: Google Books API, ISBN lookup

This library management system demonstrates professional-level database integration, comprehensive business logic, and robust testing practices, making it an excellent example of enterprise Java application development with SQLite persistence!