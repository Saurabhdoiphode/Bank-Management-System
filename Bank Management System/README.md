# Bank Management System

A complete, production-ready banking system built with Spring Boot backend and modern HTML/CSS/JavaScript frontend, featuring split-screen login design, customer and admin dashboards, and comprehensive banking operations.

## 🚀 Features

### Frontend Features
- **Modern Split-Screen Login**: Customer login on left, Admin login on right
- **Animated UI**: Smooth transitions, glassmorphism cards, floating animations
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Customer Dashboard**: View balance, transactions, perform deposits/withdrawals/transfers
- **Admin Dashboard**: Manage customers, view transactions, analytics, approve accounts
- **Real-time Updates**: Dynamic balance updates, transaction history

### Backend Features
- **JWT Authentication**: Secure token-based authentication
- **RESTful APIs**: Complete API endpoints for all operations
- **MongoDB Integration**: Document-based data storage
- **Password Encryption**: BCrypt password hashing
- **Role-Based Access**: Customer and Admin roles
- **Transaction Management**: Deposit, withdrawal, and transfer operations
- **Admin Features**: Customer management, transaction tracking, analytics

## 📋 Project Structure

```
Bank Management System/
├── frontend/
│   ├── index.html                 # Home page with split-screen login
│   ├── customer-dashboard.html    # Customer dashboard
│   ├── admin-dashboard.html       # Admin dashboard
│   ├── css/
│   │   ├── style.css             # Main styles
│   │   ├── dashboard.css         # Dashboard styles
│   │   └── animations.css        # Animation styles
│   ├── js/
│   │   ├── auth.js               # Authentication logic
│   │   ├── customer-dashboard.js # Customer dashboard logic
│   │   └── admin-dashboard.js    # Admin dashboard logic
│   └── images/                    # Image assets
│
└── backend/
    ├── src/main/java/com/bankmanagement/
    │   ├── BankManagementApplication.java  # Main Spring Boot app
    │   ├── config/                          # Configuration classes
    │   │   ├── CorsConfig.java
    │   │   └── SecurityConfig.java
    │   ├── controller/                      # REST Controllers
    │   │   ├── AuthController.java
    │   │   ├── CustomerController.java
    │   │   ├── AccountController.java
    │   │   ├── TransactionController.java
    │   │   └── AdminController.java
    │   ├── model/                           # MongoDB Models
    │   │   ├── Customer.java
    │   │   ├── Admin.java
    │   │   └── Transaction.java
    │   ├── repository/                      # MongoDB Repositories
    │   │   ├── CustomerRepository.java
    │   │   ├── AdminRepository.java
    │   │   └── TransactionRepository.java
    │   ├── service/                         # Business Logic Services
    │   │   ├── CustomerService.java
    │   │   ├── AdminService.java
    │   │   ├── TransactionService.java
    │   │   └── JwtService.java
    │   └── dto/                             # Data Transfer Objects
    │       ├── LoginRequest.java
    │       ├── CustomerRegisterRequest.java
    │       ├── AuthResponse.java
    │       ├── DepositRequest.java
    │       ├── WithdrawRequest.java
    │       └── TransferRequest.java
    ├── src/main/resources/
    │   └── application.properties           # Spring Boot configuration
    └── pom.xml                              # Maven dependencies
```

## 🛠️ Tech Stack

### Frontend
- **HTML5**: Semantic markup
- **CSS3**: Modern styling with gradients, animations, and responsive design
- **JavaScript (ES6+)**: Dynamic interactions, API calls, DOM manipulation
- **Chart.js**: Data visualization for analytics

### Backend
- **Java 17**: Programming language
- **Spring Boot 3.1.5**: Application framework
- **Spring Data MongoDB**: MongoDB integration
- **Spring Security**: Authentication and security
- **JWT (JSON Web Tokens)**: Token-based authentication
- **Maven**: Dependency management

### Database
- **MongoDB**: Document-based NoSQL database

## 📦 Installation & Setup

### Prerequisites
- Java 17 or higher
- MongoDB running locally (default: localhost:27017)
- Node.js (optional, for serving frontend)
- Modern web browser

### Backend Setup

1. **Navigate to backend directory:**
```bash
cd backend
```

2. **Install dependencies:**
```bash
mvn clean install
```

3. **Configure MongoDB (optional):**
Edit `src/main/resources/application.properties` if using different MongoDB configuration.

Default configuration:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017
spring.data.mongodb.database=bank_management_db
```

4. **Run Spring Boot application:**
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory:**
```bash
cd frontend
```

2. **Serve the frontend (choose one):**

**Option 1: Using Python (built-in)**
```bash
python -m http.server 3000
```

**Option 2: Using Node.js http-server**
```bash
npx http-server -p 3000
```

**Option 3: Using any local server**
- Open `index.html` directly in browser (limited functionality)

3. **Access the application:**
- Open browser and go to `http://localhost:3000`

## 🔐 Default Credentials

### Admin Login
- **Email:** `admin@gmail.com`
- **Password:** `admin123`

