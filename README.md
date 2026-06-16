# 🛒 SmartShop - Backend E-Commerce API

[![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)](https://jwt.io/)
[![Razorpay](https://img.shields.io/badge/Razorpay-Payment-0C8B3E?style=for-the-badge&logo=razorpay&logoColor=white)](https://razorpay.com/)

---

## 📌 Project Overview

**SmartShop** is a production-ready e-commerce backend API built with Spring Boot. It provides secure REST APIs for user authentication, product management, shopping cart, order processing, and payment integration with Razorpay.

### ✨ Key Features

| Feature | Description |
|---------|-------------|
| 🔐 **Authentication** | JWT-based secure authentication with role-based access control (USER/ADMIN) |
| 📦 **Product Management** | Complete CRUD operations with category-based filtering |
| 🛒 **Shopping Cart** | Add, update, and remove products from cart |
| 📋 **Order Processing** | Place orders with automatic stock management |
| 💳 **Payment Integration** | Razorpay payment gateway with verification |
| 📊 **Admin Dashboard** | Manage products, orders, and users |

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2.5 |
| **Security** | Spring Security 6.x, JWT |
| **ORM** | Spring Data JPA (Hibernate) |
| **Database** | MySQL 8.0 |
| **Payment** | Razorpay API |
| **Build Tool** | Maven 3.9+ |
| **API Documentation** | Swagger/OpenAPI 3.0 |

---

## 🗂️ Project Structure
src/main/java/com/smartshop/
├── controller/ # REST API endpoints
│ ├── AuthController.java
│ ├── ProductController.java
│ ├── CartController.java
│ ├── OrderController.java
│ └── CategoryController.java
├── service/ # Business logic layer
│ ├── AuthService.java
│ ├── ProductService.java
│ ├── CartService.java
│ ├── OrderService.java
│ └── impl/ # Service implementations
├── repository/ # JPA Repository interfaces
├── entity/ # Database entities (JPA)
│ ├── User.java
│ ├── Role.java
│ ├── Product.java
│ ├── Category.java
│ ├── CartItem.java
│ ├── Order.java
│ └── OrderItem.java
├── dto/ # Data Transfer Objects
├── config/ # Configuration classes
├── security/ # JWT & Security configuration
├── payment/ # Razorpay integration
├── exception/ # Global exception handling
└── util/ # Utility classes

text

---

## 📡 API Endpoints

### 🔐 Authentication

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login & get JWT token | Public |

### 📦 Products

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/products` | Get all products | Public |
| GET | `/api/products/{id}` | Get product by ID | Public |
| GET | `/api/products/search` | Search products | Public |
| GET | `/api/products/category/{id}` | Get products by category | Public |
| POST | `/api/products` | Add new product | Admin |
| PUT | `/api/products/{id}` | Update product | Admin |
| DELETE | `/api/products/{id}` | Delete product | Admin |

### 🛒 Cart

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/cart` | Get user cart | User |
| POST | `/api/cart/add` | Add product to cart | User |
| DELETE | `/api/cart/remove/{id}` | Remove item from cart | User |
| DELETE | `/api/cart/clear` | Clear cart | User |

### 📋 Orders

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/orders/create` | Create order from cart | User |
| GET | `/api/orders/my-orders` | Get user's orders | User |
| GET | `/api/orders/{id}` | Get order by ID | User |
| GET | `/api/orders/all` | Get all orders | Admin |
| PUT | `/api/orders/{id}/status` | Update order status | Admin |

### 💳 Payment

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/payment/create/{orderId}` | Create Razorpay order | User |
| POST | `/api/payment/verify` | Verify payment | User |

---

## 💻 Installation & Setup

### Prerequisites

- JDK 17+
- MySQL 8+
- Maven 3.9+
- Git
- Razorpay Account (for payment)

### Step 1 — Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/smartshop-backend.git
cd smartshop-backend
Step 2 — Database Setup
sql
CREATE DATABASE smartshop_db;
Step 3 — Configure Application
Update src/main/resources/application.properties:

properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/smartshop_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT
app.jwt.secret=YOUR_JWT_SECRET_KEY
app.jwt.expiration=86400000

# Razorpay
razorpay.key.id=YOUR_RAZORPAY_KEY_ID
razorpay.key.secret=YOUR_RAZORPAY_KEY_SECRET
Step 4 — Build & Run
bash
# Build the application
mvn clean package

# Run the application
mvn spring-boot:run
Step 5 — Access API
Base URL: http://localhost:8080

Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI Docs: http://localhost:8080/v3/api-docs

🔐 Environment Variables (For Production)
bash
# Database
DB_URL=jdbc:mysql://localhost:3306/smartshop_db
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your_jwt_secret_key

# Razorpay
RAZORPAY_KEY_ID=your_key_id
RAZORPAY_KEY_SECRET=your_key_secret
🧪 Testing
bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest
📦 Deployment
Build JAR
bash
mvn clean package
Run JAR
bash
java -jar target/smartshop-ecommerce-0.0.1-SNAPSHOT.jar
Deploy on Render
https://render.com/images/deploy-to-render-button.svg

🤝 Contributing
Fork the repository

Create feature branch

bash
git checkout -b feature/AmazingFeature
Commit changes

bash
git commit -m "Add some AmazingFeature"
Push to branch

bash
git push origin feature/AmazingFeature
Open a Pull Request

👨‍💻 Author
Deepak Rathore

GitHub: deepakrathor0789-lab

LinkedIn: Deepak Rathore

Email: deepakrathor0789@gmail.com

📜 License
This project is open source and available under the MIT License.

🙏 Acknowledgments
Spring Boot Documentation

JWT.io

Razorpay API

Baeldung

📊 GitHub Stats
https://img.shields.io/github/stars/deepakrathor0789-lab/smartshop-backend
https://img.shields.io/github/forks/deepakrathor0789-lab/smartshop-backend
https://img.shields.io/github/issues/deepakrathor0789-lab/smartshop-backend
https://img.shields.io/github/license/deepakrathor0789-lab/smartshop-backend

## 💻 Installation & Setup

### Prerequisites
- JDK 17+
- MySQL 8+
- Maven 3.9+
- Git
- Razorpay Account (for payment)

### Step 1 — Clone Repository
```bash
git clone https://github.com/deepakrathor0789-lab/smartshop-backend.git
cd smartshop-backend
```

### Step 2 — Database Setup
```sql
CREATE DATABASE smartshop_db;
```

### Step 3 — Configure Application

Update `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/smartshop_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
app.jwt.secret=YOUR_JWT_SECRET_KEY
app.jwt.expiration=86400000

# Razorpay
razorpay.key.id=YOUR_RAZORPAY_KEY_ID
razorpay.key.secret=YOUR_RAZORPAY_KEY_SECRET
```

### Step 4 — Build & Run
```bash
mvn clean package
mvn spring-boot:run
```

### Step 5 — Access API
- **Base URL:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`

---

## 🔐 Environment Variables (For Production)

```bash
DB_URL=jdbc:mysql://localhost:3306/smartshop_db
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
RAZORPAY_KEY_ID=your_key_id
RAZORPAY_KEY_SECRET=your_key_secret
```

---

## 👨‍💻 Author

**Deepak Rathore**
- GitHub: [deepakrathor0789-lab](https://github.com/deepakrathor0789-lab)
- Email: deepakrathor0789@gmail.com

---

## 📜 License

This project is open source and available under the [MIT License](LICENSE).

---

⭐ **If you find this project useful, please give it a star!**