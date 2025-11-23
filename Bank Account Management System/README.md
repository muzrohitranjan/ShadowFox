# Bank Account Management System

A comprehensive banking system with both Java backend (with JUnit testing) and web frontend.

## Features

### Core Banking Operations
- ✅ Create bank accounts with multiple types
- ✅ Deposit money with daily limits
- ✅ Withdraw money (with overdraft support)
- ✅ Check account balance
- ✅ Transfer money between accounts
- ✅ Calculate monthly interest
- ✅ View transaction history

### Advanced Features
- ✅ **Multiple Account Types**: Savings, Checking, Premium, Business
- ✅ **Loan Management**: Create loans, payment tracking, amortization
- ✅ **Fee System**: Overdraft fees, transfer fees, minimum balance fees
- ✅ **Fraud Protection**: Daily transaction limits, account freezing
- ✅ **Advanced Interest**: Type-based rates (0.1% - 3.5%)
- ✅ **System Analytics**: High-value accounts, balance reporting
- ✅ **Account Management**: Freeze/unfreeze, age tracking
- ✅ **Web-based user interface**
- ✅ **Local storage persistence**

## Project Structure

```
BankAccountSystem/
├── src/
│   ├── main/java/          # Java source code
│   │   ├── BankAccount.java
│   │   ├── Transaction.java
│   │   ├── BankingSystem.java
│   │   └── BankingApp.java
│   └── test/java/          # JUnit test cases
│       ├── BankAccountTest.java
│       ├── BankingSystemTest.java
│       └── TransactionTest.java
├── frontend/               # Web frontend
│   ├── index.html
│   ├── css/style.css
│   └── js/
│       ├── bankingSystem.js
│       └── app.js
└── pom.xml                # Maven configuration
```

## Running the Application

### Java Console Application
```bash
cd BankAccountSystem
javac -cp src/main/java src/main/java/*.java
java -cp src/main/java BankingApp
```

### Web Frontend
1. Open `frontend/index.html` in a web browser
2. Start creating accounts and performing banking operations

### Running Tests
```bash
# With Maven
mvn test

# Manual compilation and testing
javac -cp "junit-platform-console-standalone-1.9.2.jar" src/test/java/*.java src/main/java/*.java
java -cp "junit-platform-console-standalone-1.9.2.jar:." org.junit.platform.console.ConsoleLauncher --scan-classpath
```

## Test Coverage (100+ Test Cases)

### BankAccount Tests (40+ test cases)
- **Deposit Tests**: Valid amounts, daily limits, frozen accounts
- **Withdrawal Tests**: Overdraft scenarios, fees, daily limits
- **Balance Inquiry**: Various balance states and fee calculations
- **Transaction History**: History tracking and immutability
- **Interest Calculation**: Type-based rates, minimum balance fees
- **Transfer Operations**: Fee calculations, premium account benefits
- **Advanced Features**: Account freezing, daily limits, fee tracking

### BankingSystem Tests (30+ test cases)
- **Account Creation**: Multiple types, minimum balance validation
- **Loan Management**: Loan creation, payment processing
- **System Analytics**: High-value accounts, type filtering
- **Account Management**: Advanced querying and reporting

### LoanAccount Tests (25+ test cases)
- **Loan Creation**: Payment calculation, various terms
- **Payment Processing**: Interest/principal breakdown, overpayments
- **Loan Status**: Active/inactive states, payment history

### AccountType Tests (5+ test cases)
- **Type Properties**: Interest rates, limits, minimum balances

## Key Learning Outcomes

1. **Advanced OOP**: Enums, composition, complex inheritance hierarchies
2. **Financial Algorithms**: Loan amortization, compound interest, fee calculations
3. **Unit Testing**: JUnit 5, comprehensive test coverage (100+ tests), edge case testing
4. **Software Architecture**: Multi-layered design, service patterns, data modeling
5. **Business Logic**: Banking regulations, fraud detection, account management
6. **Web Development**: Responsive design, state management, user experience
7. **Data Structures**: Collections, streams, filtering, aggregation
8. **Error Handling**: Comprehensive validation, business rule enforcement

## Banking Business Logic

### Account Types & Features

| Account Type | Interest Rate | Min Balance | Overdraft Limit | Special Features |
|--------------|---------------|-------------|-----------------|------------------|
| **Savings**  | 2.5%         | $1,000      | $0              | High interest, no overdraft |
| **Checking** | 0.1%         | $500        | $500            | Basic account with overdraft |
| **Premium**  | 3.5%         | $5,000      | $2,000          | No transfer fees, high limits |
| **Business** | 1.5%         | $10,000     | $5,000          | High transaction limits |

### Advanced Banking Logic
- **Daily Limits**: 10 transactions/day (50 for business), amount limits by type
- **Fee Structure**: $35 overdraft fee, $3 transfer fee (waived for premium), $25 minimum balance fee
- **Loan System**: Amortized payments, interest/principal breakdown
- **Fraud Detection**: Transaction pattern monitoring, account freezing

### Security Features
- Input validation for all operations
- Balance verification before withdrawals
- Account existence checks
- Immutable transaction records

## Technologies Used

### Backend (Java)
- Java 11+
- JUnit 5 for testing
- Maven for build management
- JaCoCo for test coverage

### Frontend (Web)
- HTML5 for structure
- CSS3 with Flexbox/Grid for responsive design
- Vanilla JavaScript (ES6+)
- LocalStorage for data persistence

## Advanced Features Implemented

- ✅ **Multiple Account Types** with different rules and benefits
- ✅ **Comprehensive Loan System** with amortization calculations
- ✅ **Advanced Fee Structure** with type-based fee waivers
- ✅ **Fraud Detection** with daily limits and account freezing
- ✅ **System Analytics** with filtering and reporting
- ✅ **Business Logic Validation** with comprehensive error handling

## Future Enhancements

- [ ] Credit scoring and loan approval algorithms
- [ ] Investment accounts with portfolio management
- [ ] Multi-currency support with exchange rates
- [ ] Mobile app with biometric authentication
- [ ] Blockchain integration for transaction verification
- [ ] AI-powered fraud detection and spending analysis