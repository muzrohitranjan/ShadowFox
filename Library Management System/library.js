// Library Management System - Core Logic
class LibrarySystem {
    constructor() {
        this.currentUser = null;
        this.books = [];
        this.users = [];
        this.loans = [];
        this.selectedBook = null;
        this.selectedRating = 0;
        this.loadSampleData();
    }

    loadSampleData() {
        // Sample users
        this.users = [
            { id: 1, username: 'admin', password: 'admin123', email: 'admin@library.com', fullName: 'Library Administrator', role: 'ADMIN', isActive: true },
            { id: 2, username: 'john_doe', password: 'password123', email: 'john@email.com', fullName: 'John Doe', role: 'USER', isActive: true },
            { id: 3, username: 'jane_smith', password: 'password123', email: 'jane@email.com', fullName: 'Jane Smith', role: 'USER', isActive: true },
            { id: 4, username: 'bob_wilson', password: 'password123', email: 'bob@email.com', fullName: 'Bob Wilson', role: 'USER', isActive: true }
        ];

        // Sample books
        this.books = [
            { id: 1, isbn: '978-0134685991', title: 'Effective Java', author: 'Joshua Bloch', publisher: 'Addison-Wesley', year: 2017, category: 'Programming', totalCopies: 3, availableCopies: 2, description: 'Best practices for Java programming', rating: 4.5 },
            { id: 2, isbn: '978-0596009205', title: 'Head First Design Patterns', author: 'Eric Freeman', publisher: "O'Reilly Media", year: 2004, category: 'Programming', totalCopies: 2, availableCopies: 1, description: 'Design patterns in object-oriented programming', rating: 4.2 },
            { id: 3, isbn: '978-0132350884', title: 'Clean Code', author: 'Robert C. Martin', publisher: 'Prentice Hall', year: 2008, category: 'Programming', totalCopies: 4, availableCopies: 3, description: 'A handbook of agile software craftsmanship', rating: 4.7 },
            { id: 4, isbn: '978-1449331818', title: 'Learning SQL', author: 'Alan Beaulieu', publisher: "O'Reilly Media", year: 2009, category: 'Database', totalCopies: 2, availableCopies: 2, description: 'Master SQL fundamentals', rating: 4.0 },
            { id: 5, isbn: '978-0321573513', title: 'Algorithms', author: 'Robert Sedgewick', publisher: 'Addison-Wesley', year: 2011, category: 'Computer Science', totalCopies: 3, availableCopies: 1, description: 'Essential algorithms and data structures', rating: 4.3 },
            { id: 6, isbn: '978-0262033848', title: 'Introduction to Algorithms', author: 'Thomas H. Cormen', publisher: 'MIT Press', year: 2009, category: 'Computer Science', totalCopies: 2, availableCopies: 0, description: 'Comprehensive algorithms textbook', rating: 4.6 },
            { id: 7, isbn: '978-0735619678', title: 'Code Complete', author: 'Steve McConnell', publisher: 'Microsoft Press', year: 2004, category: 'Programming', totalCopies: 2, availableCopies: 2, description: 'A practical handbook of software construction', rating: 4.4 },
            { id: 8, isbn: '978-0201616224', title: 'The Pragmatic Programmer', author: 'Andrew Hunt', publisher: 'Addison-Wesley', year: 1999, category: 'Programming', totalCopies: 3, availableCopies: 1, description: 'From journeyman to master', rating: 4.8 }
        ];

        // Sample loans
        this.loans = [
            { id: 1, userId: 2, bookId: 1, issueDate: new Date('2024-01-15'), dueDate: new Date('2024-01-29'), returnDate: null, status: 'ISSUED', fineAmount: 0 },
            { id: 2, userId: 3, bookId: 5, issueDate: new Date('2024-01-10'), dueDate: new Date('2024-01-24'), returnDate: null, status: 'ISSUED', fineAmount: 0 },
            { id: 3, userId: 2, bookId: 2, issueDate: new Date('2024-01-05'), dueDate: new Date('2024-01-19'), returnDate: new Date('2024-01-18'), status: 'RETURNED', fineAmount: 0 }
        ];

        this.nextUserId = 5;
        this.nextBookId = 9;
        this.nextLoanId = 4;
    }

    // Authentication
    login(username, password) {
        const user = this.users.find(u => u.username === username && u.password === password && u.isActive);
        if (user) {
            this.currentUser = user;
            return { success: true, user };
        }
        return { success: false, message: 'Invalid credentials' };
    }

