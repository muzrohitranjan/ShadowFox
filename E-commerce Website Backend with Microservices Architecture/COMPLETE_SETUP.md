# Complete E-Commerce Microservices Setup

## 🚀 Full Stack E-Commerce Application

### Backend: Spring Boot Microservices
### Frontend: Vanilla JavaScript SPA

---

## 📋 Quick Start Guide

### 1. **Start Infrastructure Services**
```bash
# Option 1: Docker (Recommended)
docker-compose up -d mysql postgres mongodb redis rabbitmq

# Option 2: Install locally
# MySQL (Port: 3306)
# PostgreSQL (Port: 5432) 
# MongoDB (Port: 27017)
# Redis (Port: 6379)
# RabbitMQ (Port: 5672, Management: 15672)
```

### 2. **Start Backend Microservices**
```bash
# Run the batch script (starts all services in order)
start-services.bat

# Or start manually:
cd eureka-server && mvn spring-boot:run
cd config-server && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
cd auth-service && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

### 3. **Start Frontend**
```bash
# Run the frontend server
run-frontend.bat

# Or manually:
cd frontend
python -m http.server 3000
```

### 4. **Access Application**
- **Customer App**: http://localhost:3000
- **Admin Panel**: http://localhost:3000/admin.html
- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (admin/admin)

---

## 🎯 Complete User Journey

### **Customer Experience**

1. **Registration/Login**
   - Open http://localhost:3000
   - Register new account or login
   - JWT token automatically managed

2. **Browse Products**
   - View product catalog
   - Search products by name
   - Filter by categories

3. **Shopping Cart**
   - Add products to cart
   - Manage quantities
   - View cart total

4. **Checkout Process**
   - Place order
   - Automatic payment processing
   - Order confirmation
   - Email notifications (check service logs)

5. **Order Tracking**
   - View order history
   - Track order status
   - Payment status updates

### **Admin Experience**

1. **Product Management**
   - Access http://localhost:3000/admin.html
   - Add new products
   - Manage product inventory
   - Activate/deactivate products

---

## 🏗️ Architecture Flow

```
Frontend (Port 3000)
    ↓
API Gateway (Port 8080) 
    ↓
┌─────────────────────────────────────┐
│           Microservices             │
├─────────────────────────────────────┤
│ Auth Service (8081) ← MySQL         │
│ Product Service (8082) ← MongoDB    │
│ Order Service (8084) ← PostgreSQL   │
│ Payment Service (8085) ← Redis      │
│ Notification Service (8086)         │
└─────────────────────────────────────┘
    ↓
Service Discovery (Eureka - 8761)
    ↓
Message Broker (RabbitMQ - 5672)
```

---

## 📊 Service Status Check

### **Verify All Services Running**
```bash
# Check Eureka Dashboard
http://localhost:8761

# Health Checks
curl http://localhost:8080/auth/health
curl http://localhost:8080/products/health
curl http://localhost:8080/orders/health
curl http://localhost:8080/payments/health
```

### **Expected Services in Eureka**
- AUTH-SERVICE
- PRODUCT-SERVICE  
- ORDER-SERVICE
- PAYMENT-SERVICE
- NOTIFICATION-SERVICE
- API-GATEWAY
- CONFIG-SERVER

---

## 🧪 Testing the Complete Flow

### **1. Test User Registration**
```javascript
// Frontend automatically handles this
// Or test via API:
POST http://localhost:8080/auth/register
{
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User"
}
```

### **2. Test Product Creation (Admin)**
```javascript
// Via admin panel or API:
POST http://localhost:8080/products
Authorization: Bearer {token}
{
  "name": "iPhone 15",
  "description": "Latest iPhone",
  "price": 999.99,
  "category": "Electronics"
}
```

### **3. Test Order Flow**
1. Add products to cart (frontend)
2. Checkout (creates order)
3. Payment processing (automatic)
4. Check notifications in service logs
5. View order in "My Orders" section

---

## 📱 Frontend Features

### **Customer Interface**
✅ User authentication (register/login)
✅ Product browsing and search
✅ Shopping cart management  
✅ Order placement and tracking
✅ Payment processing
✅ Responsive design

### **Admin Interface**
✅ Product management
✅ Inventory control
✅ Product activation/deactivation

---

## 🔧 Troubleshooting

### **Common Issues**

1. **Services not starting**
   - Check if ports are available
   - Ensure databases are running
   - Check Java version (17+)

2. **Frontend not connecting**
   - Verify API Gateway is running (8080)
   - Check CORS configuration
   - Ensure all services registered in Eureka

3. **Database connection errors**
   - Verify database credentials in application.yml
   - Check if databases are running
   - Create databases if they don't exist

4. **RabbitMQ connection issues**
   - Ensure RabbitMQ is running
   - Check credentials (admin/admin)
   - Verify queues are created

### **Service Logs**
Check individual service console outputs for detailed error messages and event processing logs.

---

## 🎉 Success Indicators

✅ All 8 services registered in Eureka
✅ Frontend loads without errors
✅ User can register and login
✅ Products display correctly
✅ Orders can be placed successfully
✅ Payments process automatically
✅ Notifications appear in service logs
✅ Admin can manage products

---

## 📈 Production Deployment

For production deployment:
- Use Docker containers
- Deploy to Kubernetes
- Add proper monitoring (Prometheus/Grafana)
- Implement distributed tracing
- Add proper logging aggregation
- Use external databases
- Implement proper security measures
- Add CI/CD pipelines

---

**🎯 Complete E-Commerce Microservices Application Ready!**

This demonstrates enterprise-grade microservices architecture with a functional frontend, suitable for learning and as a foundation for production systems.