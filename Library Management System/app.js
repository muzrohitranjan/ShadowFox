// Main Application Logic
class LibraryApp {
    constructor() {
        this.currentSection = 'dashboard';
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.checkAuthStatus();
    }

    setupEventListeners() {
        // Login form
        document.getElementById('login-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleLogin();
        });

        // Register form
        document.getElementById('register-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleRegister();
        });

        // Navigation
        document.querySelectorAll('.nav-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const section = e.currentTarget.dataset.section;
                this.showSection(section);
            });
        });

        // Search
        document.getElementById('book-search').addEventListener('input', (e) => {
            this.searchBooks(e.target.value);
        });

        // Filters
        document.getElementById('category-filter').addEventListener('change', (e) => {
            this.filterBooks();
        });

        document.getElementById('availability-filter').addEventListener('change', (e) => {
            this.filterBooks();
        });

        // Star rating
        document.querySelectorAll('#star-rating i').forEach((star, index) => {
            star.addEventListener('click', () => {
                this.setRating(index + 1);
            });
        });
    }

    checkAuthStatus() {
        if (library.currentUser) {
            this.showApp();
        } else {
            this.showLogin();
        }
    }

    showLogin() {
        document.getElementById('login-modal').classList.add('active');
        document.getElementById('register-modal').classList.remove('active');
        document.getElementById('app').classList.remove('active');
    }

    showApp() {
        document.getElementById('login-modal').classList.remove('active');
        document.getElementById('register-modal').classList.remove('active');
        document.getElementById('app').classList.add('active');
        
        this.updateUserInfo();
        this.updateNavigation();
        this.showSection('dashboard');
    }

    handleLogin() {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        const result = library.login(username, password);
        if (result.success) {
            this.showToast('Login successful!', 'success');
            this.showApp();
        } else {
            this.showToast(result.message, 'error');
        }
    }

    handleRegister() {
        const userData = {
            username: document.getElementById('reg-username').value,
            email: document.getElementById('reg-email').value,
            fullName: document.getElementById('reg-fullname').value,
            password: document.getElementById('reg-password').value
        };

        const result = library.register(userData);
        if (result.success) {
            this.showToast('Registration successful! Please login.', 'success');
            this.showLogin();
            document.getElementById('register-form').reset();
        } else {
            this.showToast(result.message, 'error');
        }
    }

    updateUserInfo() {
        if (library.currentUser) {
            document.getElementById('user-name').textContent = `Welcome, ${library.currentUser.fullName}`;
        }
    }

    updateNavigation() {
        const adminItems = document.querySelectorAll('.admin-only');
        if (library.currentUser && library.currentUser.role === 'ADMIN') {
            adminItems.forEach(item => item.style.display = 'block');
        } else {
            adminItems.forEach(item => item.style.display = 'none');
        }
    }

    showSection(sectionName) {
        // Update navigation
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector(`[data-section="${sectionName}"]`).classList.add('active');

        // Update content
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });
        document.getElementById(sectionName).classList.add('active');

        this.currentSection = sectionName;

        // Load section-specific content
        switch (sectionName) {
            case 'dashboard':
                this.loadDashboard();
                break;
            case 'books':
                this.loadBooks();
                break;
            case 'my-loans':
                this.loadMyLoans();
                break;
            case 'recommendations':
                this.loadRecommendations();
                break;
            case 'manage-books':
                this.loadManageBooks();
                break;
            case 'manage-users':
                this.loadManageUsers();
                break;
            case 'reports':
                this.loadReports();
                break;
        }
    }

    loadDashboard() {
        const stats = library.getStatistics();
        const userStats = library.getUserStatistics();

        // Update statistics
        document.getElementById('total-books').textContent = stats.totalBooks;
        document.getElementById('active-loans').textContent = library.currentUser.role === 'ADMIN' ? stats.activeLoans : userStats.activeLoans;
        document.getElementById('overdue-count').textContent = library.currentUser.role === 'ADMIN' ? stats.overdueLoans : userStats.overdueLoans;
        document.getElementById('total-fines').textContent = `$${(library.currentUser.role === 'ADMIN' ? stats.totalFines : userStats.totalFines).toFixed(2)}`;

        // Load recent activity (simplified)
        this.loadRecentActivity();
        
        // Load popular books
        this.loadPopularBooks();
    }

    loadRecentActivity() {
        const container = document.getElementById('recent-activity');
        const userLoans = library.getUserLoans().slice(0, 5);
        
        if (userLoans.length === 0) {
            container.innerHTML = '<p>No recent activity</p>';
            return;
        }

        container.innerHTML = userLoans.map(loan => `
            <div class="activity-item">
                <strong>${loan.bookTitle}</strong><br>
                <small>${loan.status} on ${loan.issueDate.toLocaleDateString()}</small>
            </div>
        `).join('');
    }

    loadPopularBooks() {
        const container = document.getElementById('popular-books');
        const popularBooks = library.getPopularBooks();
        
        container.innerHTML = popularBooks.map(book => `
            <div class="book-item">
                <strong>${book.title}</strong><br>
                <small>by ${book.author} - Rating: ${book.rating ? book.rating.toFixed(1) : 'N/A'}</small>
            </div>
        `).join('');
    }

    loadBooks() {
        this.updateCategoryFilter();
        this.displayBooks(library.getAllBooks());
    }

    updateCategoryFilter() {
        const categoryFilter = document.getElementById('category-filter');
        const categories = library.getCategories();
        
        categoryFilter.innerHTML = '<option value="">All Categories</option>';
        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            categoryFilter.appendChild(option);
        });
    }

    searchBooks(query) {
        const books = library.searchBooks(query);
        this.displayBooks(books);
    }

    filterBooks() {
        const category = document.getElementById('category-filter').value;
        const availability = document.getElementById('availability-filter').value;
        
        let books = library.getAllBooks();
        
        if (category) {
            books = library.getBooksByCategory(category);
        }
        
        if (availability === 'available') {
            books = books.filter(book => book.availableCopies > 0);
        }
        
        this.displayBooks(books);
    }

    displayBooks(books) {
        const container = document.getElementById('books-grid');
        
        if (books.length === 0) {
            container.innerHTML = '<p>No books found.</p>';
            return;
        }

        container.innerHTML = books.map(book => `
            <div class="book-card" onclick="app.showBookDetails(${book.id})">
                <div class="book-cover">
                    <i class="fas fa-book fa-3x"></i>
                </div>
                <div class="book-info">
                    <h3>${book.title}</h3>
                    <p><strong>Author:</strong> ${book.author}</p>
                    <p><strong>Category:</strong> ${book.category}</p>
                    <p><strong>Year:</strong> ${book.year}</p>
                    ${book.rating ? `<p><strong>Rating:</strong> ${book.rating.toFixed(1)}/5</p>` : ''}
                </div>
                <div class="book-status">
                    <span class="availability ${book.availableCopies > 0 ? 'available' : 'unavailable'}">
                        ${book.availableCopies > 0 ? `${book.availableCopies} available` : 'Not available'}
                    </span>
                </div>
            </div>
        `).join('');
    }

    showBookDetails(bookId) {
        const book = library.getBookById(bookId);
        if (!book) return;

        library.selectedBook = book;
        
        // Populate modal
        document.getElementById('modal-book-title').textContent = book.title;
        document.getElementById('modal-book-author').textContent = book.author;
        document.getElementById('modal-book-category').textContent = book.category;
        document.getElementById('modal-book-publisher').textContent = book.publisher || 'N/A';
        document.getElementById('modal-book-year').textContent = book.year || 'N/A';
        document.getElementById('modal-book-available').textContent = `${book.availableCopies}/${book.totalCopies}`;
        document.getElementById('modal-book-description').textContent = book.description || 'No description available.';
        
        // Update issue button
        const issueBtn = document.getElementById('issue-book-btn');
        if (book.availableCopies > 0) {
            issueBtn.style.display = 'inline-flex';
            issueBtn.disabled = false;
        } else {
            issueBtn.style.display = 'none';
        }
        
        this.showModal('book-modal');
    }

    loadMyLoans() {
        const container = document.getElementById('loans-list');
        const loans = library.getUserLoans();
        
        if (loans.length === 0) {
            container.innerHTML = '<p>You have no loans.</p>';
            return;
        }

        container.innerHTML = loans.map(loan => {
            const isOverdue = loan.status === 'ISSUED' && new Date() > loan.dueDate;
            return `
                <div class="loan-card ${isOverdue ? 'overdue' : ''}">
                    <div class="loan-info">
                        <h4>${loan.bookTitle}</h4>
                        <p><strong>Author:</strong> ${loan.bookAuthor}</p>
                        <p><strong>Issue Date:</strong> ${loan.issueDate.toLocaleDateString()}</p>
                        <p><strong>Due Date:</strong> ${loan.dueDate.toLocaleDateString()}</p>
                        <p><strong>Status:</strong> ${loan.status}</p>
                        ${loan.fineAmount > 0 ? `<p><strong>Fine:</strong> $${loan.fineAmount.toFixed(2)}</p>` : ''}
                        ${isOverdue ? '<p style="color: #e74c3c;"><strong>OVERDUE!</strong></p>' : ''}
                    </div>
                    <div class="loan-actions">
                        ${loan.status === 'ISSUED' ? `
                            <button class="btn btn-primary" onclick="app.returnBook(${loan.id})">
                                <i class="fas fa-undo"></i> Return
                            </button>
                            <button class="btn btn-secondary" onclick="app.renewLoan(${loan.id})">
                                <i class="fas fa-refresh"></i> Renew
                            </button>
                        ` : ''}
                    </div>
                </div>
            `;
        }).join('');
    }

    loadRecommendations() {
        const container = document.getElementById('recommendations-grid');
        const recommendations = library.getRecommendations();
        
        if (recommendations.length === 0) {
            container.innerHTML = '<p>No recommendations available. Try reading some books first!</p>';
            return;
        }

        this.displayBooksInContainer(recommendations, container);
    }

    displayBooksInContainer(books, container) {
        container.innerHTML = books.map(book => `
            <div class="book-card" onclick="app.showBookDetails(${book.id})">
                <div class="book-cover">
                    <i class="fas fa-book fa-3x"></i>
                </div>
                <div class="book-info">
                    <h3>${book.title}</h3>
                    <p><strong>Author:</strong> ${book.author}</p>
                    <p><strong>Category:</strong> ${book.category}</p>
                    ${book.rating ? `<p><strong>Rating:</strong> ${book.rating.toFixed(1)}/5</p>` : ''}
                </div>
                <div class="book-status">
                    <span class="availability available">
                        ${book.availableCopies} available
                    </span>
                </div>
            </div>
        `).join('');
    }

    loadManageBooks() {
        if (library.currentUser.role !== 'ADMIN') return;
        
        const container = document.getElementById('manage-books-table');
        const books = library.getAllBooks();
        
        container.innerHTML = `
            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Author</th>
                        <th>Category</th>
                        <th>Available/Total</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${books.map(book => `
                        <tr>
                            <td>${book.id}</td>
                            <td>${book.title}</td>
                            <td>${book.author}</td>
                            <td>${book.category}</td>
                            <td>${book.availableCopies}/${book.totalCopies}</td>
                            <td>
                                <button class="btn btn-secondary btn-small" onclick="app.editBook(${book.id})">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-danger btn-small" onclick="app.deleteBook(${book.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }

    loadManageUsers() {
        if (library.currentUser.role !== 'ADMIN') return;
        
        const container = document.getElementById('users-table');
        const users = library.users;
        
        container.innerHTML = `
            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    ${users.map(user => `
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.fullName}</td>
                            <td>${user.email}</td>
                            <td>${user.role}</td>
                            <td>${user.isActive ? 'Active' : 'Inactive'}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }

    loadReports() {
        if (library.currentUser.role !== 'ADMIN') return;
        
        const stats = library.getStatistics();
        const overdueLoans = library.getOverdueLoans();
        const categories = library.getCategories();
        
        // Library statistics
        document.getElementById('library-stats').innerHTML = `
            <p><strong>Total Books:</strong> ${stats.totalBooks}</p>
            <p><strong>Active Loans:</strong> ${stats.activeLoans}</p>
            <p><strong>Overdue Loans:</strong> ${stats.overdueLoans}</p>
            <p><strong>Total Fines:</strong> $${stats.totalFines.toFixed(2)}</p>
        `;
        
        // Top categories
        const categoryStats = categories.map(category => {
            const books = library.getBooksByCategory(category);
            return { category, count: books.length };
        }).sort((a, b) => b.count - a.count);
        
        document.getElementById('top-categories').innerHTML = categoryStats.map(stat => `
            <p><strong>${stat.category}:</strong> ${stat.count} books</p>
        `).join('');
        
        // Overdue books
        document.getElementById('overdue-books').innerHTML = overdueLoans.length > 0 ? 
            overdueLoans.map(loan => `
                <p><strong>${loan.bookTitle}</strong> - ${loan.userName}</p>
            `).join('') : '<p>No overdue books</p>';
    }

    // Book operations
    issueBook() {
        if (!library.selectedBook) return;
        
        const result = library.issueBook(library.selectedBook.id);
        if (result.success) {
            this.showToast('Book issued successfully!', 'success');
            this.closeModal('book-modal');
            if (this.currentSection === 'books') {
                this.loadBooks();
            }
        } else {
            this.showToast(result.message, 'error');
        }
    }

    returnBook(loanId) {
        const result = library.returnBook(loanId);
        if (result.success) {
            this.showToast('Book returned successfully!', 'success');
            this.loadMyLoans();
        } else {
            this.showToast(result.message, 'error');
        }
    }

    renewLoan(loanId) {
        const result = library.renewLoan(loanId);
        if (result.success) {
            this.showToast('Loan renewed successfully!', 'success');
            this.loadMyLoans();
        } else {
            this.showToast(result.message, 'error');
        }
    }

    // Rating system
    setRating(rating) {
        library.selectedRating = rating;
        document.querySelectorAll('#star-rating i').forEach((star, index) => {
            if (index < rating) {
                star.classList.add('active');
            } else {
                star.classList.remove('active');
            }
        });
    }

    submitRating() {
        if (!library.selectedBook || library.selectedRating === 0) {
            this.showToast('Please select a rating', 'error');
            return;
        }

        const review = document.getElementById('book-review').value;
        const result = library.rateBook(library.selectedBook.id, library.selectedRating, review);
        
        if (result.success) {
            this.showToast('Rating submitted successfully!', 'success');
            document.getElementById('book-review').value = '';
            this.setRating(0);
        } else {
            this.showToast(result.message, 'error');
        }
    }

    // Admin functions
    showAddBookModal() {
        this.showModal('add-book-modal');
    }

    addBook() {
        const bookData = {
            isbn: document.getElementById('book-isbn').value,
            title: document.getElementById('book-title').value,
            author: document.getElementById('book-author').value,
            publisher: document.getElementById('book-publisher').value,
            year: parseInt(document.getElementById('book-year').value) || new Date().getFullYear(),
            category: document.getElementById('book-category').value,
            totalCopies: parseInt(document.getElementById('book-copies').value),
            description: document.getElementById('book-desc').value
        };

        const result = library.addBook(bookData);
        if (result.success) {
            this.showToast('Book added successfully!', 'success');
            this.closeModal('add-book-modal');
            document.getElementById('add-book-form').reset();
            if (this.currentSection === 'manage-books') {
                this.loadManageBooks();
            }
        } else {
            this.showToast(result.message, 'error');
        }
    }

    deleteBook(bookId) {
        if (confirm('Are you sure you want to delete this book?')) {
            const result = library.deleteBook(bookId);
            if (result.success) {
                this.showToast('Book deleted successfully!', 'success');
                this.loadManageBooks();
            } else {
                this.showToast(result.message, 'error');
            }
        }
    }

    // Utility functions
    showModal(modalId) {
        document.getElementById(modalId).classList.add('active');
    }

    closeModal(modalId) {
        document.getElementById(modalId).classList.remove('active');
    }

    showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;
        
        document.getElementById('toast-container').appendChild(toast);
        
        setTimeout(() => {
            toast.remove();
        }, 3000);
    }

    logout() {
        library.logout();
        this.showLogin();
        this.showToast('Logged out successfully!', 'success');
    }
}

// Global functions
function showRegister() {
    document.getElementById('login-modal').classList.remove('active');
    document.getElementById('register-modal').classList.add('active');
}

function showLogin() {
    document.getElementById('register-modal').classList.remove('active');
    document.getElementById('login-modal').classList.add('active');
}

function logout() {
    app.logout();
}

function showAddBookModal() {
    app.showAddBookModal();
}

function addBook() {
    app.addBook();
}

function closeModal(modalId) {
    app.closeModal(modalId);
}

function issueBook() {
    app.issueBook();
}

function submitRating() {
    app.submitRating();
}

// Initialize app
const app = new LibraryApp();