# 🛒 OrderHub - E-Commerce Microservices

OrderHub is a scalable e-commerce backend built using Java Spring Boot and a Microservices Architecture. The application
demonstrates industry-standard backend development practices including service discovery, API Gateway, JWT
authentication, asynchronous communication with RabbitMQ, Docker containerization, and MySQL databases.

The project is designed to simulate a real-world online shopping platform where users can browse products, place orders,
make payments, and receive email notifications.

---

## 🚀 Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL
- RabbitMQ
- Spring Cloud Gateway
- Eureka Service Registry
- Docker & Docker Compose
- Maven
- JWT Authentication
- Lombok

---

# 🏗️ Microservices

| Service         | Description                                                     |
|-----------------|-----------------------------------------------------------------|
| User Service    | User registration, login, JWT authentication, role management   |
| Product Service | Product CRUD operations and inventory management                |
| Order Service   | Places orders and coordinates with other services               |
| Payment Service | Handles payment processing                                      |
| Email Service   | Sends order confirmation and notification emails asynchronously |
| API Gateway     | Single entry point for all client requests                      |
| Eureka Server   | Service Discovery for all microservices                         |

---

# 📌 Features

- User Registration & Login
- JWT Authentication & Authorization
- Role Based Access Control
- Product Management
- Order Placement
- Payment Processing
- Email Notifications
- RabbitMQ Messaging
- Service Discovery with Eureka
- API Gateway Routing
- Database per Service
- Dockerized Deployment
- Environment Variable Configuration

---

# ⚙️ Architecture

```
                   Client
                      │
                      ▼
               API Gateway
                      │
      ┌───────────────┼───────────────┐
      ▼               ▼               ▼
 User Service   Product Service   Order Service
                                       │
                                       ▼
                               Payment Service
                                       │
                                       ▼
                               RabbitMQ Queue
                                       │
                                       ▼
                                Email Service

                 Eureka Service Registry
```

---

# 🔄 Order Flow

1. User registers and logs in.
2. JWT token is generated.
3. User requests product details.
4. Product service returns available products.
5. User places an order.
6. Order service validates the product.
7. Payment service processes payment.
8. RabbitMQ publishes an event.
9. Email service consumes the event.
10. Confirmation email is sent to the customer.

---

# 🔐 Security

- Spring Security
- JWT Authentication
- Stateless Authentication
- Password Encryption using BCrypt
- Role-based Authorization

---

# 🐳 Docker

Each microservice has its own Dockerfile.

Docker Compose is used to start:

- MySQL
- RabbitMQ
- Eureka Server
- API Gateway
- User Service
- Product Service
- Order Service
- Payment Service
- Email Service

Run the application:

```bash
docker compose up --build
```

Stop:

```bash
docker compose down
```

---

# 📂 Project Structure

```
orderhub
│
├── service-registry
├── api-gateway
├── user-service
├── product-service
├── order-service
├── payment-service
├── email-service
├── docker-compose.yml
└── README.md
```

---

# 📨 RabbitMQ

RabbitMQ is used for synchronous and asynchronous communication between services.

Example Flow:

```
Order Service
      │
      ▼
RabbitMQ Queue
      │
      ▼
Email Service
```

This improves scalability and decouples services.

---

# 🗄️ Database

Each microservice maintains its own MySQL database.

- User DB
- Product DB
- Order DB
- Payment DB
- Email DB

This follows the **Database per Service** pattern.

---

# 🌟 Future Improvements

- Product Search
- Redis Caching
- Distributed Tracing (Zipkin)
- Centralized Logging (ELK)
- Kubernetes Deployment
- CI/CD with Jenkins
- Prometheus & Grafana Monitoring
- API Documentation with Swagger/OpenAPI
- Inventory Reservation
- Order Tracking
- File Upload for Product Images

---

# 👨‍💻 Author

**Syed Abdul Rahman**

Java Backend Developer

---

## ⭐ If you like this project

Give this repository a ⭐ and feel free to contribute!