# 🛒 SmartShop - E-Commerce Backend API

A production-ready e-commerce backend built using Spring Boot, JWT Authentication, MySQL, and Razorpay integration.

---

## 🚀 Features

- JWT Authentication & Authorization
- Role-Based Access Control (USER / ADMIN)
- Product & Category Management
- Shopping Cart Management
- Order Processing
- Razorpay Payment Integration
- RESTful API Architecture
- Global Exception Handling
- Swagger API Documentation

---

## 🛠 Tech Stack

| Technology | Version |
|------------|---------|
| Java | 17 |
| Spring Boot | 3.2.5 |
| Spring Security | 6.x |
| Spring Data JPA | Hibernate |
| MySQL | 8 |
| JWT | Authentication |
| Razorpay | Payment Gateway |
| Maven | Build Tool |

---

## 📂 Project Structure

src/main/java/com/smartshop/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── security/
├── config/
├── payment/
├── exception/
└── util/

---

## 📡 API Modules

### Authentication
- Register User
- Login User

### Products
- Create Product
- Update Product
- Delete Product
- Search Products
- Category Filtering

### Cart
- Add to Cart
- Remove from Cart
- Clear Cart

### Orders
- Create Order
- View Orders
- Update Order Status

### Payments
- Razorpay Order Creation
- Payment Verification

---

## ⚙️ Installation

### Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/smartshop-backend.git
cd smartshop-backend

---

Database Setup 
CREATE DATABASE smartshop_db;

spring.datasource.url=jdbc:mysql://localhost:3306/smartshop_db
spring.datasource.username=root
spring.datasource.password=your_password

app.jwt.secret=your_secret_key

razorpay.key.id=your_key
razorpay.key.secret=your_secret

Run Project
mvn clean install
mvn spring-boot:run

API Documentation

Swagger UI

http://localhost:8080/swagger-ui.html

OpenAPI Docs

http://localhost:8080/v3/api-docs

Testing
mvn test

Deployment

Build Application

mvn clean package

Run JAR

java -jar target/smartshop-ecommerce.jar

Author

Deepak Rathore

GitHub:
https://github.com/deepakrathor0789-lab

LinkedIn:
(www.linkedin.com/in/deepak-rathor-282925366)

📄 License

This project is licensed under the MIT License.