    register(userData) {
        if (this.users.find(u => u.username === userData.username)) {
            return { success: false, message: 'Username already exists' };
        }
        if (this.users.find(u => u.email === userData.email)) {
            return { success: false, message: 'Email already exists' };
        }

        const newUser = {
            id: this.nextUserId++,
            username: userData.username,
            password: userData.password,
            email: userData.email,
            fullName: userData.fullName,
            role: 'USER',
            isActive: true
        };

        this.users.push(newUser);
        return { success: true, user: newUser };
    }

    logout() {
        this.currentUser = null;
    }

    // Book Management
    getAllBooks() {
        return this.books;
    }

    searchBooks(query) {
        if (!query) return this.books;
        
        const searchTerm = query.toLowerCase();
        return this.books.filter(book =>
            book.title.toLowerCase().includes(searchTerm) ||
            book.author.toLowerCase().includes(searchTerm) ||
            book.category.toLowerCase().includes(searchTerm) ||
            book.isbn.toLowerCase().includes(searchTerm)
        );
    }

    getBooksByCategory(category) {
        if (!category) return this.books;
        return this.books.filter(book => book.category === category);
    }

    getAvailableBooks() {
        return this.books.filter(book => book.availableCopies > 0);
    }

    getBookById(id) {
        return this.books.find(book => book.id === id);
    }

    addBook(bookData) {
        if (this.books.find(b => b.isbn === bookData.isbn)) {
            return { success: false, message: 'ISBN already exists' };
        }

        const newBook = {
            id: this.nextBookId++,
            isbn: bookData.isbn,
            title: bookData.title,
            author: bookData.author,
            publisher: bookData.publisher || '',
            year: bookData.year || new Date().getFullYear(),
            category: bookData.category,
            totalCopies: bookData.totalCopies,
            availableCopies: bookData.totalCopies,
            description: bookData.description || '',
            rating: 0
        };

        this.books.push(newBook);
        return { success: true, book: newBook };
    }

    updateBook(bookData) {
        const index = this.books.findIndex(b => b.id === bookData.id);
        if (index === -1) return { success: false, message: 'Book not found' };

        this.books[index] = { ...this.books[index], ...bookData };
        return { success: true, book: this.books[index] };
    }

    deleteBook(id) {
        const hasActiveLoans = this.loans.some(loan => loan.bookId === id && loan.status === 'ISSUED');
        if (hasActiveLoans) {
            return { success: false, message: 'Cannot delete book with active loans' };
        }

        const index = this.books.findIndex(b => b.id === id);
        if (index === -1) return { success: false, message: 'Book not found' };

        this.books.splice(index, 1);
        return { success: true };
    }

    // Loan Management
    issueBook(bookId, loanDays = 14) {
        if (!this.currentUser) return { success: false, message: 'Not logged in' };

        const book = this.getBookById(bookId);
        if (!book || book.availableCopies <= 0) {
            return { success: false, message: 'Book not available' };
        }

        // Check if user already has this book
        const existingLoan = this.loans.find(loan => 
            loan.userId === this.currentUser.id && 
            loan.bookId === bookId && 
            loan.status === 'ISSUED'
        );
        if (existingLoan) {
            return { success: false, message: 'You already have this book' };
        }

        const issueDate = new Date();
        const dueDate = new Date();
        dueDate.setDate(dueDate.getDate() + loanDays);

        const newLoan = {
            id: this.nextLoanId++,
            userId: this.currentUser.id,
            bookId: bookId,
            issueDate: issueDate,
            dueDate: dueDate,
            returnDate: null,
            status: 'ISSUED',
            fineAmount: 0
        };

        this.loans.push(newLoan);
        book.availableCopies--;

        return { success: true, loan: newLoan };
    }

    returnBook(loanId) {
        const loan = this.loans.find(l => l.id === loanId);
        if (!loan || loan.status !== 'ISSUED') {
            return { success: false, message: 'Loan not found or already returned' };
        }

        const book = this.getBookById(loan.bookId);
        if (book) {
            book.availableCopies++;
        }

        loan.returnDate = new Date();
        loan.status = 'RETURNED';

        // Calculate fine if overdue
        if (new Date() > loan.dueDate) {
            const daysOverdue = Math.ceil((new Date() - loan.dueDate) / (1000 * 60 * 60 * 24));
            loan.fineAmount = daysOverdue * 1.0; // $1 per day
        }

        return { success: true, loan: loan };
    }

