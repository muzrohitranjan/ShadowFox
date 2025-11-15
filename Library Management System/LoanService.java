import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoanService {
    private DatabaseManager dbManager;
    private BookService bookService;
    private static final double FINE_PER_DAY = 1.0; // $1 per day overdue

    public LoanService(DatabaseManager dbManager, BookService bookService) {
        this.dbManager = dbManager;
        this.bookService = bookService;
    }

    public boolean issueBook(int userId, int bookId, int loanDurationDays) {
        // Check if book is available
        Book book = bookService.getBookById(bookId);
        if (book == null || !book.isAvailable()) {
            return false;
        }

        // Check if user already has this book
        if (hasActiveBookLoan(userId, bookId)) {
            return false;
        }

        Connection conn = dbManager.getConnection();
        try {
            conn.setAutoCommit(false);

            // Create loan record
            LocalDateTime dueDate = LocalDateTime.now().plusDays(loanDurationDays);
            String loanQuery = "INSERT INTO loans (user_id, book_id, due_date) VALUES (?, ?, ?)";
            
            try (PreparedStatement loanStmt = conn.prepareStatement(loanQuery)) {
                loanStmt.setInt(1, userId);
                loanStmt.setInt(2, bookId);
                loanStmt.setTimestamp(3, Timestamp.valueOf(dueDate));
                loanStmt.executeUpdate();
            }

            // Update available copies
            if (bookService.updateAvailableCopies(bookId, -1)) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean returnBook(int loanId) {
        Loan loan = getLoanById(loanId);
        if (loan == null || !"ISSUED".equals(loan.getStatus())) {
            return false;
        }

        Connection conn = dbManager.getConnection();
        try {
            conn.setAutoCommit(false);

            // Calculate fine if overdue
            double fine = calculateFine(loan);
            
            // Update loan record
            String updateQuery = "UPDATE loans SET return_date = ?, status = 'RETURNED', fine_amount = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setDouble(2, fine);
                stmt.setInt(3, loanId);
                stmt.executeUpdate();
            }

            // Update available copies
            if (bookService.updateAvailableCopies(loan.getBookId(), 1)) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean renewLoan(int loanId, int additionalDays) {
        Loan loan = getLoanById(loanId);
        if (loan == null || !"ISSUED".equals(loan.getStatus())) {
            return false;
        }

        LocalDateTime newDueDate = loan.getDueDate().plusDays(additionalDays);
        String query = "UPDATE loans SET due_date = ? WHERE id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(newDueDate));
            stmt.setInt(2, loanId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Loan getLoanById(int id) {
        String query = """
            SELECT l.*, u.full_name as user_name, b.title as book_title, b.author as book_author
            FROM loans l
            JOIN users u ON l.user_id = u.id
            JOIN books b ON l.book_id = b.id
            WHERE l.id = ?
        """;
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLoan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Loan> getUserLoans(int userId) {
        List<Loan> loans = new ArrayList<>();
        String query = """
            SELECT l.*, u.full_name as user_name, b.title as book_title, b.author as book_author
            FROM loans l
            JOIN users u ON l.user_id = u.id
            JOIN books b ON l.book_id = b.id
            WHERE l.user_id = ?
            ORDER BY l.issue_date DESC
        """;
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public List<Loan> getActiveLoans() {
        List<Loan> loans = new ArrayList<>();
        String query = """
            SELECT l.*, u.full_name as user_name, b.title as book_title, b.author as book_author
            FROM loans l
            JOIN users u ON l.user_id = u.id
            JOIN books b ON l.book_id = b.id
            WHERE l.status = 'ISSUED'
            ORDER BY l.due_date ASC
        """;
        
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public List<Loan> getOverdueLoans() {
        List<Loan> loans = new ArrayList<>();
        String query = """
            SELECT l.*, u.full_name as user_name, b.title as book_title, b.author as book_author
            FROM loans l
            JOIN users u ON l.user_id = u.id
            JOIN books b ON l.book_id = b.id
            WHERE l.status = 'ISSUED' AND l.due_date < ?
            ORDER BY l.due_date ASC
        """;
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String query = """
            SELECT l.*, u.full_name as user_name, b.title as book_title, b.author as book_author
            FROM loans l
            JOIN users u ON l.user_id = u.id
            JOIN books b ON l.book_id = b.id
            ORDER BY l.issue_date DESC
        """;
        
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public int getUserActiveLoanCount(int userId) {
        String query = "SELECT COUNT(*) FROM loans WHERE user_id = ? AND status = 'ISSUED'";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getUserTotalFines(int userId) {
        String query = "SELECT SUM(fine_amount) FROM loans WHERE user_id = ? AND fine_amount > 0";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private boolean hasActiveBookLoan(int userId, int bookId) {
        String query = "SELECT COUNT(*) FROM loans WHERE user_id = ? AND book_id = ? AND status = 'ISSUED'";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private double calculateFine(Loan loan) {
        if (loan.getDueDate().isAfter(LocalDateTime.now())) {
            return 0.0; // Not overdue
        }
        
        long daysOverdue = java.time.Duration.between(loan.getDueDate(), LocalDateTime.now()).toDays();
        return Math.max(0, daysOverdue * FINE_PER_DAY);
    }

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setUserId(rs.getInt("user_id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setIssueDate(rs.getTimestamp("issue_date").toLocalDateTime());
        loan.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
        
        Timestamp returnDate = rs.getTimestamp("return_date");
        if (returnDate != null) {
            loan.setReturnDate(returnDate.toLocalDateTime());
        }
        
        loan.setStatus(rs.getString("status"));
        loan.setFineAmount(rs.getDouble("fine_amount"));
        loan.setUserName(rs.getString("user_name"));
        loan.setBookTitle(rs.getString("book_title"));
        loan.setBookAuthor(rs.getString("book_author"));
        
        return loan;
    }
}