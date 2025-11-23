# Online Banking System

A comprehensive online banking system built with Spring Boot backend and vanilla JavaScript frontend.

## Features

### Authentication & Security
- User registration and login
- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control
- Secure API endpoints

### Account Management
- Create multiple bank accounts (Savings, Current, Fixed Deposit)
- View account details and balances
- Auto-generated unique account numbers
- Account type management

### Fund Transfer
- Transfer money between accounts
- Real-time balance updates
- Transaction validation
- Insufficient balance checks
- Transfer descriptions

### Transaction History
- Complete transaction logs
- Filter by account
- Date range filtering
- Transaction status tracking
- Credit/Debit categorization

### Frontend Features
- Responsive web interface
- Real-time updates
- User-friendly dashboard
- Account overview
- Transaction management
- Error handling and notifications

## Technology Stack

### Backend
- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database operations
- **Hibernate** - ORM
- **MySQL** - Database
- **JWT** - Token-based authentication
- **Maven** - Dependency management
- **Swagger** - API documentation

### Frontend
- **HTML5**
- **CSS3** - Responsive design
- **Vanilla JavaScript** - Frontend logic
- **Fetch API** - HTTP requests

## Project Structure

```
online-banking-system/
├── src/
│   └── main/
│       ├── java/com/banking/
│       │   ├── OnlineBankingApplication.java
│       │   ├── config/
│       │   │   └── SecurityConfig.java
│       │   ├── controller/
│       │   │   ├── AuthController.java
│       │   │   ├── AccountController.java
│       │   │   ├── TransactionController.java
│       │   │   └── HomeController.java
│       │   ├── dto/
│       │   │   ├── LoginRequest.java
│       │   │   ├── RegisterRequest.java
│       │   │   ├── TransferRequest.java
│       │   │   ├── AccountRequest.java
│       │   │   └── JwtResponse.java
│       │   ├── entity/
│       │   │   ├── User.java
│       │   │   ├── Account.java
│       │   │   └── Transaction.java
│       │   ├── exception/
│       │   │   ├── CustomExceptions.java
│       │   │   └── GlobalExceptionHandler.java
│       │   ├── repository/
│       │   │   ├── UserRepository.java
│       │   │   ├── AccountRepository.java
│       │   │   └── TransactionRepository.java
│       │   ├── service/
│       │   │   ├── UserService.java
│       │   │   ├── AccountService.java
│       │   │   ├── TransactionService.java
│       │   │   └── CustomUserDetailsService.java
│       │   └── util/
│       │       └── JwtUtil.java
│       └── resources/
│           ├── application.properties
│           └── static/
│               ├── index.html
│               ├── style.css
│               └── script.js
└── pom.xml
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- MySQL 8.0+
- Maven 3.6+

### Database Setup
1. Install MySQL and create a database:
```sql
CREATE DATABASE banking_system;
```

2. Update database credentials in `application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Running the Application

1. **Clone the repository**
```bash
git clone <repository-url>
cd online-banking-system
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

4. **Access the application**
- Frontend: http://localhost:8080
- API Documentation: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - User login

### Account Management
- `POST /accounts/create` - Create new account
- `GET /accounts/{id}` - Get account by ID
- `GET /accounts/my-accounts` - Get user's accounts
- `GET /accounts/number/{accountNumber}` - Get account by number

### Transactions
- `POST /transactions/transfer` - Transfer funds
- `POST /transactions/deposit` - Deposit funds
- `GET /transactions/account/{accountId}` - Get account transactions
- `GET /transactions/account/{accountId}/date-range` - Get transactions by date range

## Usage Guide

### 1. User Registration
- Open the application in browser
- Click "Register" tab
- Fill in username, email, and password
- Click "Register" button

### 2. Login
- Enter username and password
- Click "Login" button
- You'll be redirected to the dashboard

### 3. Create Account
- Navigate to "Create Account" section
- Select account type (Savings/Current/Fixed Deposit)
- Click "Create Account"

### 4. Transfer Funds
- Go to "Transfer Funds" section
- Select source account
- Enter destination account number
- Enter amount and description
- Click "Transfer"

### 5. View Transactions
- Navigate to "Transaction History"
- Select an account
- Click "Load Transactions"
- View complete transaction history

## Security Features

- **JWT Authentication**: Secure token-based authentication
- **Password Encryption**: BCrypt hashing for passwords
- **Input Validation**: Server-side validation for all inputs
- **Error Handling**: Comprehensive error handling and user feedback
- **CORS Configuration**: Proper cross-origin resource sharing setup

## Database Schema

### Users Table
- user_id (Primary Key)
- username (Unique)
- email (Unique)
- password (Encrypted)
- role (USER/ADMIN)
- created_at

### Accounts Table
- account_id (Primary Key)
- account_number (Unique)
- account_type (SAVINGS/CURRENT/FIXED_DEPOSIT)
- balance
- user_id (Foreign Key)
- created_at

### Transactions Table
- transaction_id (Primary Key)
- from_account_id (Foreign Key)
- to_account_id (Foreign Key)
- amount
- type (TRANSFER/DEPOSIT/WITHDRAWAL)
- description
- timestamp
- status (PENDING/COMPLETED/FAILED)

## Testing

### Manual Testing with Postman
1. Import the API endpoints
2. Test authentication endpoints
3. Test account creation
4. Test fund transfers
5. Test transaction history

### Frontend Testing
1. Test user registration and login
2. Test account creation
3. Test fund transfers
4. Test transaction viewing
5. Test responsive design

## Future Enhancements

- Mobile application
- Email notifications
- SMS alerts
- Loan management
- Credit card integration
- Bill payment system
- Investment portfolio
- Multi-currency support
- Advanced reporting
- Admin dashboard
- Fraud detection
- Two-factor authentication

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please create an issue in the repository.