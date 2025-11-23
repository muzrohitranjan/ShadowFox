import java.sql.*;
import java.time.LocalDateTime;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:database/library.db";
    private Connection connection;

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            insertSampleData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String[] createTableQueries = {
            // Users table
            """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                full_name TEXT NOT NULL,
                role TEXT DEFAULT 'USER',
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT 1
            )
            """,
            
            // Books table
            """
            CREATE TABLE IF NOT EXISTS books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                isbn TEXT UNIQUE,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                publisher TEXT,
                publication_year INTEGER,
                category TEXT,
                total_copies INTEGER DEFAULT 1,
                available_copies INTEGER DEFAULT 1,
                description TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
            """,
            
            // Loans table
            """
            CREATE TABLE IF NOT EXISTS loans (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                book_id INTEGER NOT NULL,
                issue_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                due_date DATETIME NOT NULL,
                return_date DATETIME,
                status TEXT DEFAULT 'ISSUED',
                fine_amount DECIMAL(10,2) DEFAULT 0.00,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (book_id) REFERENCES books(id)
            )
            """,
            
            // User preferences for recommendations
            """
            CREATE TABLE IF NOT EXISTS user_preferences (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                preferred_categories TEXT,
                preferred_authors TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
            """,
            
            // Book ratings for recommendations
            """
            CREATE TABLE IF NOT EXISTS book_ratings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                book_id INTEGER NOT NULL,
                rating INTEGER CHECK(rating >= 1 AND rating <= 5),
                review TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (book_id) REFERENCES books(id),
                UNIQUE(user_id, book_id)
            )
            """
        };

        for (String query : createTableQueries) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(query);
            }
        }
    }

    private void insertSampleData() throws SQLException {
        // Check if data already exists
        String checkQuery = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkQuery)) {
            if (rs.getInt(1) > 0) return; // Data already exists
        }

        // Insert sample users
        String[] userInserts = {
            "INSERT INTO users (username, password, email, full_name, role) VALUES ('admin', 'admin123', 'admin@library.com', 'Library Administrator', 'ADMIN')",
            "INSERT INTO users (username, password, email, full_name) VALUES ('john_doe', 'password123', 'john@email.com', 'John Doe')",
            "INSERT INTO users (username, password, email, full_name) VALUES ('jane_smith', 'password123', 'jane@email.com', 'Jane Smith')",
            "INSERT INTO users (username, password, email, full_name) VALUES ('bob_wilson', 'password123', 'bob@email.com', 'Bob Wilson')"
        };

        // Insert sample books
        String[] bookInserts = {
            "INSERT INTO books (isbn, title, author, publisher, publication_year, category, total_copies, available_copies, description) VALUES ('978-0134685991', 'Effective Java', 'Joshua Bloch', 'Addison-Wesley', 2017, 'Programming', 3, 3, 'Best practices for Java programming')",
            "INSERT INTO books (isbn, title, author, publisher, publication_year, category, total_copies, available_copies, description) VALUES ('978-0596009205', 'Head First Design Patterns', 'Eric Freeman', 'O''Reilly Media', 2004, 'Programming', 2, 2, 'Design patterns in object-oriented programming')",
            "INSERT INTO books (isbn, title, author, publisher, publication_year, category, total_copies, available_copies, description) VALUES ('978-0132350884', 'Clean Code', 'Robert C. Martin', 'Prentice Hall', 2008, 'Programming', 4, 4, 'A handbook of agile software craftsmanship')",
            "INSERT INTO books (isbn, title, author, publisher, publication_year, category, total_copies, available_copies, description) VALUES ('978-1449331818', 'Learning SQL', 'Alan Beaulieu', 'O''Reilly Media', 2009, 'Database', 2, 2, 'Master SQL fundamentals')",
            "INSERT INTO books (isbn, title, author, publisher, publication_year, category, total_copies, available_copies, description) VALUES ('978-0321573513', 'Algorithms', 'Robert Sedgewick', 'Addison-Wesley', 2011, 'Computer Science', 3, 3, 'Essential algorithms and data structures')",
            "INSERT INTO books (isbn, title, author, publisher, publication_year, category, total_copies, available_copies, description) VALUES ('978-0262033848', 'Introduction to Algorithms', 'Thomas H. Cormen', 'MIT Press', 2009, 'Computer Science', 2, 2, 'Comprehensive algorithms textbook')",
            "INSERT INTO books (isbn, title, author, publisher, publication_year, category, total_copies, available_copies, description) VALUES ('978-0735619678', 'Code Complete', 'Steve McConnell', 'Microsoft Press', 2004, 'Programming', 2, 2, 'A practical handbook of software construction')",
            "INSERT INTO books (isbn, title, author, publisher, publication_year, category, total_copies, available_copies, description) VALUES ('978-0201616224', 'The Pragmatic Programmer', 'Andrew Hunt', 'Addison-Wesley', 1999, 'Programming', 3, 3, 'From journeyman to master')"
        };

        for (String insert : userInserts) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(insert);
            }
        }

        for (String insert : bookInserts) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(insert);
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}