    renewLoan(loanId, additionalDays = 7) {
        const loan = this.loans.find(l => l.id === loanId);
        if (!loan || loan.status !== 'ISSUED') {
            return { success: false, message: 'Loan not found or not active' };
        }

        loan.dueDate.setDate(loan.dueDate.getDate() + additionalDays);
        return { success: true, loan: loan };
    }

    getUserLoans(userId = null) {
        const targetUserId = userId || (this.currentUser ? this.currentUser.id : null);
        if (!targetUserId) return [];

        return this.loans.filter(loan => loan.userId === targetUserId).map(loan => {
            const book = this.getBookById(loan.bookId);
            const user = this.users.find(u => u.id === loan.userId);
            return {
                ...loan,
                bookTitle: book ? book.title : 'Unknown',
                bookAuthor: book ? book.author : 'Unknown',
                userName: user ? user.fullName : 'Unknown'
            };
        });
    }

    getOverdueLoans() {
        const now = new Date();
        return this.loans.filter(loan => 
            loan.status === 'ISSUED' && loan.dueDate < now
        ).map(loan => {
            const book = this.getBookById(loan.bookId);
            const user = this.users.find(u => u.id === loan.userId);
            return {
                ...loan,
                bookTitle: book ? book.title : 'Unknown',
                bookAuthor: book ? book.author : 'Unknown',
                userName: user ? user.fullName : 'Unknown'
            };
        });
    }

    // Recommendations
    getRecommendations(limit = 6) {
        if (!this.currentUser) return [];

        const userLoans = this.getUserLoans();
        const readBookIds = userLoans.map(loan => loan.bookId);
        const readCategories = [...new Set(readBookIds.map(id => {
            const book = this.getBookById(id);
            return book ? book.category : null;
        }).filter(Boolean))];

        // Get books from preferred categories that user hasn't read
        let recommendations = this.books.filter(book => 
            !readBookIds.includes(book.id) && 
            readCategories.includes(book.category) &&
            book.availableCopies > 0
        );

        // If not enough, add popular books
        if (recommendations.length < limit) {
            const popularBooks = this.books
                .filter(book => !readBookIds.includes(book.id) && book.availableCopies > 0)
                .sort((a, b) => (b.rating || 0) - (a.rating || 0));
            
            recommendations = [...recommendations, ...popularBooks]
                .filter((book, index, self) => self.findIndex(b => b.id === book.id) === index);
        }

        return recommendations.slice(0, limit);
    }

    // Statistics
    getStatistics() {
        const totalBooks = this.books.length;
        const activeLoans = this.loans.filter(loan => loan.status === 'ISSUED').length;
        const overdueLoans = this.getOverdueLoans().length;
        const totalFines = this.loans.reduce((sum, loan) => sum + (loan.fineAmount || 0), 0);

        return {
            totalBooks,
            activeLoans,
            overdueLoans,
            totalFines
        };
    }

    getUserStatistics() {
        if (!this.currentUser) return {};

        const userLoans = this.getUserLoans();
        const activeLoans = userLoans.filter(loan => loan.status === 'ISSUED').length;
        const overdueLoans = userLoans.filter(loan => 
            loan.status === 'ISSUED' && new Date() > loan.dueDate
        ).length;
        const totalFines = userLoans.reduce((sum, loan) => sum + (loan.fineAmount || 0), 0);

        return {
            activeLoans,
            overdueLoans,
            totalFines,
            totalBooksRead: userLoans.filter(loan => loan.status === 'RETURNED').length
        };
    }

    getCategories() {
        return [...new Set(this.books.map(book => book.category))].sort();
    }

    getPopularBooks(limit = 5) {
        return this.books
            .sort((a, b) => (b.rating || 0) - (a.rating || 0))
            .slice(0, limit);
    }

    // Rating System
    rateBook(bookId, rating, review = '') {
        if (!this.currentUser) return { success: false, message: 'Not logged in' };

        const book = this.getBookById(bookId);
        if (!book) return { success: false, message: 'Book not found' };

        // For simplicity, just update the book's rating
        // In a real system, you'd store individual ratings
        book.rating = rating;
        
        return { success: true };
    }
}

// Global library system instance
const library = new LibrarySystem();