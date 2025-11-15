import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class LibraryManagementApp {
    private DatabaseManager dbManager;
    private UserService userService;
    private BookService bookService;
    private LoanService loanService;
    private RecommendationService recommendationService;
    private Scanner scanner;
    private User currentUser;

    public LibraryManagementApp() {
        dbManager = new DatabaseManager();
        userService = new UserService(dbManager);
        bookService = new BookService(dbManager);
        loanService = new LoanService(dbManager, bookService);
        recommendationService = new RecommendationService(dbManager, bookService);
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Library Management System ===");
        
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n--- Login Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1: login(); break;
            case 2: register(); break;
            case 3: 
                System.out.println("Thank you for using Library Management System!");
                dbManager.closeConnection();
                System.exit(0);
            default: System.out.println("Invalid option!");
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== Welcome, " + currentUser.getFullName() + " ===");
        System.out.println("1. Browse Books");
        System.out.println("2. Search Books");
        System.out.println("3. My Loans");
        System.out.println("4. Book Recommendations");
        System.out.println("5. Rate a Book");
        
        if (currentUser.isAdmin()) {
            System.out.println("\n--- Admin Options ---");
            System.out.println("6. Manage Books");
            System.out.println("7. Manage Users");
            System.out.println("8. Manage Loans");
            System.out.println("9. View Reports");
        }
        
        System.out.println("0. Logout");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1: browseBooks(); break;
            case 2: searchBooks(); break;
            case 3: viewMyLoans(); break;
            case 4: viewRecommendations(); break;
            case 5: rateBook(); break;
            case 6: if (currentUser.isAdmin()) manageBooks(); break;
            case 7: if (currentUser.isAdmin()) manageUsers(); break;
            case 8: if (currentUser.isAdmin()) manageLoans(); break;
            case 9: if (currentUser.isAdmin()) viewReports(); break;
            case 0: logout(); break;
            default: System.out.println("Invalid option!");
        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        User user = userService.authenticate(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful! Welcome, " + user.getFullName());
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    private void register() {
        System.out.println("\n--- User Registration ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        if (userService.isUsernameExists(username)) {
            System.out.println("Username already exists!");
            return;
        }
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        if (userService.isEmailExists(email)) {
            System.out.println("Email already exists!");
            return;
        }
        
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();
        
        User user = new User(username, password, email, fullName);
        if (userService.registerUser(user)) {
            System.out.println("Registration successful! You can now login.");
        } else {
            System.out.println("Registration failed!");
        }
    }

    private void browseBooks() {
        List<Book> books = bookService.getAllBooks();
        displayBooks(books);
        
        if (!books.isEmpty()) {
            System.out.print("\nEnter book ID to issue (0 to go back): ");
            int bookId = getIntInput();
            if (bookId > 0) {
                issueBook(bookId);
            }
        }
    }

    private void searchBooks() {
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine();
        
        List<Book> books = bookService.searchBooks(searchTerm);
        displayBooks(books);
        
        if (!books.isEmpty()) {
            System.out.print("\nEnter book ID to issue (0 to go back): ");
            int bookId = getIntInput();
            if (bookId > 0) {
                issueBook(bookId);
            }
        }
    }

    private void issueBook(int bookId) {
        System.out.print("Loan duration in days (default 14): ");
        String input = scanner.nextLine();
        int days = input.isEmpty() ? 14 : Integer.parseInt(input);
        
        if (loanService.issueBook(currentUser.getId(), bookId, days)) {
            System.out.println("Book issued successfully!");
        } else {
            System.out.println("Failed to issue book. Book may not be available or you already have it.");
        }
    }

    private void viewMyLoans() {
        List<Loan> loans = loanService.getUserLoans(currentUser.getId());
        
        if (loans.isEmpty()) {
            System.out.println("You have no loans.");
            return;
        }
        
        System.out.println("\n--- My Loans ---");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Loan loan : loans) {
            System.out.printf("ID: %d | %s by %s%n", loan.getId(), loan.getBookTitle(), loan.getBookAuthor());
            System.out.printf("Status: %s | Due: %s%n", loan.getStatus(), loan.getDueDate().format(formatter));
            if (loan.isOverdue()) {
                System.out.printf("OVERDUE by %d days!%n", loan.getDaysOverdue());
            }
            if (loan.getFineAmount() > 0) {
                System.out.printf("Fine: $%.2f%n", loan.getFineAmount());
            }
            System.out.println("---");
        }
        
        System.out.print("\nEnter loan ID to return book or renew (0 to go back): ");
        int loanId = getIntInput();
        if (loanId > 0) {
            System.out.println("1. Return Book");
            System.out.println("2. Renew Loan");
            System.out.print("Choose action: ");
            int action = getIntInput();
            
            if (action == 1) {
                if (loanService.returnBook(loanId)) {
                    System.out.println("Book returned successfully!");
                } else {
                    System.out.println("Failed to return book!");
                }
            } else if (action == 2) {
                System.out.print("Additional days (default 7): ");
                String input = scanner.nextLine();
                int days = input.isEmpty() ? 7 : Integer.parseInt(input);
                
                if (loanService.renewLoan(loanId, days)) {
                    System.out.println("Loan renewed successfully!");
                } else {
                    System.out.println("Failed to renew loan!");
                }
            }
        }
    }

    private void viewRecommendations() {
        List<Book> recommendations = recommendationService.getRecommendationsForUser(currentUser.getId(), 10);
        
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available. Try reading some books first!");
            return;
        }
        
        System.out.println("\n--- Recommended Books for You ---");
        displayBooks(recommendations);
        
        System.out.print("\nEnter book ID to issue (0 to go back): ");
        int bookId = getIntInput();
        if (bookId > 0) {
            issueBook(bookId);
        }
    }

    private void rateBook() {
        System.out.print("Enter book ID to rate: ");
        int bookId = getIntInput();
        
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        System.out.println("Rating book: " + book.getTitle());
        System.out.print("Rating (1-5): ");
        int rating = getIntInput();
        
        if (rating < 1 || rating > 5) {
            System.out.println("Invalid rating!");
            return;
        }
        
        System.out.print("Review (optional): ");
        String review = scanner.nextLine();
        
        if (recommendationService.rateBook(currentUser.getId(), bookId, rating, review)) {
            System.out.println("Rating submitted successfully!");
        } else {
            System.out.println("Failed to submit rating!");
        }
    }

    private void manageBooks() {
        System.out.println("\n--- Book Management ---");
        System.out.println("1. Add Book");
        System.out.println("2. Update Book");
        System.out.println("3. Delete Book");
        System.out.println("4. View All Books");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1: addBook(); break;
            case 2: updateBook(); break;
            case 3: deleteBook(); break;
            case 4: browseBooks(); break;
        }
    }

    private void addBook() {
        System.out.println("\n--- Add New Book ---");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("Publisher: ");
        String publisher = scanner.nextLine();
        System.out.print("Publication Year: ");
        int year = getIntInput();
        System.out.print("Category: ");
        String category = scanner.nextLine();
        System.out.print("Total Copies: ");
        int copies = getIntInput();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        
        Book book = new Book(isbn, title, author, publisher, year, category, copies, description);
        if (bookService.addBook(book)) {
            System.out.println("Book added successfully!");
        } else {
            System.out.println("Failed to add book!");
        }
    }

    private void updateBook() {
        System.out.print("Enter book ID to update: ");
        int bookId = getIntInput();
        
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        System.out.println("Current: " + book.getTitle());
        System.out.print("New title (press enter to keep current): ");
        String title = scanner.nextLine();
        if (!title.isEmpty()) book.setTitle(title);
        
        // Similar for other fields...
        if (bookService.updateBook(book)) {
            System.out.println("Book updated successfully!");
        } else {
            System.out.println("Failed to update book!");
        }
    }

    private void deleteBook() {
        System.out.print("Enter book ID to delete: ");
        int bookId = getIntInput();
        
        if (bookService.deleteBook(bookId)) {
            System.out.println("Book deleted successfully!");
        } else {
            System.out.println("Failed to delete book! It may have active loans.");
        }
    }

    private void manageUsers() {
        List<User> users = userService.getAllUsers();
        System.out.println("\n--- All Users ---");
        for (User user : users) {
            System.out.printf("ID: %d | %s (%s) | %s | Active: %s%n", 
                user.getId(), user.getFullName(), user.getUsername(), user.getRole(), user.isActive());
        }
    }

    private void manageLoans() {
        System.out.println("\n--- Loan Management ---");
        System.out.println("1. View All Loans");
        System.out.println("2. View Overdue Loans");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        List<Loan> loans = (choice == 2) ? loanService.getOverdueLoans() : loanService.getAllLoans();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("\n--- Loans ---");
        for (Loan loan : loans) {
            System.out.printf("ID: %d | %s | %s | %s | Due: %s%n", 
                loan.getId(), loan.getUserName(), loan.getBookTitle(), 
                loan.getStatus(), loan.getDueDate().format(formatter));
        }
    }

    private void viewReports() {
        System.out.println("\n--- Library Reports ---");
        
        List<Book> topRated = recommendationService.getTopRatedBooks(5);
        System.out.println("\nTop Rated Books:");
        for (Book book : topRated) {
            double rating = recommendationService.getBookAverageRating(book.getId());
            System.out.printf("- %s (Rating: %.1f)%n", book.getTitle(), rating);
        }
        
        List<Loan> overdueLoans = loanService.getOverdueLoans();
        System.out.println("\nOverdue Loans: " + overdueLoans.size());
        
        List<Book> allBooks = bookService.getAllBooks();
        long availableBooks = allBooks.stream().mapToLong(Book::getAvailableCopies).sum();
        System.out.println("Total Available Books: " + availableBooks);
    }

    private void displayBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        
        System.out.println("\n--- Books ---");
        for (Book book : books) {
            System.out.printf("ID: %d | %s by %s%n", book.getId(), book.getTitle(), book.getAuthor());
            System.out.printf("Category: %s | Available: %d/%d%n", 
                book.getCategory(), book.getAvailableCopies(), book.getTotalCopies());
            if (book.getDescription() != null && !book.getDescription().isEmpty()) {
                System.out.println("Description: " + book.getDescription());
            }
            System.out.println("---");
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully!");
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        new LibraryManagementApp().start();
    }
}