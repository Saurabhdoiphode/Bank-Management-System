// ==================== CONFIGURATION ====================
const API_BASE_URL = 'http://localhost:8080/api';

// ==================== DOM ELEMENTS ====================
const customerLoginForm = document.getElementById('customerLoginForm');
const customerRegisterForm = document.getElementById('customerRegisterForm');
const adminLoginForm = document.getElementById('adminLoginForm');
const tabButtons = document.querySelectorAll('.tab-btn');
const tabContents = document.querySelectorAll('.tab-content');
const toast = document.getElementById('toast');
const loadingSpinner = document.getElementById('loadingSpinner');

// ==================== EVENT LISTENERS ====================
tabButtons.forEach(button => {
    button.addEventListener('click', handleTabSwitch);
});

customerLoginForm.addEventListener('submit', handleCustomerLogin);
customerRegisterForm.addEventListener('submit', handleCustomerRegister);
adminLoginForm.addEventListener('submit', handleAdminLogin);

// ==================== TAB SWITCHING ====================
function handleTabSwitch(e) {
    const tabName = e.target.dataset.tab;
    
    // Update active button
    tabButtons.forEach(btn => btn.classList.remove('active'));
    e.target.classList.add('active');
    
    // Update active content
    tabContents.forEach(content => content.classList.remove('active'));
    document.getElementById(`${tabName}-tab`).classList.add('active');
}