### Customer Registration
- Use the "Register" tab on the home page to create a new account

## 📡 API Endpoints

### Authentication
- `POST /api/auth/customer/register` - Customer registration
- `POST /api/auth/customer/login` - Customer login
- `POST /api/auth/admin/login` - Admin login

### Customer
- `GET /api/customers/profile` - Get customer profile
- `GET /api/customers` - Get all customers (admin)
- `PUT /api/customers/profile` - Update profile
- `DELETE /api/customers/{customerId}` - Delete customer (admin)

### Transactions
- `POST /api/transactions/deposit` - Deposit money
- `POST /api/transactions/withdraw` - Withdraw money
- `POST /api/transactions/transfer` - Transfer money
- `GET /api/transactions/recent` - Recent transactions
- `GET /api/transactions/all` - All transactions
- `GET /api/transactions/admin/all` - All transactions (admin)

### Accounts
- `GET /api/accounts/balance` - Get account balance
- `GET /api/accounts/info` - Get account information

### Admin
- `GET /api/admin/customers` - List all customers
- `GET /api/admin/transactions` - List all transactions
- `GET /api/admin/stats/dashboard` - Dashboard statistics
- `PUT /api/admin/approvals/{customerId}/approve` - Approve account
- `PUT /api/admin/approvals/{customerId}/reject` - Reject account

## 🎨 UI/UX Features

### Design Elements
- **Glassmorphism**: Modern frosted glass effect on cards
- **Animated Gradients**: Smooth color transitions
- **Floating Animations**: Elements animate smoothly
- **Responsive Layout**: Mobile-first approach
- **Dark Theme**: Eye-friendly dark interface
- **Smooth Transitions**: All interactions have smooth animations

### Customer Dashboard
- Welcome card with personalized greeting
- 4 key info cards (Balance, Monthly Spent, Incoming Funds, Savings Goal)
- Quick action buttons (Deposit, Withdraw, Transfer, Pay Bills)
- Recent transactions table
- Analytics charts (Monthly Spending, Transaction Types)
- Transaction history with filtering
- Profile section
- Settings panel

### Admin Dashboard
- Key statistics cards (Total Users, Transactions, Total Balance, Pending Approvals)
- Charts for daily transactions and user growth
- Customer management table with search and filter
- Transaction management with advanced filtering
- Account approvals section
- Banking analytics with multiple charts
- System configuration settings

## 🔒 Security Features

- **Password Hashing**: BCrypt encryption for all passwords
- **JWT Authentication**: Secure token-based sessions
- **CORS Protection**: Cross-origin request handling
- **Role-Based Access**: Customer and Admin separated
- **Input Validation**: Server-side validation for all inputs
- **Secure Headers**: Proper HTTP headers for security

## 📊 Database Schema

### Customers Collection
```javascript
{
  fullName: String,
  email: String (unique),
  password: String (hashed),
  mobileNumber: String,
  address: String,
  aadhaarNumber: String,
  panNumber: String,
  accountType: String (SAVINGS/CURRENT/STUDENT),
  balance: Number,
  accountNumber: String (unique),
  status: String (ACTIVE/INACTIVE/BLOCKED),
  active: Boolean,
  createdAt: DateTime,
  updatedAt: DateTime,
  lastLogin: DateTime
}
```

### Transactions Collection
```javascript
{
  customerId: String,
  accountNumber: String,
  customerName: String,
  type: String (DEPOSIT/WITHDRAW/TRANSFER/TRANSFER_RECEIVED),
  amount: Number,
  balanceBefore: Number,
  balanceAfter: Number,
  description: String,
  status: String (PENDING/COMPLETED/FAILED),
  method: String,
  recipientAccountNumber: String,
  recipientName: String,
  createdAt: DateTime,
  referenceId: String
}
```

## 🚀 Performance Optimizations

- Lazy loading for images and charts
- Efficient MongoDB queries with indexing
- JWT token caching on client
- Responsive image handling
- CSS animations use hardware acceleration
- Debounced search operations

## 🐛 Troubleshooting

### MongoDB Connection Issues
- Ensure MongoDB is running on localhost:27017
- Check MongoDB logs for errors
- Verify database name in application.properties

### Frontend API Calls Failing
- Ensure backend is running on localhost:8080
- Check browser console for CORS errors
- Verify JWT token is being sent in Authorization header

### Authentication Issues
- Clear browser cache and cookies
- Check JWT token expiration in application.properties
- Verify credentials are correct

## 📝 Future Enhancements

- [ ] Email notifications
- [ ] SMS alerts
- [ ] Two-factor authentication
- [ ] Investment options
- [ ] Loan applications
- [ ] Mobile app (React Native)
- [ ] Advanced analytics and reports
- [ ] Card management
- [ ] Bill payments integration
- [ ] Multi-currency support

## 📄 License

This project is open source and available under the MIT License.

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📞 Support

For any questions or support, please contact the development team or create an issue in the repository.

## ✨ Credits

Built with ❤️ by the Bank Management System Team

---

**Happy Banking! 🏦**
