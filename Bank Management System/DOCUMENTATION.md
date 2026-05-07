# 📚 Bank Management System - Complete Documentation

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Frontend Architecture](#frontend-architecture)
3. [Backend Architecture](#backend-architecture)
4. [Database Schema](#database-schema)
5. [API Documentation](#api-documentation)
6. [Authentication Flow](#authentication-flow)
7. [Transaction Flow](#transaction-flow)
8. [Deployment Guide](#deployment-guide)

---

## Architecture Overview

### System Architecture Diagram
```
┌─────────────────────────────────────────────────────────────┐
│                     WEB BROWSER                             │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Frontend (HTML/CSS/JavaScript)                        │ │
│  │  - Login Page (Split-screen design)                   │ │
│  │  - Customer Dashboard                                  │ │
│  │  - Admin Dashboard                                     │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                          ↕ HTTP/REST
┌─────────────────────────────────────────────────────────────┐
│                 SPRING BOOT BACKEND (Java)                  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ Controllers                                           │  │
│  │ - AuthController      - CustomerController           │  │
│  │ - AccountController   - TransactionController        │  │
│  │ - AdminController                                    │  │
│  └───────────────────────────────────────────────────────┘  │
│                          ↓                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ Services (Business Logic)                             │  │
│  │ - CustomerService     - TransactionService           │  │
│  │ - AdminService        - JwtService                   │  │
│  └───────────────────────────────────────────────────────┘  │
│                          ↓                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ Repositories (Data Access)                            │  │
│  │ - CustomerRepository  - AdminRepository              │  │
│  │ - TransactionRepository                              │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                          ↕ MongoDB
┌─────────────────────────────────────────────────────────────┐
│                      MONGODB (NoSQL)                        │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ Collections                                           │  │
│  │ - customers    - admins    - transactions            │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## Frontend Architecture

### Technology Stack
- **HTML5**: Semantic structure and accessibility
- **CSS3**: Modern styling with animations
- **JavaScript (ES6+)**: Client-side logic and API integration
- **Chart.js**: Data visualization

### Key Files

#### index.html (Login Page)
- Split-screen layout
- Customer login/registration forms
- Admin login panel
- Glassmorphism design
- Responsive layout

#### customer-dashboard.html
- Sidebar navigation
- Welcome card
- Account overview cards
- Quick action buttons
- Transaction history
- Analytics charts
- Profile management
- Settings panel

#### admin-dashboard.html
- Sidebar navigation
- Dashboard statistics
- Customer management table
- Transaction list
- Account approvals
- Analytics dashboard
- System settings

### Frontend State Management
```
┌─────────────────────┐
│  localStorage       │
├─────────────────────┤
│ - token            │
│ - customerId       │
│ - customerName     │
│ - accountNumber    │
│ - adminToken       │
└─────────────────────┘
```

### Frontend Flow
```
index.html (Login)
    ↓
auth.js (Handles login/registration)
    ↓
    ├─→ Customer Login → Redirect to customer-dashboard.html
    │       ↓
    │   customer-dashboard.js (Load customer data)
    │       ↓
    │   Display dashboard with animations
    │
    └─→ Admin Login → Redirect to admin-dashboard.html
            ↓
        admin-dashboard.js (Load admin data)
            ↓
        Display admin panel with charts
```

---

## Backend Architecture

### Technology Stack
- **Java 17**: Programming language
- **Spring Boot 3.1.5**: Framework
- **Spring Data MongoDB**: ORM for MongoDB
- **Spring Security**: Authentication
- **JWT**: Token-based auth

### Package Structure

```
com.bankmanagement/
├── BankManagementApplication.java       # Main class
├── config/                              # Configuration
│   ├── CorsConfig.java
│   ├── SecurityConfig.java
│   └── JwtConfig.java
├── controller/                          # REST Endpoints
│   ├── AuthController.java
│   ├── CustomerController.java
│   ├── AccountController.java
│   ├── TransactionController.java
│   └── AdminController.java
├── model/                               # Data Models
│   ├── Customer.java
│   ├── Admin.java
│   └── Transaction.java
├── repository/                          # Data Access
│   ├── CustomerRepository.java
│   ├── AdminRepository.java
│   └── TransactionRepository.java
├── service/                             # Business Logic
│   ├── CustomerService.java
│   ├── AdminService.java
│   ├── TransactionService.java
│   └── JwtService.java
└── dto/                                 # Data Transfer Objects
    ├── LoginRequest.java
    ├── CustomerRegisterRequest.java
    ├── AuthResponse.java
    ├── DepositRequest.java
    ├── WithdrawRequest.java
    └── TransferRequest.java
```

### Service Layer Architecture

```
┌─────────────────────────────────────────┐
│         CustomerService                 │
├─────────────────────────────────────────┤
│ - registerCustomer()                    │
│ - loginCustomer()                       │
│ - getCustomerById()                     │
│ - updateCustomer()                      │
│ - getAllCustomers()                     │
│ - getTotalBankBalance()                 │
└─────────────────────────────────────────┘
         ↓ Uses
┌─────────────────────────────────────────┐
│   CustomerRepository (MongoDB)          │
├─────────────────────────────────────────┤
│ - findByEmail()                         │
│ - findByAccountNumber()                 │
│ - findAll()                             │
│ - save()                                │
│ - delete()                              │
└─────────────────────────────────────────┘
```

### Request/Response Flow

```
Request from Frontend
    ↓
Controller (Parse request)
    ↓
Service (Business logic)
    ↓
Repository (Database operation)
    ↓
MongoDB (Data storage)
    ↓
Response back through same path
```

---

## Database Schema

### Customers Collection
```json
{
  "_id": ObjectId,
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "$2a$10$encrypted...",
  "mobileNumber": "9876543210",
  "address": "123 Main St",
  "aadhaarNumber": "123456789012",
  "panNumber": "ABCDE1234F",
  "accountType": "SAVINGS",
  "balance": 50000.00,
  "accountNumber": "SB123456789",
  "status": "ACTIVE",
  "active": true,
  "createdAt": ISODate("2023-01-01"),
  "updatedAt": ISODate("2023-01-15"),
  "lastLogin": ISODate("2023-01-20")
}
```

### Transactions Collection
```json
{
  "_id": ObjectId,
  "customerId": ObjectId,
  "accountNumber": "SB123456789",
  "customerName": "John Doe",
  "type": "DEPOSIT",
  "amount": 5000.00,
  "balanceBefore": 45000.00,
  "balanceAfter": 50000.00,
  "description": "Deposit via UPI",
  "status": "COMPLETED",
  "method": "UPI",
  "recipientAccountNumber": null,
  "recipientName": null,
  "referenceId": "TXN1234567890",
  "createdAt": ISODate("2023-01-20"),
  "updatedAt": ISODate("2023-01-20")
}
```

### Admins Collection
```json
{
  "_id": ObjectId,
  "name": "Administrator",
  "email": "admin@gmail.com",
  "password": "$2a$10$encrypted...",
  "role": "SUPER_ADMIN",
  "status": "ACTIVE",
  "active": true,
  "createdAt": ISODate("2023-01-01"),
  "updatedAt": ISODate("2023-01-01"),
  "lastLogin": ISODate("2023-01-20")
}
```

---

## API Documentation

### Authentication APIs

#### Customer Registration
```
POST /api/auth/customer/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "mobileNumber": "9876543210",
  "address": "123 Main St",
  "aadhaarNumber": "123456789012",
  "panNumber": "ABCDE1234F",
  "accountType": "SAVINGS"
}

Response (201):
{
  "success": true,
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": "507f1f77bcf86cd799439011",
  "name": "John Doe",
  "email": "john@example.com",
  "accountNumber": "SB123456789",
  "message": "Customer registered successfully"
}
```

#### Customer Login
```
POST /api/auth/customer/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePass123"
}

Response (200):
{
  "success": true,
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": "507f1f77bcf86cd799439011",
  "name": "John Doe",
  "email": "john@example.com",
  "accountNumber": "SB123456789",
  "message": "Login successful"
}
```

#### Admin Login
```
POST /api/auth/admin/login
Content-Type: application/json

{
  "email": "admin@gmail.com",
  "password": "admin123"
}

Response (200):
{
  "success": true,
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": "507f1f77bcf86cd799439012",
  "name": "Administrator",
  "email": "admin@gmail.com",
  "message": "Admin login successful"
}
```

### Transaction APIs

#### Deposit Money
```
POST /api/transactions/deposit
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 5000.00,
  "method": "UPI",
  "description": "Deposit via UPI"
}

Response (200):
{
  "id": "...",
  "type": "DEPOSIT",
  "amount": 5000.00,
  "balanceAfter": 50000.00,
  "status": "COMPLETED"
}
```

#### Withdraw Money
```
POST /api/transactions/withdraw
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 1000.00,
  "withdrawType": "ATM",
  "description": "ATM Withdrawal"
}

Response (200):
{
  "id": "...",
  "type": "WITHDRAW",
  "amount": 1000.00,
  "balanceAfter": 49000.00,
  "status": "COMPLETED"
}
```

#### Transfer Money
```
POST /api/transactions/transfer
Authorization: Bearer {token}
Content-Type: application/json

{
  "recipientAccountNumber": "SB987654321",
  "amount": 2000.00,
  "description": "Payment for services"
}

Response (200):
{
  "id": "...",
  "type": "TRANSFER",
  "amount": 2000.00,
  "balanceAfter": 47000.00,
  "recipientAccountNumber": "SB987654321",
  "status": "COMPLETED"
}
```

### Account APIs

#### Get Account Balance
```
GET /api/accounts/balance
Authorization: Bearer {token}

Response (200):
{
  "balance": 47000.00,
  "accountNumber": "SB123456789",
  "accountType": "SAVINGS"
}
```

---

## Authentication Flow

### JWT Token Structure
```
JWT Token = Header.Payload.Signature

Header: {
  "alg": "HS512",
  "typ": "JWT"
}

Payload: {
  "sub": "customerId",
  "role": "CUSTOMER",
  "iat": 1234567890,
  "exp": 1234654290
}

Signature: HMACSHA512(Base64(Header) + "." + Base64(Payload), secret)
```

### Login Flow
```
1. User enters credentials
   ↓
2. Frontend sends POST /api/auth/customer/login
   ↓
3. Backend validates credentials
   ↓
4. Password verification (BCrypt)
   ↓
5. Generate JWT token
   ↓
6. Return token to frontend
   ↓
7. Frontend stores token in localStorage
   ↓
8. Redirect to dashboard
   ↓
9. All subsequent requests include: Authorization: Bearer {token}
```

### Token Validation
```
For each protected endpoint:
1. Extract token from Authorization header
2. Validate token signature
3. Extract userId from token
4. Verify token hasn't expired
5. Load user from database
6. Proceed with request
```

---

## Transaction Flow

### Deposit Flow
```
Customer initiates deposit
   ↓
Validate amount
   ↓
Load customer from database
   ↓
Calculate new balance (balance + amount)
   ↓
Update customer balance
   ↓
Create transaction record
   ↓
Save transaction
   ↓
Return transaction details
   ↓
Update frontend UI with new balance
```

### Transfer Flow
```
Customer enters transfer details
   ↓
Validate sender and recipient accounts exist
   ↓
Check sender has sufficient balance
   ↓
Deduct amount from sender
   ↓
Add amount to recipient
   ↓
Create transfer transaction (from sender)
   ↓
Create transfer_received transaction (for recipient)
   ↓
Return transaction details
   ↓
Update frontend with new balance
```

---

## Deployment Guide

### Production Deployment Checklist

#### Backend Deployment
1. **Update JWT Secret** (Change from default)
```properties
jwt.secret=your-very-long-and-secure-secret-key-that-is-hard-to-guess
```

2. **Configure MongoDB**
```properties
spring.data.mongodb.uri=mongodb://username:password@mongodb-server:27017
spring.data.mongodb.database=bank_management_prod
```

3. **Set Production Profile**
```properties
spring.profiles.active=production
```

4. **Build JAR**
```bash
mvn clean package -DskipTests
```

5. **Run JAR**
```bash
java -jar target/bank-management-system-1.0.0.jar
```

#### Frontend Deployment
1. **Build for Production** (Optional, if using build tools)
2. **Deploy to Web Server**
   - Nginx
   - Apache
   - Cloud storage (S3, Azure Blob)
3. **Update API_BASE_URL** in auth.js
4. **Enable HTTPS**
5. **Configure CORS**

#### Security Considerations
- Use HTTPS everywhere
- Implement rate limiting
- Add request validation
- Use secure cookies
- Implement CSRF protection
- Regular security audits
- Update dependencies regularly

---

## Performance Optimization

### Database Indexing
```javascript
db.customers.createIndex({ "email": 1 })
db.customers.createIndex({ "accountNumber": 1 })
db.transactions.createIndex({ "customerId": 1, "createdAt": -1 })
```

### Frontend Optimization
- Lazy load images
- Cache API responses
- Minify CSS/JS
- Use CDN for static assets
- Optimize animations
- Debounce search input

### Backend Optimization
- Connection pooling
- Query optimization
- Cache frequently accessed data
- Use appropriate data types
- Regular database maintenance

---

## Troubleshooting Guide

### Common Issues and Solutions

#### MongoDB Connection Failed
**Problem**: Cannot connect to MongoDB
**Solution**: 
- Ensure MongoDB is running
- Check connection string
- Verify MongoDB port (27017)
- Check authentication credentials

#### JWT Token Expired
**Problem**: "Token expired" error
**Solution**:
- Clear browser localStorage
- Log in again
- Check jwt.expiration setting

#### CORS Error
**Problem**: "No 'Access-Control-Allow-Origin' header"
**Solution**:
- Ensure backend is running
- Check CORS configuration
- Verify allowed origins
- Restart backend

#### Insufficient Balance
**Problem**: Withdrawal fails
**Solution**:
- Check current balance first
- Verify withdrawal amount is less than balance
- Try smaller amount

---

## Support & Contribution

For issues, questions, or contributions, please refer to the main README.md file.

---

**Last Updated**: 2024
**Version**: 1.0.0
