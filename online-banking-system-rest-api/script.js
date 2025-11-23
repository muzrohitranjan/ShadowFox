// Global variables
let currentUser = null;
let authToken = null;
let userAccounts = [];

// API Base URL
const API_BASE = 'http://localhost:8080';

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    checkAuthStatus();
});

// Auth functions
function showLogin() {
    document.getElementById('login-form').classList.remove('hidden');
    document.getElementById('register-form').classList.add('hidden');
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
}

function showRegister() {
    document.getElementById('register-form').classList.remove('hidden');
    document.getElementById('login-form').classList.add('hidden');
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
}

async function login(event) {
    event.preventDefault();
    
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            authToken = data.token;
            currentUser = data;
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            showDashboard();
            showMessage('Login successful!', 'success');
        } else {
            const error = await response.json();
            showMessage(error.message || 'Login failed', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    }
}

async function register(event) {
    event.preventDefault();
    
    const username = document.getElementById('register-username').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    
    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            authToken = data.token;
            currentUser = data;
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            showDashboard();
            showMessage('Registration successful!', 'success');
        } else {
            const error = await response.json();
            showMessage(error.message || 'Registration failed', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    }
}

function logout() {
    authToken = null;
    currentUser = null;
    userAccounts = [];
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    showAuthSection();
    showMessage('Logged out successfully', 'info');
}

function checkAuthStatus() {
    const token = localStorage.getItem('authToken');
    const user = localStorage.getItem('currentUser');
    
    if (token && user) {
        authToken = token;
        currentUser = JSON.parse(user);
        showDashboard();
    } else {
        showAuthSection();
    }
}

function showAuthSection() {
    document.getElementById('auth-section').classList.remove('hidden');
    document.getElementById('dashboard-section').classList.add('hidden');
}

function showDashboard() {
    document.getElementById('auth-section').classList.add('hidden');
    document.getElementById('dashboard-section').classList.remove('hidden');
    document.getElementById('user-name').textContent = currentUser.username;
    loadUserAccounts();
}

// Navigation functions
function showAccounts() {
    hideAllSections();
    document.getElementById('accounts-section').classList.remove('hidden');
    setActiveNav(event.target);
    loadUserAccounts();
}

function showTransfer() {
    hideAllSections();
    document.getElementById('transfer-section').classList.remove('hidden');
    setActiveNav(event.target);
    loadAccountsForTransfer();
}

function showTransactions() {
    hideAllSections();
    document.getElementById('transactions-section').classList.remove('hidden');
    setActiveNav(event.target);
    loadAccountsForTransactions();
}

function showCreateAccount() {
    hideAllSections();
    document.getElementById('create-account-section').classList.remove('hidden');
    setActiveNav(event.target);
}

function hideAllSections() {
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.add('hidden');
    });
}

function setActiveNav(activeBtn) {
    document.querySelectorAll('.nav-item').forEach(btn => btn.classList.remove('active'));
    activeBtn.classList.add('active');
}

// Account functions
async function loadUserAccounts() {
    try {
        const response = await fetch(`${API_BASE}/accounts/my-accounts`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            userAccounts = await response.json();
            displayAccounts();
        } else {
            showMessage('Failed to load accounts', 'error');
        }
    } catch (error) {
        showMessage('Network error loading accounts', 'error');
    }
}

function displayAccounts() {
    const accountsList = document.getElementById('accounts-list');
    
    if (userAccounts.length === 0) {
        accountsList.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-wallet"></i>
                <h3>No accounts found</h3>
                <p>Create your first account to get started!</p>
            </div>
        `;
        return;
    }
    
    accountsList.innerHTML = userAccounts.map(account => {
        const accountIcon = {
            'SAVINGS': 'fas fa-piggy-bank',
            'CURRENT': 'fas fa-briefcase', 
            'FIXED_DEPOSIT': 'fas fa-chart-line'
        }[account.accountType] || 'fas fa-university';
        
        return `
            <div class="account-card">
                <div class="account-header">
                    <div class="account-type">
                        <i class="${accountIcon}"></i>
                        ${account.accountType.replace('_', ' ')} Account
                    </div>
                </div>
                <div class="account-number">${account.accountNumber}</div>
                <div class="account-balance">$${parseFloat(account.balance).toFixed(2)}</div>
                <div class="account-created">Created: ${new Date(account.createdAt).toLocaleDateString()}</div>
            </div>
        `;
    }).join('');
}

async function createAccount(event) {
    event.preventDefault();
    
    const accountType = document.querySelector('input[name="account-type"]:checked')?.value;
    
    if (!accountType) {
        showMessage('Please select an account type', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/accounts/create`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ accountType })
        });
        
        if (response.ok) {
            showMessage('Account created successfully!', 'success');
            document.querySelectorAll('input[name="account-type"]').forEach(input => input.checked = false);
            loadUserAccounts();
        } else {
            const error = await response.json();
            showMessage(error.message || 'Failed to create account', 'error');
        }
    } catch (error) {
        showMessage('Network error creating account', 'error');
    }
}