// ==================== CUSTOMER LOGIN ====================
async function handleCustomerLogin(e) {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    try {
        showLoading(true);
        
        const response = await fetch(`${API_BASE_URL}/auth/customer/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            // Store customer details and token
            localStorage.setItem('token', data.token);
            localStorage.setItem('customerId', data.id || data.customerId);
            localStorage.setItem('customerName', data.name);
            localStorage.setItem('customerEmail', data.email);
            localStorage.setItem('accountNumber', data.accountNumber);
            
            showToast('Login successful! Redirecting...', 'success');
            
            setTimeout(() => {
                window.location.href = 'customer-dashboard.html';
            }, 1500);
        } else {
            showToast(data.message || 'Invalid email or password', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showToast('An error occurred. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

// ==================== CUSTOMER REGISTRATION ====================
async function handleCustomerRegister(e) {
    e.preventDefault();
    
    const fullName = document.getElementById('fullName').value;
    const email = document.getElementById('regEmail').value;
    const mobileNumber = document.getElementById('mobile').value;
    const address = document.getElementById('address').value;
    const aadhaarNumber = document.getElementById('aadhaar').value;
    const panNumber = document.getElementById('pan').value;
    const accountType = document.getElementById('accountType').value;
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    // Validation
    if (password !== confirmPassword) {
        showToast('Passwords do not match', 'error');
        return;
    }
    
    if (mobileNumber.length !== 10) {
        showToast('Mobile number must be 10 digits', 'error');
        return;
    }
    
    if (aadhaarNumber.length !== 12) {
        showToast('Aadhaar number must be 12 digits', 'error');
        return;
    }
    
    try {
        showLoading(true);
        
        const response = await fetch(`${API_BASE_URL}/auth/customer/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                fullName,
                email,
                mobileNumber,
                address,
                aadhaarNumber,
                panNumber,
                accountType,
                password
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showToast('Registration successful! Your account has been created.', 'success');
            
            // Clear form
            customerRegisterForm.reset();
            
            // Switch to login tab
            setTimeout(() => {
                tabButtons[0].click();
                showToast(`Your account number: ${data.accountNumber}`, 'success');
            }, 1500);
        } else {
            showToast(data.message || 'Registration failed', 'error');
        }
    } catch (error) {
        console.error('Registration error:', error);
        showToast('An error occurred. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

// ==================== ADMIN LOGIN ====================
async function handleAdminLogin(e) {
    e.preventDefault();
    
    const email = document.getElementById('adminEmail').value;
    const password = document.getElementById('adminPassword').value;
    
    try {
        showLoading(true);
        
        const response = await fetch(`${API_BASE_URL}/auth/admin/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            // Store admin token
            localStorage.setItem('adminToken', data.token);
            localStorage.setItem('adminName', data.name);
            localStorage.setItem('adminEmail', data.email);
            
            showToast('Admin login successful! Redirecting...', 'success');
            
            setTimeout(() => {
                window.location.href = 'admin-dashboard.html';
            }, 1500);
        } else {
            showToast(data.message || 'Invalid admin credentials', 'error');
            document.getElementById('adminLoginForm').classList.add('form-shake');
            setTimeout(() => {
                document.getElementById('adminLoginForm').classList.remove('form-shake');
            }, 500);
        }
    } catch (error) {
        console.error('Admin login error:', error);
        showToast('An error occurred. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

// ==================== UTILITY FUNCTIONS ====================

/**
 * Show toast notification
 * @param {string} message - Message to display
 * @param {string} type - Type of notification (success, error, warning)
 */
function showToast(message, type = 'info') {
    toast.textContent = message;
    toast.className = `toast show ${type}`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

/**
 * Show or hide loading spinner
 * @param {boolean} show - Whether to show or hide
 */
function showLoading(show) {
    if (show) {
        loadingSpinner.classList.add('show');
    } else {
        loadingSpinner.classList.remove('show');
    }
}

/**
 * Check if user is authenticated
 * @returns {boolean}
 */
function isCustomerAuthenticated() {
    return !!localStorage.getItem('token');
}

/**
 * Check if admin is authenticated
 * @returns {boolean}
 */
function isAdminAuthenticated() {
    return !!localStorage.getItem('adminToken');
}

/**
 * Get auth headers for API requests
 * @param {boolean} isAdmin - Whether to get admin headers
 * @returns {object}
 */
function getAuthHeaders(isAdmin = false) {
    const token = isAdmin ? localStorage.getItem('adminToken') : localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

/**
 * Logout current user/admin
 * @param {boolean} isAdmin - Whether to logout admin
 */
function logout(isAdmin = false) {
    if (isAdmin) {
        localStorage.removeItem('adminToken');
        localStorage.removeItem('adminName');
        localStorage.removeItem('adminEmail');
    } else {
        localStorage.removeItem('token');
        localStorage.removeItem('customerId');
        localStorage.removeItem('customerName');
        localStorage.removeItem('customerEmail');
        localStorage.removeItem('accountNumber');
    }
    window.location.href = 'index.html';
}

// ==================== PAGE ANIMATIONS ====================
document.addEventListener('DOMContentLoaded', () => {
    // Add fade-in animation to page
    document.body.classList.add('smooth-fade-in');
});

// Handle page unload animation
window.addEventListener('beforeunload', () => {
    document.body.classList.add('page-transition-out');
});

// ==================== FORM VALIDATION ====================

// Real-time email validation
document.getElementById('email').addEventListener('blur', validateEmail);
document.getElementById('regEmail').addEventListener('blur', validateEmail);
document.getElementById('adminEmail').addEventListener('blur', validateEmail);

function validateEmail(e) {
    const email = e.target.value;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    if (!emailRegex.test(email)) {
        showToast('Please enter a valid email address', 'warning');
    }
}

// Real-time password strength validation
document.getElementById('regPassword').addEventListener('input', checkPasswordStrength);

function checkPasswordStrength(e) {
    const password = e.target.value;
    const strength = calculatePasswordStrength(password);
    
    // You can add visual feedback here if needed
    if (strength === 'weak') {
        console.log('Weak password - consider using more characters');
    }
}

function calculatePasswordStrength(password) {
    let strength = 'weak';
    
    if (password.length >= 8) strength = 'medium';
    if (password.length >= 12 && /[A-Z]/.test(password) && /[0-9]/.test(password)) {
        strength = 'strong';
    }
    
    return strength;
}

// ==================== API ERROR HANDLING ====================

/**
 * Handle API errors globally
 * @param {Response} response - Fetch response object
 */
async function handleAPIError(response) {
    if (response.status === 401) {
        showToast('Session expired. Please login again.', 'error');
        logout();
    } else if (response.status === 403) {
        showToast('You do not have permission to access this resource.', 'error');
    } else if (response.status === 404) {
        showToast('Resource not found.', 'error');
    } else if (response.status === 500) {
        showToast('Server error. Please try again later.', 'error');
    }
}

// ==================== KEYBOARD SHORTCUTS ====================

document.addEventListener('keydown', (e) => {
    // Ctrl/Cmd + Enter to submit form
    if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        const activeForm = document.querySelector('form:visible');
        if (activeForm) {
            activeForm.dispatchEvent(new Event('submit'));
        }
    }
    
    // ESC to clear current form
    if (e.key === 'Escape') {
        document.querySelector('form').reset();
    }
});

console.log('Auth script loaded successfully');
