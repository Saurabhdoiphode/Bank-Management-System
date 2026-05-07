# Quick Setup Guide

## 🚀 Start in 5 Minutes

### Step 1: Start MongoDB
```bash
# Windows
mongod

# Mac/Linux
brew services start mongodb-community
# or
mongod --config /usr/local/etc/mongod.conf
```

### Step 2: Start Backend
```bash
cd backend
mvn spring-boot:run
```
✅ Backend running on `http://localhost:8080`

### Step 3: Start Frontend
```bash
cd frontend
# Using Python
python -m http.server 3000

# Or using Node.js
npx http-server -p 3000
```
✅ Frontend running on `http://localhost:3000`

### Step 4: Open Browser
Navigate to `http://localhost:3000`

---

## 🔐 Default Login

### Admin Credentials
- Email: `admin@gmail.com`
- Password: `admin123`

### Customer
Create a new account using the "Register" tab

---

## 📝 Key Features to Try

### As Customer:
1. Register with your details
2. View your dashboard
3. Try deposit (adds money)
4. Try transfer (send to another account)
5. View transaction history

### As Admin:
1. Login with default credentials
2. View all customers
3. View all transactions
4. Check analytics and statistics
5. Manage customer approvals

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| MongoDB connection error | Ensure MongoDB is running |
| Backend won't start | Check port 8080 is free, Java is installed |
| Frontend not loading | Check port 3000 is free, refresh browser |
| Login fails | Check default credentials, clear cookies |
| CORS error | Restart backend, check frontend URL |

---

## 📦 Project Dependencies

### Frontend
- Vanilla JavaScript (ES6+)
- Chart.js (for charts)
- Font Awesome (for icons)

### Backend
- Spring Boot 3.1.5
- Spring Data MongoDB
- Spring Security
- JWT Token Support

---

## 🎯 Next Steps

1. ✅ Start the application
2. ✅ Login as admin
3. ✅ Create customer accounts
4. ✅ Test transactions
5. ✅ View analytics
6. ✅ Customize as needed

---

## 💡 Tips

- Use Chrome/Firefox for best experience
- Keep browser console open (F12) to debug
- Check network tab for API calls
- MongoDB should auto-create database on first run
- Admin account created automatically on first backend start

---

Happy Banking! 🏦
