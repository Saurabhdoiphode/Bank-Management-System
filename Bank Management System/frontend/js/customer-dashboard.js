// ==================== CONFIGURATION ====================
const API_BASE_URL = 'http://localhost:8080/api';
let spendingChart = null;
let typesChart = null;

// ==================== DOM ELEMENTS ====================
const sidebar = document.querySelector('.sidebar');
const menuToggle = document.getElementById('menuToggle');
const navItems = document.querySelectorAll('.nav-item');
const sections = document.querySelectorAll('.section');
const logoutBtn = document.getElementById('logoutBtn');
const tabButtons = document.querySelectorAll('[data-section]');
const toast = document.getElementById('toast');
const loadingSpinner = document.getElementById('loadingSpinner');

// ==================== INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', () => {
    // Check authentication
    if (!isCustomerAuthenticated()) {
        window.location.href = 'index.html';
        return;
    }

    // Load customer data
    loadCustomerData();
    loadBalance();
    loadRecentTransactions();
    loadAllTransactions();
    initializeCharts();

    // Set up event listeners
    setupEventListeners();
});

// ==================== AUTHENTICATION CHECK ====================
function isCustomerAuthenticated() {
    return !!localStorage.getItem('token');
}

// ==================== EVENT LISTENERS ================== 
function setupEventListeners() {
    // Menu toggle
    menuToggle.addEventListener('click', () => {
        sidebar.classList.toggle('show');
    });

    // Navigation
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const section = item.dataset.section;
            navigateToSection(section);
            
            // Close sidebar on mobile
            if (window.innerWidth < 768) {
                sidebar.classList.remove('show');
            }
        });
    });

    // Logout
    logoutBtn.addEventListener('click', logout);

    // Quick action buttons
    document.getElementById('depositBtn').addEventListener('click', openDepositModal);
    document.getElementById('withdrawBtn').addEventListener('click', openWithdrawModal);
    document.getElementById('transferBtn').addEventListener('click', () => {
        navigateToSection('transfer');
    });

    // Modal close buttons
    document.querySelectorAll('.close-modal').forEach(btn => {
        btn.addEventListener('click', closeAllModals);
    });

    // Forms
    document.getElementById('depositForm').addEventListener('submit', handleDeposit);
    document.getElementById('withdrawForm').addEventListener('submit', handleWithdraw);
    document.getElementById('transferForm').addEventListener('submit', handleTransfer);
    document.getElementById('filterBtn').addEventListener('click', filterTransactions);
    document.getElementById('editProfileBtn').addEventListener('click', () => {
        showToast('Profile editing feature coming soon!', 'info');
    });
    document.getElementById('changePasswordBtn').addEventListener('click', () => {
        showToast('Change password feature coming soon!', 'info');
    });
}

// ==================== NAVIGATION ====================
function navigateToSection(section) {
    // Update active nav item
    navItems.forEach(item => item.classList.remove('active'));
    document.querySelector(`[data-section="${section}"]`).classList.add('active');

    // Update active section
    sections.forEach(sec => sec.classList.remove('active'));
    document.getElementById(`${section}-section`).classList.add('active');

    // Update title
    const titles = {
        dashboard: 'Dashboard',
        transactions: 'Transaction History',
        transfer: 'Send Money',
        profile: 'Profile Information',
        settings: 'Settings'
    };
    document.getElementById('sectionTitle').textContent = titles[section] || 'Dashboard';

    // Reload section-specific data
    if (section === 'transactions') {
        loadAllTransactions();
    }
}

// ==================== LOAD CUSTOMER DATA ====================
async function loadCustomerData() {
    try {
        const customerName = localStorage.getItem('customerName');
        const customerEmail = localStorage.getItem('customerEmail');
        const accountNumber = localStorage.getItem('accountNumber');

        // Update header
        document.getElementById('userName').textContent = customerName || 'Customer';
        document.getElementById('userEmail').textContent = customerEmail || 'email@example.com';

        // Update welcome section
        document.getElementById('welcomeName').textContent = customerName?.split(' ')[0] || 'Customer';

        // Update profile section
        document.getElementById('profileName').textContent = customerName || 'Customer';
        document.getElementById('profileEmail').textContent = customerEmail || 'email@example.com';
        document.getElementById('profileAccountNumber').textContent = accountNumber || '-';
        document.getElementById('accountNum').textContent = accountNumber || '-';

        // Fetch full customer details from backend
        const response = await fetch(`${API_BASE_URL}/customers/profile`, {
            headers: getAuthHeaders()
        });

        if (response.ok) {
            const data = await response.json();
            updateProfileData(data);
        }
    } catch (error) {
        console.error('Error loading customer data:', error);
    }
}

