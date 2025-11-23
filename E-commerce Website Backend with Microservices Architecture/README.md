# E-Commerce Microservices Backend

A comprehensive e-commerce backend built using Spring Boot microservices architecture demonstrating distributed systems, scalability, and fault tolerance.

## Architecture Overview

This project implements a complete microservices-based e-commerce platform with the following services:

- **Eureka Server** - Service Discovery
- **Config Server** - Centralized Configuration
- **API Gateway** - Single Entry Point & Routing
- **Auth Service** - Authentication & Authorization (JWT)
- **Product Service** - Product Catalog Management
- **Inventory Service** - Stock Management
- **Order Service** - Order Processing
- **Payment Service** - Payment Processing
- **Notification Service** - Email/SMS Notifications

## Technologies Used

- Java 17+
- Spring Boot 3.x
- Spring Cloud (Gateway, Eureka, Config)
- MySQL/PostgreSQL/MongoDB
- Redis (Caching)
- RabbitMQ (Message Broker)
- JWT Authentication
- Docker & Docker Compose

## Quick Start

1. **Start Infrastructure Services:**
   ```bash
   # Start Eureka Server
   cd eureka-server && mvn spring-boot:run

   # Start Config Server
   cd config-server && mvn spring-boot:run

   # Start API Gateway
   cd api-gateway && mvn spring-boot:run
   ```

2. **Start Business Services:**
   ```bash
   # Start all services
   cd auth-service && mvn spring-boot:run
   cd product-service && mvn spring-boot:run
   cd inventory-service && mvn spring-boot:run
   cd order-service && mvn spring-boot:run
   cd payment-service && mvn spring-boot:run
   cd notification-service && mvn spring-boot:run
   ```

## Service Ports

- Eureka Server: 8761
- Config Server: 8888
- API Gateway: 8080
- Auth Service: 8081
- Product Service: 8082
- Inventory Service: 8083
- Order Service: 8084
- Payment Service: 8085
- Notification Service: 8086

## API Endpoints

### Authentication
- POST `/auth/register` - User Registration
- POST `/auth/login` - User Login

### Products
- GET `/products` - Get All Products
- GET `/products/{id}` - Get Product by ID
- POST `/products` - Create Product (Admin)

### Orders
- POST `/orders` - Place Order
- GET `/orders/user/{userId}` - Get User Orders

### Payments
- POST `/payments` - Process Payment

## Database Schema

Each service maintains its own database:
- Auth Service: MySQL (users, roles)
- Product Service: MongoDB (products, categories)
- Inventory Service: MySQL (stock levels)
- Order Service: PostgreSQL (orders, order items)
- Payment Service: Redis (payment transactions)

## Features Implemented

✅ Service Discovery with Eureka
✅ API Gateway with Routing
✅ JWT Authentication & Authorization
✅ Inter-service Communication (REST)
✅ Asynchronous Messaging (RabbitMQ)
✅ Database per Service Pattern
✅ Circuit Breaker Pattern
✅ Distributed Configuration
✅ Load Balancing
✅ Fault Tolerance

## Testing

Use Postman collection or curl commands to test the APIs through the API Gateway at `http://localhost:8080`

## Docker Deployment

```bash
docker-compose up -d
```

## Architecture Benefits

- **Scalability**: Each service can be scaled independently
- **Fault Tolerance**: Service failures don't bring down the entire system
- **Technology Diversity**: Each service can use different technologies
- **Independent Deployment**: Services can be deployed independently
- **Team Autonomy**: Different teams can work on different services