// Transfer functions
function loadAccountsForTransfer() {
    const fromAccountSelect = document.getElementById('from-account');
    fromAccountSelect.innerHTML = '<option value="">Select your account</option>';
    
    userAccounts.forEach(account => {
        const option = document.createElement('option');
        option.value = account.accountNumber;
        option.textContent = `${account.accountType} - ${account.accountNumber} ($${parseFloat(account.balance).toFixed(2)})`;
        fromAccountSelect.appendChild(option);
    });
}

async function transferFunds(event) {
    event.preventDefault();
    
    const fromAccountNumber = document.getElementById('from-account').value;
    const toAccountNumber = document.getElementById('to-account').value;
    const amount = parseFloat(document.getElementById('amount').value);
    const description = document.getElementById('description').value;
    
    if (fromAccountNumber === toAccountNumber) {
        showMessage('Cannot transfer to the same account', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/transactions/transfer`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({
                fromAccountNumber,
                toAccountNumber,
                amount,
                description
            })
        });
        
        if (response.ok) {
            showMessage('Transfer completed successfully!', 'success');
            document.querySelector('.transfer-form').reset();
            loadUserAccounts(); // Refresh account balances
        } else {
            const error = await response.json();
            showMessage(error.message || 'Transfer failed', 'error');
        }
    } catch (error) {
        showMessage('Network error during transfer', 'error');
    }
}

// Transaction functions
function loadAccountsForTransactions() {
    const accountSelect = document.getElementById('account-select');
    accountSelect.innerHTML = '<option value="">Select Account</option>';
    
    userAccounts.forEach(account => {
        const option = document.createElement('option');
        option.value = account.accountId;
        option.textContent = `${account.accountType} - ${account.accountNumber}`;
        accountSelect.appendChild(option);
    });
}

async function loadTransactions() {
    const accountId = document.getElementById('account-select').value;
    
    if (!accountId) {
        showMessage('Please select an account', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/transactions/account/${accountId}`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            const transactions = await response.json();
            displayTransactions(transactions, accountId);
        } else {
            showMessage('Failed to load transactions', 'error');
        }
    } catch (error) {
        showMessage('Network error loading transactions', 'error');
    }
}

function displayTransactions(transactions, accountId) {
    const transactionsList = document.getElementById('transactions-list');
    
    if (transactions.length === 0) {
        transactionsList.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-receipt"></i>
                <h3>No transactions found</h3>
                <p>No transactions available for this account.</p>
            </div>
        `;
        return;
    }
    
    transactionsList.innerHTML = transactions.map(transaction => {
        const isCredit = transaction.toAccount.accountId == accountId;
        const otherAccount = isCredit ? transaction.fromAccount : transaction.toAccount;
        const amountClass = isCredit ? 'credit' : 'debit';
        const amountPrefix = isCredit ? '+' : '-';
        const typeIcon = {
            'TRANSFER': 'fas fa-exchange-alt',
            'DEPOSIT': 'fas fa-arrow-down',
            'WITHDRAWAL': 'fas fa-arrow-up'
        }[transaction.type] || 'fas fa-money-bill';
        
        return `
            <div class="transaction-item">
                <div class="transaction-details">
                    <h4><i class="${typeIcon}"></i> ${transaction.type}</h4>
                    <p><strong>${isCredit ? 'From' : 'To'}:</strong> ${otherAccount.accountNumber}</p>
                    <p><strong>Description:</strong> ${transaction.description || 'No description'}</p>
                    <p><strong>Date:</strong> ${new Date(transaction.timestamp).toLocaleString()}</p>
                    <p><strong>Status:</strong> <span class="status-${transaction.status.toLowerCase()}">${transaction.status}</span></p>
                </div>
                <div class="transaction-amount ${amountClass}">
                    ${amountPrefix}$${parseFloat(transaction.amount).toFixed(2)}
                </div>
            </div>
        `;
    }).join('');
}

// Utility functions
function showMessage(message, type) {
    const toastContainer = document.getElementById('toast-container');
    const toast = document.createElement('div');
    
    const icons = {
        'success': 'fas fa-check-circle',
        'error': 'fas fa-exclamation-circle',
        'info': 'fas fa-info-circle'
    };
    
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <i class="${icons[type] || 'fas fa-info-circle'}"></i>
        <span>${message}</span>
    `;
    
    toastContainer.appendChild(toast);
    
    setTimeout(() => toast.classList.add('show'), 100);
    
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Auto-populate from account when typing
document.addEventListener('DOMContentLoaded', function() {
    const fromAccountInput = document.getElementById('from-account');
    if (fromAccountInput) {
        fromAccountInput.addEventListener('input', function() {
            // This could be enhanced to show a dropdown of user's accounts
        });
    }
});