function updateProfileData(data) {
    if (data.mobileNumber) document.getElementById('profileMobile').textContent = data.mobileNumber;
    if (data.address) document.getElementById('profileAddress').textContent = data.address;
    if (data.accountType) document.getElementById('profileAccountType').textContent = data.accountType;
    if (data.createdAt) {
        const date = new Date(data.createdAt).toLocaleDateString();
        document.getElementById('profileJoinDate').textContent = date;
    }
}

// ==================== LOAD BALANCE ====================
async function loadBalance() {
    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/accounts/balance`, {
            headers: getAuthHeaders()
        });

        if (response.ok) {
            const data = await response.json();
            const balance = parseFloat(data.balance || 0).toFixed(2);
            document.getElementById('totalBalance').textContent = `₹${balance}`;
            document.getElementById('availableBalance').textContent = `₹${balance}`;
        }
    } catch (error) {
        console.error('Error loading balance:', error);
        showToast('Could not load balance', 'error');
    } finally {
        showLoading(false);
    }
}

// ==================== LOAD RECENT TRANSACTIONS ====================
async function loadRecentTransactions() {
    try {
        const response = await fetch(`${API_BASE_URL}/transactions/recent?limit=5`, {
            headers: getAuthHeaders()
        });

        if (response.ok) {
            const transactions = await response.json();
            displayRecentTransactions(transactions);
        }
    } catch (error) {
        console.error('Error loading recent transactions:', error);
    }
}

function displayRecentTransactions(transactions) {
    const tbody = document.getElementById('recentTransactionsList');

    if (transactions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="no-data">No transactions yet</td></tr>';
        return;
    }

    tbody.innerHTML = transactions.map(tx => `
        <tr>
            <td>${formatDate(tx.createdAt)}</td>
            <td>${tx.description || tx.type}</td>
            <td><span class="type-badge">${tx.type}</span></td>
            <td class="amount ${tx.type === 'DEPOSIT' || tx.type === 'TRANSFER_RECEIVED' ? 'success' : 'danger'}">
                ${tx.type === 'DEPOSIT' || tx.type === 'TRANSFER_RECEIVED' ? '+' : '-'}₹${Math.abs(tx.amount).toFixed(2)}
            </td>
            <td><span class="status-badge ${tx.status.toLowerCase()}">${tx.status}</span></td>
        </tr>
    `).join('');
}

// ==================== LOAD ALL TRANSACTIONS ====================
async function loadAllTransactions() {
    try {
        const response = await fetch(`${API_BASE_URL}/transactions/all`, {
            headers: getAuthHeaders()
        });

        if (response.ok) {
            const transactions = await response.json();
            displayAllTransactions(transactions);
        }
    } catch (error) {
        console.error('Error loading all transactions:', error);
        showToast('Could not load transactions', 'error');
    }
}

function displayAllTransactions(transactions) {
    const tbody = document.getElementById('allTransactionsList');

    if (transactions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="no-data">No transactions found</td></tr>';
        return;
    }

    tbody.innerHTML = transactions.map(tx => {
        const date = new Date(tx.createdAt);
        return `
            <tr>
                <td>${date.toLocaleDateString()}</td>
                <td>${date.toLocaleTimeString()}</td>
                <td>${tx.description || tx.type}</td>
                <td>${tx.type}</td>
                <td class="${tx.type === 'DEPOSIT' || tx.type === 'TRANSFER_RECEIVED' ? 'success' : 'danger'}">
                    ${tx.type === 'DEPOSIT' || tx.type === 'TRANSFER_RECEIVED' ? '+' : '-'}₹${Math.abs(tx.amount).toFixed(2)}
                </td>
                <td>₹${tx.balanceAfter.toFixed(2)}</td>
                <td><span class="status-badge ${tx.status.toLowerCase()}">${tx.status}</span></td>
                <td>${tx.referenceId || '-'}</td>
            </tr>
        `;
    }).join('');
}

// ==================== FILTER TRANSACTIONS ====================
function filterTransactions() {
    const type = document.getElementById('filterType').value;
    const date = document.getElementById('filterDate').value;

    // Implementation for filtering
    showToast('Filter functionality will be implemented soon', 'info');
}

// ==================== DEPOSIT ====================
function openDepositModal() {
    document.getElementById('depositModal').classList.add('show');
}

async function handleDeposit(e) {
    e.preventDefault();

    const amount = document.getElementById('depositAmount').value;
    const method = document.getElementById('depositMethod').value;

    if (!amount || !method) {
        showToast('Please fill all fields', 'error');
        return;
    }

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/transactions/deposit`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ amount: parseFloat(amount), method })
        });

        const data = await response.json();

        if (response.ok) {
            showToast('Deposit successful!', 'success');
            document.getElementById('depositForm').reset();
            closeAllModals();
            loadBalance();
            loadRecentTransactions();
        } else {
            showToast(data.message || 'Deposit failed', 'error');
        }
    } catch (error) {
        console.error('Deposit error:', error);
        showToast('An error occurred', 'error');
    } finally {
        showLoading(false);
    }
}

