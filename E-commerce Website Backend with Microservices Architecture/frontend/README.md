# E-Commerce Frontend

Simple frontend for the E-Commerce Microservices backend.

## Features

### Customer Interface (index.html)
- User registration and login
- Product browsing and search
- Shopping cart management
- Order placement and tracking
- Payment processing

### Admin Interface (admin.html)
- Product management
- Add new products
- Activate/deactivate products

## Setup

1. **Start Backend Services**
   ```bash
   # From project root
   start-services.bat
   ```

2. **Serve Frontend**
   ```bash
   # Option 1: Python HTTP Server
   cd frontend
   python -m http.server 3000

   # Option 2: Node.js HTTP Server
   npx http-server -p 3000

   # Option 3: Live Server (VS Code Extension)
   # Right-click index.html -> Open with Live Server
   ```

3. **Access Application**
   - Customer: http://localhost:3000
   - Admin: http://localhost:3000/admin.html

## Usage Flow

### Customer Journey
1. **Register/Login**
   - Create account or login with existing credentials
   - JWT token stored in localStorage

2. **Browse Products**
   - View all products
   - Search by product name
   - Products loaded from Product Service

3. **Shopping Cart**
   - Add products to cart
   - Manage quantities
   - Cart stored in localStorage

4. **Checkout**
   - Place order through Order Service
   - Automatic payment processing via Payment Service
   - Order confirmation and tracking

### Admin Journey
1. **Login** (same credentials as customer)
2. **Product Management**
   - Add new products
   - Manage product status
   - View all products

## API Integration

The frontend integrates with all microservices through the API Gateway:

- **Auth Service**: User authentication
- **Product Service**: Product catalog
- **Order Service**: Order management
- **Payment Service**: Payment processing
- **Notification Service**: Event-driven notifications

## Configuration

- API Base URL: `http://localhost:8080` (API Gateway)
- All requests routed through API Gateway
- JWT authentication for protected endpoints
- CORS enabled on backend services

## Sample Data

### Test User
```json
{
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User"
}
```

### Sample Products
Create products via admin panel or API:
```json
{
  "name": "iPhone 15",
  "description": "Latest iPhone model",
  "price": 999.99,
  "category": "Electronics",
  "imageUrl": "https://via.placeholder.com/200"
}
```

## Features Demonstrated

✅ **Authentication Flow**
- JWT token management
- Protected routes
- User session persistence

✅ **Product Management**
- CRUD operations
- Search functionality
- Category filtering

✅ **Shopping Cart**
- Add/remove items
- Quantity management
- Local storage persistence

✅ **Order Processing**
- Order creation
- Payment integration
- Order tracking

✅ **Admin Functions**
- Product management
- Inventory control

## Technology Stack

- **HTML5** - Structure
- **CSS3** - Styling (Flexbox/Grid)
- **Vanilla JavaScript** - Logic
- **Fetch API** - HTTP requests
- **LocalStorage** - Client-side storage

## Production Considerations

For production deployment:
- Add proper error handling
- Implement loading states
- Add form validation
- Use a frontend framework (React/Vue/Angular)
- Add proper routing
- Implement proper state management
- Add unit tests
- Optimize for mobile devices