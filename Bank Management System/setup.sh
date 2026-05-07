#!/bin/bash

# ==================== INSTALLATION SCRIPT ====================
# This script helps set up the Bank Management System

echo "================================"
echo "Bank Management System Setup"
echo "================================"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
else
    echo "✅ Java is installed: $(java -version 2>&1 | head -1)"
fi

echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven."
    exit 1
else
    echo "✅ Maven is installed: $(mvn -version | head -1)"
fi

echo ""

# Check if MongoDB is installed/running
echo "Checking MongoDB..."
if command -v mongosh &> /dev/null || command -v mongo &> /dev/null; then
    echo "✅ MongoDB CLI found"
else
    echo "⚠️  MongoDB CLI not found. Please ensure MongoDB is running."
fi

echo ""
echo "================================"
echo "Building Backend..."
echo "================================"

cd backend

# Clean and install dependencies
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Backend built successfully!"
else
    echo "❌ Backend build failed!"
    exit 1
fi

echo ""
echo "================================"
echo "Setup Complete!"
echo "================================"
echo ""
echo "Next steps:"
echo ""
echo "1. Start MongoDB:"
echo "   mongod"
echo ""
echo "2. Start Backend (in another terminal):"
echo "   cd backend"
echo "   mvn spring-boot:run"
echo ""
echo "3. Start Frontend (in another terminal):"
echo "   cd frontend"
echo "   python -m http.server 3000"
echo ""
echo "4. Open browser:"
echo "   http://localhost:3000"
echo ""
echo "5. Login with admin credentials:"
echo "   Email: admin@gmail.com"
echo "   Password: admin123"
echo ""