// ==================== WITHDRAW ====================
function openWithdrawModal() {
    document.getElementById('withdrawModal').classList.add('show');
}

async function handleWithdraw(e) {
    e.preventDefault();

    const amount = document.getElementById('withdrawAmount').value;
    const type = document.getElementById('withdrawType').value;

    if (!amount || !type) {
        showToast('Please fill all fields', 'error');
        return;
    }

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/transactions/withdraw`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ amount: parseFloat(amount), withdrawType: type })
        });

        const data = await response.json();

        if (response.ok) {
            showToast('Withdrawal successful!', 'success');
            document.getElementById('withdrawForm').reset();
            closeAllModals();
            loadBalance();
            loadRecentTransactions();
        } else {
            showToast(data.message || 'Withdrawal failed', 'error');
        }
    } catch (error) {
        console.error('Withdraw error:', error);
        showToast('An error occurred', 'error');
    } finally {
        showLoading(false);
    }
}

// ==================== TRANSFER ====================
async function handleTransfer(e) {
    e.preventDefault();

    const recipientAccount = document.getElementById('recipientAccount').value;
    const amount = document.getElementById('transferAmount').value;
    const description = document.getElementById('transferDescription').value;

    if (!recipientAccount || !amount) {
        showToast('Please fill all required fields', 'error');
        return;
    }

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/transactions/transfer`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({
                recipientAccountNumber: recipientAccount,
                amount: parseFloat(amount),
                description
            })
        });

        const data = await response.json();

        if (response.ok) {
            showToast('Transfer successful!', 'success');
            document.getElementById('transferForm').reset();
            navigateToSection('dashboard');
            loadBalance();
            loadRecentTransactions();
        } else {
            showToast(data.message || 'Transfer failed', 'error');
        }
    } catch (error) {
        console.error('Transfer error:', error);
        showToast('An error occurred', 'error');
    } finally {
        showLoading(false);
    }
}

// ==================== CHARTS ====================
function initializeCharts() {
    // Monthly Spending Chart
    const spendingCtx = document.getElementById('spendingChart');
    if (spendingCtx) {
        spendingChart = new Chart(spendingCtx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                datasets: [{
                    label: 'Monthly Spending',
                    data: [2500, 3200, 2800, 3500, 4200, 3800],
                    borderColor: '#6366f1',
                    backgroundColor: 'rgba(99, 102, 241, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#6366f1',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2,
                    pointRadius: 5
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            color: 'rgba(255, 255, 255, 0.7)',
                            callback: (value) => `₹${value}`
                        },
                        grid: {
                            color: 'rgba(255, 255, 255, 0.05)'
                        }
                    },
                    x: {
                        ticks: {
                            color: 'rgba(255, 255, 255, 0.7)'
                        },
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    // Transaction Types Chart
    const typesCtx = document.getElementById('typesChart');
    if (typesCtx) {
        typesChart = new Chart(typesCtx, {
            type: 'doughnut',
            data: {
                labels: ['Deposits', 'Withdrawals', 'Transfers'],
                datasets: [{
                    data: [35, 25, 40],
                    backgroundColor: [
                        '#10b981',
                        '#ef4444',
                        '#6366f1'
                    ],
                    borderColor: 'rgba(255, 255, 255, 0.1)',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            color: 'rgba(255, 255, 255, 0.7)',
                            padding: 20
                        }
                    }
                }
            }
        });
    }
}

// ==================== UTILITY FUNCTIONS ====================
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('customerId');
    localStorage.removeItem('customerName');
    localStorage.removeItem('customerEmail');
    localStorage.removeItem('accountNumber');
    window.location.href = 'index.html';
}

function showToast(message, type = 'info') {
    toast.textContent = message;
    toast.className = `toast show ${type}`;

    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function showLoading(show) {
    if (show) {
        loadingSpinner.classList.add('show');
    } else {
        loadingSpinner.classList.remove('show');
    }
}

function closeAllModals() {
    document.querySelectorAll('.modal').forEach(modal => {
        modal.classList.remove('show');
    });
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// ==================== RESPONSIVE ================== 
window.addEventListener('resize', () => {
    if (window.innerWidth > 768) {
        sidebar.classList.remove('show');
    }
});

console.log('Customer Dashboard loaded successfully');
