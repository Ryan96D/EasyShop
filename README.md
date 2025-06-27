# 🛒 EasyShop E-Commerce API

A full-featured Java Spring Boot REST API for **EasyShop**, a fictional online shopping platform. This project powers a secure, dynamic online store with real-time shopping cart management, user profiles, and checkout functionality — all connected to a MySQL database.

> 🔐 Authentication, 💬 Search, 🛍️ Cart, 📦 Checkout, 👤 Profiles — it's all here.

---

## 🌟 What Can This App Do?

### 🔐 User Authentication
- Register and log in securely using encrypted passwords and JWT tokens
- Role-based access: `ADMIN` vs `USER`
- Persistent sessions across browser or Postman

### 🛍️ Browse & Search Products
- View all products or filter by:
  - Category
  - Color
  - Price range (`minPrice` and `maxPrice`)
- Detailed product data: name, description, price, stock, image, category

###🛒 Shopping Cart
Add items to cart while browsing

- Cart remembers your items even after logout
- Update item quantity or remove entire cart
- Cart displays line totals and final total

###👤 User Profile
- Automatically created when a user registers
- View and update your profile info
- One profile per user, securely linked to your login

###🧾 Checkout & Receipts
- Checkout API turns your cart into a receipt
- Database saves receipt and line items
- View previous purchases with full item details and totals

###🚀 Tech Stack
- Java 17+
- Spring Boot + Spring Security + JWT
- MySQL 8
- JDBC
- Postman 
- HTML

###🧪 Sample API Endpoints
- POST	/register	Create new user
- POST	/login	Authenticate and get token
- GET	/products?cat=1&color=black&minPrice=50&maxPrice=500	Filter products
- POST	/cart/products/12	Add item to cart
- PUT	/cart/products/12	Update quantity
- POST	/orders	Convert cart to receipt
- GET	/profile	Get profile info
- PUT	/profile	Update profile

###🔐 How to Run Locally
Import create_database.sql into MySQL

Configure your application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/easyshop
spring.datasource.username=root
spring.datasource.password=yourpassword

###👨‍💻 Author
Ryan Do
Backend Developer • App Dev & Coding Track @ YearUp United

