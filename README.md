# BookGrid — Library Management System Backend API

<p align="center">
  <img src="https://img.shields.io/badge/Spring--Boot-3.2.3-brightgreen?style=for-the-badge&logo=springboot" alt="Spring Boot 3.2.3" />
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java 21" />
  <img src="https://img.shields.io/badge/PostgreSQL-DB-blue?style=for-the-badge&logo=postgresql" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Security-JWT-red?style=for-the-badge&logo=jsonwebtokens" alt="JWT Security" />
  <img src="https://img.shields.io/badge/Swagger-OpenAPI-green?style=for-the-badge&logo=swagger" alt="Swagger" />
</p>

## 📌 Overview

**BookGrid LMS Backend** is a high-performance, robust RESTful service built with **Spring Boot 3.2**, **Java 21**, **Spring Security (JWT)**, and **Spring Data JPA**. It powers the entire BookGrid Library Management platform, providing secure authentication, role-based authorization, inventory barcode tracking, real-time circulation management, PDF e-Reader support, fine processing, and notification services.

---

## ✨ Key Features

- 🔐 **Security & Role-Based Authorization**
  - Stateless **JWT Authentication** (Bearer Token).
  - Fine-grained Access Control across **`STUDENT`**, **`MODERATOR`**, and **`ADMINISTRATOR`** roles.
  - Student Registration with custom/predefined university departments and exact Student ID preservation.

- 📚 **Book Catalog & e-Reader Metadata API**
  - Full CRUD operations for physical books and digital e-Books.
  - Dynamic image cover storage (Cloudinary integration) and e-Book PDF URL storage.
  - Public & protected catalog queries with instant title, ISBN, and category filtering.

- 📦 **Physical Inventory & Barcode Tracking**
  - Barcode tag generation and stack/shelf location mappings (`Stack 1A`, `Rack R1`).
  - Physical copy status tracking (`Available`, `Checked Out`, `Maintenance`, `Lost`) and condition monitoring (`New`, `Excellent`, `Good`, `Fair`, `Damaged`).

- 🔄 **Circulation Desk (Borrowing & Returns)**
  - Book issuance to verified students with custom due dates.
  - Dynamic return processing with automatic fine calculation for overdue items.
  - Loan renewals with updated return deadlines.

- 💰 **Fine & Fee Assessment Desk**
  - Automated fine calculation based on days overdue.
  - Instant fine collection, receipt payload generation, and payment status updates (`PENDING`, `PAID`, `WAIVED`).

- 🔖 **Reservations & Book Requests**
  - Student book reservation queue with deadline pickup triggers.
  - Student new book request submission and moderator approval pipeline.
  - Automated email notifications via Spring Mail.

- 📊 **Analytics & Audit Log Tracker**
  - Real-time library stats (Active Borrowings, Overdue Loans, Today's Returns, Unpaid Fines).
  - System-wide activity audit logs tracking IP addresses and user actions.

---

## 🛠️ Technology Stack

| Technology | Description |
| :--- | :--- |
| **Framework** | [Spring Boot 3.2.3](https://spring.io/projects/spring-boot) |
| **Language** | [Java 21](https://www.oracle.com/java/) |
| **Security** | Spring Security 6, JJWT (`0.11.5`) |
| **Persistence** | Spring Data JPA, Hibernate |
| **Database** | PostgreSQL (Production) / H2 (Development & Test) |
| **API Docs** | Springdoc OpenAPI UI (Swagger 3) |
| **Email** | Spring Boot Starter Mail (SMTP) |
| **Build Tool** | Maven 3.x |

---

## 📁 Repository Structure

```
LMS Backend/
├── src/
│   ├── main/
│   │   ├── java/com/bookvault/backend/
│   │   │   ├── config/              # Security, CORS, OpenAPI & Seed Data Initializers
│   │   │   ├── controller/          # REST Endpoints (Auth, Book, Student, Moderator, Admin)
│   │   │   ├── dto/                 # Data Transfer Objects (Login, Register, UserDTO, etc.)
│   │   │   ├── entity/              # JPA Entities (User, Book, Borrowing, Fine, Reservation, etc.)
│   │   │   ├── repository/          # Spring Data JPA Repositories
│   │   │   ├── security/            # JWT Token Provider, Filters, & Security Config
│   │   │   └── service/             # Business Logic & Email Service
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-prod.properties
│   └── test/                        # Unit & Integration Tests
├── Dockerfile                       # Multi-stage Docker build configuration
├── pom.xml                          # Maven build dependencies
└── README.md
```

---

## ⚙️ Prerequisites & Environment Configuration

### Prerequisites
- **JDK 21** installed and configured in system path (`java -version`).
- **Maven 3.8+** installed (`mvn -version`).
- **PostgreSQL Database** running (or default to H2 in-memory DB).

### Environment Variables (`application.properties` or Environment)
Create an `.env` file or export the following environment variables:

```properties
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bookgrid_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# JWT Security
JWT_SECRET=*************************************************************************

# Server Port
SERVER_PORT=9292

# Email Verification (SMTP)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password
```

---

## 🚀 Quickstart & Execution

### 1. Clone & Navigate to Backend
```bash
git clone https://github.com/sakib-011/LMS-Backend.git
cd LMS-Backend
```

### 2. Run with Maven
```bash
mvn spring-boot:run
```
The server will start at `http://localhost:9292/api/v1`.

### 3. Access Swagger API Documentation
Once running, open your browser to:
- **Swagger UI**: `http://localhost:9292/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:9292/v3/api-docs`

---

## 🔑 Key API Endpoints Summary

### 🔓 Public & Authentication
- `POST /api/v1/auth/register` — Register a new student/user (Preserves exact Student ID & Department)
- `POST /api/v1/auth/login` — Authenticate user & return JWT Token
- `POST /api/v1/auth/resend-verification` — Send email verification link

### 📚 Catalog & Books
- `GET /api/v1/books` — Get all library books
- `GET /api/v1/books/{id}` — Get book details by ID
- `POST /api/v1/books` — Create new book record (Moderator/Admin)
- `PUT /api/v1/books/{id}` — Update book record
- `PUT /api/v1/books/{id}/image` — Update book cover Cloudinary image URL
- `PUT /api/v1/books/{id}/pdf` — Update book e-Book PDF URL

### 🛡️ Moderator Desk (`/api/v1/moderator`)
- `GET /moderator/dashboard` — Get library stats summary
- `GET /moderator/borrowing` — List active borrowings
- `POST /moderator/borrowing/issue` — Checkout book to student
- `POST /moderator/returns/process` — Process book return & calculate fines
- `GET /moderator/inventory` — List physical inventory items & stacks locations
- `GET /moderator/students` — List students with dynamic active borrowing & fine totals
- `GET /moderator/fines` — View fine records
- `POST /moderator/fines/{id}/collect` — Collect fine payment

### 👑 Administrator (`/api/v1/admin`)
- `GET /admin/users` — List all system users
- `POST /admin/users` — Create user with specific role
- `PUT /admin/users/{id}` — Edit user credentials/roles
- `DELETE /admin/users/{id}` — Remove user account

---

## 🐳 Docker Deployment

### Build Docker Image
```bash
docker build -t bookgrid-backend .
```

### Run Docker Container
```bash
docker run -d -p 9292:9292 --name bookgrid-backend-app bookgrid-backend
```

---

## 📜 License & Copyright
Developed for University Web & Internet Application Project.  
© 2026 Sakib Shourov & BookGrid Team. All rights reserved.
