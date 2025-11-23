# API Testing Guide

## Prerequisites
1. Start all infrastructure services (MySQL, PostgreSQL, MongoDB, Redis, RabbitMQ)
2. Start all microservices using `start-services.bat`
3. Wait for all services to register with Eureka (check http://localhost:8761)

## API Endpoints (via API Gateway - http://localhost:8080)

### 1. User Registration
```bash
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

### 2. User Login
```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}
```
**Response:** Save the `token` for subsequent requests

### 3. Create Product (Admin)
```bash
POST http://localhost:8080/products
Content-Type: application/json

{
  "name": "iPhone 15",
  "description": "Latest iPhone model",
  "price": 999.99,
  "category": "Electronics",
  "imageUrl": "https://example.com/iphone15.jpg"
}
```

### 4. Get All Products
```bash
GET http://localhost:8080/products
```

### 5. Get Product by ID
```bash
GET http://localhost:8080/products/{productId}
```

### 6. Search Products
```bash
GET http://localhost:8080/products/search?name=iPhone
```

### 7. Create Order (Authenticated)
```bash
POST http://localhost:8080/orders
Content-Type: application/json
Authorization: Bearer {your-jwt-token}

{
  "items": [
    {
      "productId": "{productId}",
      "quantity": 2
    }
  ]
}
```

### 8. Get User Orders (Authenticated)
```bash
GET http://localhost:8080/orders/user/{userId}
Authorization: Bearer {your-jwt-token}
```

### 9. Process Payment (Authenticated)
```bash
POST http://localhost:8080/payments
Content-Type: application/json
Authorization: Bearer {your-jwt-token}

{
  "orderId": 1,
  "amount": 1999.98,
  "paymentMethod": "CREDIT_CARD",
  "cardNumber": "4111111111111111",
  "expiryDate": "12/25",
  "cvv": "123"
}
```

## Service Health Checks
- Eureka Dashboard: http://localhost:8761
- Auth Service: http://localhost:8080/auth/health
- Product Service: http://localhost:8080/products/health
- Order Service: http://localhost:8080/orders/health
- Payment Service: http://localhost:8080/payments/health

## Testing Flow
1. Register a new user
2. Login to get JWT token
3. Create some products
4. Browse products
5. Create an order
6. Process payment for the order
7. Check notifications in service logs

## Postman Collection
Import the following endpoints into Postman for easier testing:
- Set base URL as `http://localhost:8080`
- Add Authorization header with Bearer token for protected endpoints
- Use environment variables for token and user ID