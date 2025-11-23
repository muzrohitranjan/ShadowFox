import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class BookServiceTest {
    private DatabaseManager dbManager;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        dbManager = new DatabaseManager();
        bookService = new BookService(dbManager);
    }

    @AfterEach
    void tearDown() {
        dbManager.closeConnection();
    }

    // Add Book Tests (5 test cases)
    @Test
    void testAddValidBook() {
        Book book = new Book("978-1234567890", "Test Book", "Test Author", 
                           "Test Publisher", 2023, "Test Category", 3, "Test description");
        assertTrue(bookService.addBook(book));
        
        List<Book> books = bookService.searchBooks("Test Book");
        assertFalse(books.isEmpty());
        assertEquals("Test Book", books.get(0).getTitle());
    }

    @Test
    void testAddBookWithDuplicateISBN() {
        Book book1 = new Book("978-1111111111", "Book One", "Author One", 
                             "Publisher", 2023, "Category", 2, "Description");
        Book book2 = new Book("978-1111111111", "Book Two", "Author Two", 
                             "Publisher", 2023, "Category", 1, "Description");
        
        assertTrue(bookService.addBook(book1));
        assertFalse(bookService.addBook(book2)); // Should fail due to duplicate ISBN
    }

    @Test
    void testAddBookWithNullISBN() {
        Book book = new Book(null, "No ISBN Book", "Author", "Publisher", 2023, "Category", 1, "Description");
        assertTrue(bookService.addBook(book)); // Should work with null ISBN
    }

    @Test
    void testAddBookWithMinimalInfo() {
        Book book = new Book("978-2222222222", "Minimal Book", "Author", null, 0, null, 1, null);
        assertTrue(bookService.addBook(book));
    }

    @Test
    void testAddMultipleBooks() {
        Book book1 = new Book("978-3333333333", "Book 1", "Author 1", "Publisher", 2023, "Fiction", 2, "Desc 1");
        Book book2 = new Book("978-4444444444", "Book 2", "Author 2", "Publisher", 2023, "Non-Fiction", 3, "Desc 2");
        Book book3 = new Book("978-5555555555", "Book 3", "Author 3", "Publisher", 2023, "Science", 1, "Desc 3");
        
        assertTrue(bookService.addBook(book1));
        assertTrue(bookService.addBook(book2));
        assertTrue(bookService.addBook(book3));
        
        List<Book> allBooks = bookService.getAllBooks();
        assertTrue(allBooks.size() >= 3);
    }

    // Book Retrieval Tests (5 test cases)
    @Test
    void testGetBookById() {
        List<Book> books = bookService.getAllBooks();
        assertFalse(books.isEmpty());
        
        Book firstBook = books.get(0);
        Book retrieved = bookService.getBookById(firstBook.getId());
        
        assertNotNull(retrieved);
        assertEquals(firstBook.getTitle(), retrieved.getTitle());
        assertEquals(firstBook.getAuthor(), retrieved.getAuthor());
    }

    @Test
    void testGetBookByInvalidId() {
        Book book = bookService.getBookById(99999);
        assertNull(book);
    }

    @Test
    void testGetAllBooks() {
        List<Book> books = bookService.getAllBooks();
        assertFalse(books.isEmpty());
        assertTrue(books.size() >= 8); // At least sample books
    }

    @Test
    void testGetAvailableBooks() {
        List<Book> availableBooks = bookService.getAvailableBooks();
        assertFalse(availableBooks.isEmpty());
        
        // All returned books should have available copies > 0
        for (Book book : availableBooks) {
            assertTrue(book.getAvailableCopies() > 0);
        }
    }

    @Test
    void testGetBooksByCategory() {
        List<Book> programmingBooks = bookService.getBooksByCategory("Programming");
        assertFalse(programmingBooks.isEmpty());
        
        for (Book book : programmingBooks) {
            assertEquals("Programming", book.getCategory());
        }
    }

    // Search Tests (5 test cases)
    @Test
    void testSearchBooksByTitle() {
        List<Book> books = bookService.searchBooks("Java");
        assertFalse(books.isEmpty());
        
        boolean foundJavaBook = books.stream()
                .anyMatch(book -> book.getTitle().toLowerCase().contains("java"));
        assertTrue(foundJavaBook);
    }

    @Test
    void testSearchBooksByAuthor() {
        List<Book> books = bookService.searchBooks("Joshua Bloch");
        assertFalse(books.isEmpty());
        
        boolean foundAuthorBook = books.stream()
                .anyMatch(book -> book.getAuthor().contains("Joshua Bloch"));
        assertTrue(foundAuthorBook);
    }

    @Test
    void testSearchBooksByISBN() {
        List<Book> allBooks = bookService.getAllBooks();
        if (!allBooks.isEmpty()) {
            Book firstBook = allBooks.get(0);
            if (firstBook.getIsbn() != null) {
                List<Book> books = bookService.searchBooks(firstBook.getIsbn());
                assertFalse(books.isEmpty());
                assertEquals(firstBook.getId(), books.get(0).getId());
            }
        }
    }

    @Test
    void testSearchBooksNoResults() {
        List<Book> books = bookService.searchBooks("NonExistentBookTitle12345");
        assertTrue(books.isEmpty());
    }

    @Test
    void testSearchBooksCaseInsensitive() {
        List<Book> lowerCase = bookService.searchBooks("java");
        List<Book> upperCase = bookService.searchBooks("JAVA");
        List<Book> mixedCase = bookService.searchBooks("Java");
        
        // All should return similar results (case-insensitive search)
        assertFalse(lowerCase.isEmpty());
        assertFalse(upperCase.isEmpty());
        assertFalse(mixedCase.isEmpty());
    }

    // Update Book Tests (5 test cases)
    @Test
    void testUpdateBook() {
        Book newBook = new Book("978-6666666666", "Original Title", "Original Author", 
                               "Publisher", 2023, "Category", 2, "Original description");
        assertTrue(bookService.addBook(newBook));
        
        List<Book> books = bookService.searchBooks("Original Title");
        assertFalse(books.isEmpty());
        
        Book bookToUpdate = books.get(0);
        bookToUpdate.setTitle("Updated Title");
        bookToUpdate.setAuthor("Updated Author");
        bookToUpdate.setDescription("Updated description");
        
        assertTrue(bookService.updateBook(bookToUpdate));
        
        Book updated = bookService.getBookById(bookToUpdate.getId());
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Author", updated.getAuthor());
        assertEquals("Updated description", updated.getDescription());
    }

    @Test
    void testUpdateNonExistentBook() {
        Book fakeBook = new Book("978-7777777777", "Fake Book", "Fake Author", 
                                "Publisher", 2023, "Category", 1, "Description");
        fakeBook.setId(99999); // Non-existent ID
        
        assertFalse(bookService.updateBook(fakeBook));
    }

    @Test
    void testUpdateBookCopies() {
        List<Book> books = bookService.getAllBooks();
        assertFalse(books.isEmpty());
        
        Book book = books.get(0);
        int originalTotal = book.getTotalCopies();
        int originalAvailable = book.getAvailableCopies();
        
        book.setTotalCopies(originalTotal + 2);
        book.setAvailableCopies(originalAvailable + 2);
        
        assertTrue(bookService.updateBook(book));
        
        Book updated = bookService.getBookById(book.getId());
        assertEquals(originalTotal + 2, updated.getTotalCopies());
        assertEquals(originalAvailable + 2, updated.getAvailableCopies());
    }

    @Test
    void testUpdateAvailableCopies() {
        List<Book> books = bookService.getAllBooks();
        assertFalse(books.isEmpty());
        
        Book book = books.get(0);
        int originalAvailable = book.getAvailableCopies();
        
        // Decrease available copies
        assertTrue(bookService.updateAvailableCopies(book.getId(), -1));
        
        Book updated = bookService.getBookById(book.getId());
        assertEquals(originalAvailable - 1, updated.getAvailableCopies());
        
        // Increase available copies
        assertTrue(bookService.updateAvailableCopies(book.getId(), 1));
        
        Book restored = bookService.getBookById(book.getId());
        assertEquals(originalAvailable, restored.getAvailableCopies());
    }

    @Test
    void testUpdateAvailableCopiesInvalidOperation() {
        List<Book> books = bookService.getAllBooks();
        assertFalse(books.isEmpty());
        
        Book book = books.get(0);
        int availableCopies = book.getAvailableCopies();
        
        // Try to decrease more than available (should fail)
        assertFalse(bookService.updateAvailableCopies(book.getId(), -(availableCopies + 1)));
    }

    // Category and Author Tests (5 test cases)
    @Test
    void testGetAllCategories() {
        List<String> categories = bookService.getAllCategories();
        assertFalse(categories.isEmpty());
        assertTrue(categories.contains("Programming"));
    }

    @Test
    void testGetAllAuthors() {
        List<String> authors = bookService.getAllAuthors();
        assertFalse(authors.isEmpty());
        assertTrue(authors.stream().anyMatch(author -> author.contains("Joshua Bloch")));
    }

    @Test
    void testCategoriesAreSorted() {
        List<String> categories = bookService.getAllCategories();
        if (categories.size() > 1) {
            for (int i = 1; i < categories.size(); i++) {
                assertTrue(categories.get(i-1).compareTo(categories.get(i)) <= 0);
            }
        }
    }

    @Test
    void testAuthorsAreSorted() {
        List<String> authors = bookService.getAllAuthors();
        if (authors.size() > 1) {
            for (int i = 1; i < authors.size(); i++) {
                assertTrue(authors.get(i-1).compareTo(authors.get(i)) <= 0);
            }
        }
    }

    @Test
    void testCategoriesAreUnique() {
        List<String> categories = bookService.getAllCategories();
        long uniqueCount = categories.stream().distinct().count();
        assertEquals(categories.size(), uniqueCount);
    }

    // Delete Book Tests (5 test cases)
    @Test
    void testDeleteBookWithoutLoans() {
        Book newBook = new Book("978-8888888888", "Delete Test", "Test Author", 
                               "Publisher", 2023, "Category", 1, "Description");
        assertTrue(bookService.addBook(newBook));
        
        List<Book> books = bookService.searchBooks("Delete Test");
        assertFalse(books.isEmpty());
        
        int bookId = books.get(0).getId();
        assertTrue(bookService.deleteBook(bookId));
        
        Book deleted = bookService.getBookById(bookId);
        assertNull(deleted);
    }

    @Test
    void testDeleteNonExistentBook() {
        assertFalse(bookService.deleteBook(99999));
    }

    @Test
    void testDeleteBookMultipleTimes() {
        Book newBook = new Book("978-9999999999", "Multi Delete Test", "Test Author", 
                               "Publisher", 2023, "Category", 1, "Description");
        assertTrue(bookService.addBook(newBook));
        
        List<Book> books = bookService.searchBooks("Multi Delete Test");
        assertFalse(books.isEmpty());
        
        int bookId = books.get(0).getId();
        assertTrue(bookService.deleteBook(bookId));
        assertFalse(bookService.deleteBook(bookId)); // Second delete should fail
    }

    @Test
    void testDeleteBookAffectsSearch() {
        Book newBook = new Book("978-0000000000", "Search Delete Test", "Test Author", 
                               "Publisher", 2023, "Category", 1, "Description");
        assertTrue(bookService.addBook(newBook));
        
        List<Book> beforeDelete = bookService.searchBooks("Search Delete Test");
        assertFalse(beforeDelete.isEmpty());
        
        int bookId = beforeDelete.get(0).getId();
        assertTrue(bookService.deleteBook(bookId));
        
        List<Book> afterDelete = bookService.searchBooks("Search Delete Test");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeleteBookAffectsCategoryList() {
        // Add a book with unique category
        Book uniqueBook = new Book("978-1010101010", "Unique Category Book", "Author", 
                                 "Publisher", 2023, "UniqueTestCategory", 1, "Description");
        assertTrue(bookService.addBook(uniqueBook));
        
        List<String> categoriesBefore = bookService.getAllCategories();
        assertTrue(categoriesBefore.contains("UniqueTestCategory"));
        
        List<Book> books = bookService.searchBooks("Unique Category Book");
        int bookId = books.get(0).getId();
        assertTrue(bookService.deleteBook(bookId));
        
        List<String> categoriesAfter = bookService.getAllCategories();
        assertFalse(categoriesAfter.contains("UniqueTestCategory"));
    }
}