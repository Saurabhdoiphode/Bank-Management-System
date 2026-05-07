#!/bin/bash

# ==================== BANK MANAGEMENT SYSTEM - API TEST SCRIPT ====================
# This script contains curl commands to test all API endpoints
# Modify variables as needed and run commands individually or in sequence

# ==================== CONFIGURATION ====================
BASE_URL="http://localhost:8080"
CUSTOMER_TOKEN=""
ADMIN_TOKEN=""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}================================================${NC}"
echo -e "${YELLOW}Bank Management System - API Test Script${NC}"
echo -e "${YELLOW}================================================${NC}\n"

# ==================== AUTHENTICATION TESTS ====================
echo -e "${YELLOW}1. CUSTOMER REGISTRATION${NC}"
curl -X POST "$BASE_URL/api/auth/customer/register" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "SecurePass123",
    "mobileNumber": "9876543210",
    "address": "123 Main Street, City",
    "aadhaarNumber": "123456789012",
    "panNumber": "ABCDE1234F",
    "accountType": "SAVINGS"
  }' | jq '.'

echo -e "\n${YELLOW}2. CUSTOMER LOGIN${NC}"
RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/customer/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123"
  }')
echo "$RESPONSE" | jq '.'

# Extract token from response
CUSTOMER_TOKEN=$(echo "$RESPONSE" | jq -r '.token')
echo -e "\n${GREEN}Customer Token: $CUSTOMER_TOKEN${NC}\n"

echo -e "${YELLOW}3. ADMIN LOGIN${NC}"
RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/admin/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@gmail.com",
    "password": "admin123"
  }')
echo "$RESPONSE" | jq '.'

# Extract admin token
ADMIN_TOKEN=$(echo "$RESPONSE" | jq -r '.token')
echo -e "\n${GREEN}Admin Token: $ADMIN_TOKEN${NC}\n"

# ==================== ACCOUNT TESTS ====================
echo -e "${YELLOW}4. GET ACCOUNT BALANCE (Customer)${NC}"
curl -s -X GET "$BASE_URL/api/accounts/balance" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'

echo -e "\n${YELLOW}5. GET CUSTOMER PROFILE${NC}"
curl -s -X GET "$BASE_URL/api/customers/profile" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'

# ==================== TRANSACTION TESTS ====================
echo -e "\n${YELLOW}6. DEPOSIT MONEY${NC}"
curl -s -X POST "$BASE_URL/api/transactions/deposit" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000.00,
    "method": "UPI",
    "description": "Deposit via UPI"
  }' | jq '.'

echo -e "\n${YELLOW}7. WITHDRAW MONEY${NC}"
curl -s -X POST "$BASE_URL/api/transactions/withdraw" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000.00,
    "withdrawType": "ATM",
    "description": "ATM Withdrawal"
  }' | jq '.'

# ==================== TRANSFER TEST (Requires second customer) ====================
echo -e "\n${YELLOW}8. TRANSFER MONEY${NC}"
echo -e "${YELLOW}(Note: First create another customer account)${NC}"
curl -s -X POST "$BASE_URL/api/transactions/transfer" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "recipientAccountNumber": "SB987654321",
    "amount": 2000.00,
    "description": "Payment for services"
  }' | jq '.'

# ==================== TRANSACTION HISTORY ====================
echo -e "\n${YELLOW}9. GET RECENT TRANSACTIONS${NC}"
curl -s -X GET "$BASE_URL/api/transactions/recent?limit=5" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'

echo -e "\n${YELLOW}10. GET ALL TRANSACTIONS (Customer)${NC}"
curl -s -X GET "$BASE_URL/api/transactions/all" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'

# ==================== ADMIN TESTS ====================
echo -e "\n${YELLOW}11. GET ALL CUSTOMERS (Admin)${NC}"
curl -s -X GET "$BASE_URL/api/admin/customers" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

echo -e "\n${YELLOW}12. SEARCH CUSTOMERS (Admin)${NC}"
curl -s -X GET "$BASE_URL/api/admin/customers/search?search=John" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

echo -e "\n${YELLOW}13. GET DASHBOARD STATISTICS (Admin)${NC}"
curl -s -X GET "$BASE_URL/api/admin/stats/dashboard" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

echo -e "\n${YELLOW}14. GET ALL TRANSACTIONS (Admin)${NC}"
curl -s -X GET "$BASE_URL/api/admin/transactions" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

echo -e "\n${YELLOW}15. GET PENDING APPROVALS (Admin)${NC}"
curl -s -X GET "$BASE_URL/api/admin/approvals" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

# ==================== STATISTICS ====================
echo -e "\n${YELLOW}16. GET TOTAL USERS STAT${NC}"
curl -s -X GET "$BASE_URL/api/admin/stats/users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

echo -e "\n${YELLOW}17. GET TOTAL TRANSACTIONS STAT${NC}"
curl -s -X GET "$BASE_URL/api/admin/stats/transactions" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

echo -e "\n${YELLOW}18. GET TOTAL BALANCE STAT${NC}"
curl -s -X GET "$BASE_URL/api/admin/stats/balance" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

# ==================== ERROR HANDLING TESTS ====================
echo -e "\n${YELLOW}19. TEST INVALID LOGIN${NC}"
curl -s -X POST "$BASE_URL/api/auth/customer/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "invalid@example.com",
    "password": "wrongpassword"
  }' | jq '.'

echo -e "\n${YELLOW}20. TEST INSUFFICIENT BALANCE${NC}"
curl -s -X POST "$BASE_URL/api/transactions/withdraw" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 999999999.00,
    "withdrawType": "ATM",
    "description": "Large withdrawal"
  }' | jq '.'

echo -e "\n${GREEN}================================================${NC}"
echo -e "${GREEN}API Tests Complete!${NC}"
echo -e "${GREEN}================================================${NC}\n"

# ==================== NOTES ====================
echo -e "${YELLOW}NOTES:${NC}"
echo "1. Ensure MongoDB is running on localhost:27017"
echo "2. Ensure backend is running on http://localhost:8080"
echo "3. Ensure jq is installed for JSON formatting"
echo "4. Update email addresses for registration tests"
echo "5. Keep tokens from login responses for authenticated requests"
echo ""
echo -e "${YELLOW}USEFUL CURL OPTIONS:${NC}"
echo "  -H : Add header"
echo "  -d : Send data"
echo "  -X : Specify HTTP method (GET, POST, PUT, DELETE)"
echo "  -v : Verbose output"
echo "  -i : Include headers in output"
echo ""
