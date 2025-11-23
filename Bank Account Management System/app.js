// UI Management
let currentAccount = null;

// Navigation
function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    document.getElementById(sectionId).classList.add('active');
    event.target.classList.add('active');
    
    if (sectionId === 'account-list') {
        loadAllAccounts();
    }
    updateAccountSelectors();
}

// Message Display
function showMessage(text, type = 'success') {
    const messageEl = document.getElementById('message');
    messageEl.textContent = text;
    messageEl.className = `message ${type} show`;
    
    setTimeout(() => {
        messageEl.classList.remove('show');
    }, 3000);
}

// Account Creation
document.getElementById('create-account-form').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const accountHolder = document.getElementById('account-holder').value;
    const initialBalance = parseFloat(document.getElementById('initial-balance').value);
    
    const account = bankingSystem.createAccount(accountHolder, initialBalance);
    
    if (account) {
        showMessage(`Account created successfully! Account Number: ${account.accountNumber}`);
        this.reset();
        updateAccountSelectors();
    } else {
        showMessage('Failed to create account. Check your inputs.', 'error');
    }
});

// Account Operations
function updateAccountSelectors() {
    const selectors = ['operation-account-select', 'history-account-select', 'transfer-target'];
    const accounts = bankingSystem.getAllAccounts();
    
    selectors.forEach(selectorId => {
        const select = document.getElementById(selectorId);
        const currentValue = select.value;
        select.innerHTML = '<option value="">Select Account</option>';
        
        accounts.forEach(account => {
            const option = document.createElement('option');
            option.value = account.accountNumber;
            option.textContent = `${account.accountNumber} - ${account.accountHolder}`;
            select.appendChild(option);
        });
        
        if (currentValue) select.value = currentValue;
    });
}

function loadAccountInfo() {
    const accountNumber = document.getElementById('operation-account-select').value;
    if (!accountNumber) return;
    
    currentAccount = bankingSystem.getAccount(accountNumber);
    if (currentAccount) {
        document.getElementById('current-balance').textContent = currentAccount.balance.toFixed(2);
        document.getElementById('account-info').style.display = 'block';
        updateTransferTargets();
    }
}

function updateTransferTargets() {
    const transferSelect = document.getElementById('transfer-target');
    const accounts = bankingSystem.getAllAccounts();
    
    transferSelect.innerHTML = '<option value="">Select Target Account</option>';
    
    accounts.forEach(account => {
        if (account.accountNumber !== currentAccount.accountNumber) {
            const option = document.createElement('option');
            option.value = account.accountNumber;
            option.textContent = `${account.accountNumber} - ${account.accountHolder}`;
            transferSelect.appendChild(option);
        }
    });
}

function deposit() {
    if (!currentAccount) return;
    
    const amount = parseFloat(document.getElementById('deposit-amount').value);
    if (isNaN(amount) || amount <= 0) {
        showMessage('Please enter a valid amount', 'error');
        return;
    }
    
    if (currentAccount.deposit(amount)) {
        document.getElementById('current-balance').textContent = currentAccount.balance.toFixed(2);
        document.getElementById('deposit-amount').value = '';
        bankingSystem.saveToStorage();
        showMessage(`Deposited $${amount.toFixed(2)} successfully!`);
    } else {
        showMessage('Deposit failed', 'error');
    }
}

function withdraw() {
    if (!currentAccount) return;
    
    const amount = parseFloat(document.getElementById('withdraw-amount').value);
    if (isNaN(amount) || amount <= 0) {
        showMessage('Please enter a valid amount', 'error');
        return;
    }
    
    if (currentAccount.withdraw(amount)) {
        document.getElementById('current-balance').textContent = currentAccount.balance.toFixed(2);
        document.getElementById('withdraw-amount').value = '';
        bankingSystem.saveToStorage();
        showMessage(`Withdrew $${amount.toFixed(2)} successfully!`);
    } else {
        showMessage('Insufficient funds or invalid amount', 'error');
    }
}

function transfer() {
    if (!currentAccount) return;
    
    const targetAccountNumber = document.getElementById('transfer-target').value;
    const amount = parseFloat(document.getElementById('transfer-amount').value);
    
    if (!targetAccountNumber) {
        showMessage('Please select a target account', 'error');
        return;
    }
    
    if (isNaN(amount) || amount <= 0) {
        showMessage('Please enter a valid amount', 'error');
        return;
    }
    
    const targetAccount = bankingSystem.getAccount(targetAccountNumber);
    
    if (currentAccount.transfer(targetAccount, amount)) {
        document.getElementById('current-balance').textContent = currentAccount.balance.toFixed(2);
        document.getElementById('transfer-amount').value = '';
        bankingSystem.saveToStorage();
        showMessage(`Transferred $${amount.toFixed(2)} successfully!`);
    } else {
        showMessage('Transfer failed. Check amount and balances.', 'error');
    }
}

function calculateInterest() {
    if (!currentAccount) return;
    
    const interest = currentAccount.calculateInterest();
    document.getElementById('current-balance').textContent = currentAccount.balance.toFixed(2);
    bankingSystem.saveToStorage();
    
    if (interest > 0) {
        showMessage(`Interest calculated! Earned $${interest.toFixed(2)}`);
    } else {
        showMessage('No interest earned (balance must be positive)', 'error');
    }
}

// Transaction History
function loadTransactionHistory() {
    const accountNumber = document.getElementById('history-account-select').value;
    if (!accountNumber) return;
    
    const account = bankingSystem.getAccount(accountNumber);
    if (!account) return;
    
    const transactionList = document.getElementById('transaction-list');
    transactionList.innerHTML = '';
    
    if (account.transactionHistory.length === 0) {
        transactionList.innerHTML = '<p>No transactions found.</p>';
        return;
    }
    
    account.transactionHistory.slice().reverse().forEach(transaction => {
        const div = document.createElement('div');
        div.className = 'transaction-item';
        
        const typeClass = transaction.type.toLowerCase().replace('_', '-');
        
        div.innerHTML = `
            <div class="transaction-type ${typeClass}">${transaction.type}</div>
            <div class="transaction-amount ${typeClass}">$${transaction.amount.toFixed(2)}</div>
            <div>Balance After: $${transaction.balanceAfter.toFixed(2)}</div>
            <div>${transaction.description}</div>
            <div style="font-size: 0.9em; color: #666;">${transaction.timestamp.toLocaleString()}</div>
        `;
        
        transactionList.appendChild(div);
    });
}

// Account List
function loadAllAccounts() {
    const accounts = bankingSystem.getAllAccounts();
    const tableDiv = document.getElementById('accounts-table');
    
    if (accounts.length === 0) {
        tableDiv.innerHTML = '<p>No accounts found.</p>';
        return;
    }
    
    let html = `
        <div class="table-header">
            <div>Account Number</div>
            <div>Account Holder</div>
            <div>Balance</div>
            <div>Transactions</div>
        </div>
    `;
    
    accounts.forEach(account => {
        html += `
            <div class="table-row">
                <div>${account.accountNumber}</div>
                <div>${account.accountHolder}</div>
                <div>$${account.balance.toFixed(2)}</div>
                <div>${account.transactionHistory.length}</div>
            </div>
        `;
    });
    
    tableDiv.innerHTML = html;
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    updateAccountSelectors();
    loadAllAccounts();
});