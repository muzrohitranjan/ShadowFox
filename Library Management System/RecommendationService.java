import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class RecommendationService {
    private DatabaseManager dbManager;
    private BookService bookService;

    public RecommendationService(DatabaseManager dbManager, BookService bookService) {
        this.dbManager = dbManager;
        this.bookService = bookService;
    }

    public List<Book> getRecommendationsForUser(int userId, int limit) {
        List<Book> recommendations = new ArrayList<>();
        
        // Get user's reading history and preferences
        List<String> userCategories = getUserPreferredCategories(userId);
        List<String> userAuthors = getUserPreferredAuthors(userId);
        Set<Integer> userReadBooks = getUserReadBooks(userId);
        
        // Category-based recommendations
        recommendations.addAll(getBooksByCategories(userCategories, userReadBooks, limit / 2));
        
        // Author-based recommendations
        recommendations.addAll(getBooksByAuthors(userAuthors, userReadBooks, limit / 2));
        
        // Popular books (highly rated)
        if (recommendations.size() < limit) {
            recommendations.addAll(getPopularBooks(userReadBooks, limit - recommendations.size()));
        }
        
        // Remove duplicates and limit results
        return recommendations.stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public boolean rateBook(int userId, int bookId, int rating, String review) {
        String query = "INSERT OR REPLACE INTO book_ratings (user_id, book_id, rating, review) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setInt(3, rating);
            stmt.setString(4, review);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getBookAverageRating(int bookId) {
        String query = "SELECT AVG(rating) FROM book_ratings WHERE book_id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, bookId);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public List<Book> getTopRatedBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String query = """
            SELECT b.*, AVG(r.rating) as avg_rating, COUNT(r.rating) as rating_count
            FROM books b
            JOIN book_ratings r ON b.id = r.book_id
            GROUP BY b.id
            HAVING rating_count >= 2
            ORDER BY avg_rating DESC, rating_count DESC
            LIMIT ?
        """;
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, limit);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getSimilarBooks(int bookId, int limit) {
        Book targetBook = bookService.getBookById(bookId);
        if (targetBook == null) return new ArrayList<>();
        
        List<Book> similarBooks = new ArrayList<>();
        
        // Books by same author
        similarBooks.addAll(getBooksByAuthor(targetBook.getAuthor(), Set.of(bookId), limit / 2));
        
        // Books in same category
        if (similarBooks.size() < limit) {
            similarBooks.addAll(getBooksByCategory(targetBook.getCategory(), 
                    similarBooks.stream().map(Book::getId).collect(Collectors.toSet()), 
                    limit - similarBooks.size()));
        }
        
        return similarBooks.stream().limit(limit).collect(Collectors.toList());
    }

    public boolean updateUserPreferences(int userId, List<String> categories, List<String> authors) {
        String deleteQuery = "DELETE FROM user_preferences WHERE user_id = ?";
        String insertQuery = "INSERT INTO user_preferences (user_id, preferred_categories, preferred_authors) VALUES (?, ?, ?)";
        
        try (PreparedStatement deleteStmt = dbManager.getConnection().prepareStatement(deleteQuery);
             PreparedStatement insertStmt = dbManager.getConnection().prepareStatement(insertQuery)) {
            
            deleteStmt.setInt(1, userId);
            deleteStmt.executeUpdate();
            
            insertStmt.setInt(1, userId);
            insertStmt.setString(2, String.join(",", categories));
            insertStmt.setString(3, String.join(",", authors));
            
            return insertStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<String> getUserPreferredCategories(int userId) {
        List<String> categories = new ArrayList<>();
        
        // Get explicit preferences
        String prefQuery = "SELECT preferred_categories FROM user_preferences WHERE user_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(prefQuery)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getString(1) != null) {
                categories.addAll(Arrays.asList(rs.getString(1).split(",")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Get categories from reading history
        String historyQuery = """
            SELECT b.category, COUNT(*) as count
            FROM loans l
            JOIN books b ON l.book_id = b.id
            WHERE l.user_id = ? AND b.category IS NOT NULL
            GROUP BY b.category
            ORDER BY count DESC
            LIMIT 5
        """;
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(historyQuery)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String category = rs.getString("category");
                if (!categories.contains(category)) {
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return categories;
    }

    private List<String> getUserPreferredAuthors(int userId) {
        List<String> authors = new ArrayList<>();
        
        // Get explicit preferences
        String prefQuery = "SELECT preferred_authors FROM user_preferences WHERE user_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(prefQuery)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getString(1) != null) {
                authors.addAll(Arrays.asList(rs.getString(1).split(",")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Get authors from reading history
        String historyQuery = """
            SELECT b.author, COUNT(*) as count
            FROM loans l
            JOIN books b ON l.book_id = b.id
            WHERE l.user_id = ?
            GROUP BY b.author
            ORDER BY count DESC
            LIMIT 5
        """;
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(historyQuery)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String author = rs.getString("author");
                if (!authors.contains(author)) {
                    authors.add(author);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return authors;
    }

    private Set<Integer> getUserReadBooks(int userId) {
        Set<Integer> bookIds = new HashSet<>();
        String query = "SELECT DISTINCT book_id FROM loans WHERE user_id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookIds.add(rs.getInt("book_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bookIds;
    }

    private List<Book> getBooksByCategories(List<String> categories, Set<Integer> excludeIds, int limit) {
        if (categories.isEmpty()) return new ArrayList<>();
        
        List<Book> books = new ArrayList<>();
        String placeholders = String.join(",", Collections.nCopies(categories.size(), "?"));
        String query = String.format(
            "SELECT * FROM books WHERE category IN (%s) AND available_copies > 0 ORDER BY RANDOM() LIMIT ?",
            placeholders
        );
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            for (int i = 0; i < categories.size(); i++) {
                stmt.setString(i + 1, categories.get(i));
            }
            stmt.setInt(categories.size() + 1, limit * 2); // Get more to filter out read books
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next() && books.size() < limit) {
                Book book = mapResultSetToBook(rs);
                if (!excludeIds.contains(book.getId())) {
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return books;
    }

    private List<Book> getBooksByAuthors(List<String> authors, Set<Integer> excludeIds, int limit) {
        if (authors.isEmpty()) return new ArrayList<>();
        
        List<Book> books = new ArrayList<>();
        String placeholders = String.join(",", Collections.nCopies(authors.size(), "?"));
        String query = String.format(
            "SELECT * FROM books WHERE author IN (%s) AND available_copies > 0 ORDER BY RANDOM() LIMIT ?",
            placeholders
        );
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            for (int i = 0; i < authors.size(); i++) {
                stmt.setString(i + 1, authors.get(i));
            }
            stmt.setInt(authors.size() + 1, limit * 2);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next() && books.size() < limit) {
                Book book = mapResultSetToBook(rs);
                if (!excludeIds.contains(book.getId())) {
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return books;
    }

    private List<Book> getBooksByAuthor(String author, Set<Integer> excludeIds, int limit) {
        return getBooksByAuthors(List.of(author), excludeIds, limit);
    }

    private List<Book> getBooksByCategory(String category, Set<Integer> excludeIds, int limit) {
        return getBooksByCategories(List.of(category), excludeIds, limit);
    }

    private List<Book> getPopularBooks(Set<Integer> excludeIds, int limit) {
        List<Book> books = new ArrayList<>();
        String query = """
            SELECT b.*, COUNT(l.id) as loan_count
            FROM books b
            LEFT JOIN loans l ON b.id = l.book_id
            WHERE b.available_copies > 0
            GROUP BY b.id
            ORDER BY loan_count DESC, b.title
            LIMIT ?
        """;
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(query)) {
            stmt.setInt(1, limit * 2);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next() && books.size() < limit) {
                Book book = mapResultSetToBook(rs);
                if (!excludeIds.contains(book.getId())) {
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return books;
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setCategory(rs.getString("category"));
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setAvailableCopies(rs.getInt("available_copies"));
        book.setDescription(rs.getString("description"));
        return book;
    }
}