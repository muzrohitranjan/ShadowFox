@echo off
echo Starting E-Commerce Microservices...

echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
timeout /t 30

echo Starting Config Server...
start "Config Server" cmd /k "cd config-server && mvn spring-boot:run"
timeout /t 20

echo Starting API Gateway...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"
timeout /t 20

echo Starting Auth Service...
start "Auth Service" cmd /k "cd auth-service && mvn spring-boot:run"
timeout /t 15

echo Starting Product Service...
start "Product Service" cmd /k "cd product-service && mvn spring-boot:run"
timeout /t 15

echo Starting Order Service...
start "Order Service" cmd /k "cd order-service && mvn spring-boot:run"
timeout /t 15

echo Starting Payment Service...
start "Payment Service" cmd /k "cd payment-service && mvn spring-boot:run"
timeout /t 15

echo Starting Notification Service...
start "Notification Service" cmd /k "cd notification-service && mvn spring-boot:run"

echo All services are starting up...
echo Check Eureka Dashboard at: http://localhost:8761
echo API Gateway is available at: http://localhost:8080
pause