// ==================== CONFIGURATION ====================
const API_BASE_URL = 'http://localhost:8080/api';
let dailyTransactionsChart = null;
let userGrowthChart = null;
let depositWithdrawalChart = null;
let transactionTypesChart = null;

// ==================== DOM ELEMENTS ====================
const sidebar = document.querySelector('.sidebar');
const menuToggle = document.getElementById('menuToggle');
const navItems = document.querySelectorAll('.nav-item');
const sections = document.querySelectorAll('.section');
const logoutBtn = document.getElementById('logoutBtn');
const toast = document.getElementById('toast');
const loadingSpinner = document.getElementById('loadingSpinner');
const searchBox = document.getElementById('searchBox');

// ==================== INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM loaded, checking authentication...');
    // Check authentication
    if (!isAdminAuthenticated()) {
        console.log('Not authenticated, redirecting to index.html');
        window.location.href = 'index.html';
        return;
    }

    console.log('Authenticated, loading admin data...');
    // Load admin data
    loadAdminData();
    loadDashboardStats();
    loadCustomers();
    loadTransactions();
    loadApprovals();
    initializeCharts();

    // Set up event listeners
    setupEventListeners();
});

// ==================== AUTHENTICATION CHECK ====================
function isAdminAuthenticated() {
    const token = localStorage.getItem('adminToken');
    console.log('Admin token:', token ? 'present' : 'not found');
    return !!token;
}

// ==================== EVENT LISTENERS ====================
function setupEventListeners() {
    console.log('Setting up event listeners...');

    // Menu toggle
    if (menuToggle) {
        menuToggle.addEventListener('click', () => {
            console.log('Menu toggle clicked');
            sidebar.classList.toggle('show');
        });
    } else {
        console.error('Menu toggle element not found');
    }

    // Navigation
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const section = item.dataset.section;
            console.log('Nav item clicked:', section);
            navigateToSection(section);

            // Close sidebar on mobile
            if (window.innerWidth < 768) {
                sidebar.classList.remove('show');
            }
        });
    });

    // Logout
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logoutAdmin);
    } else {
        console.error('Logout button not found');
    }

    // Search
    if (searchBox) {
        searchBox.addEventListener('input', searchCustomers);
    }

    // Filter buttons
    const filterCustomersBtn = document.getElementById('filterCustomersBtn');
    if (filterCustomersBtn) {
        filterCustomersBtn.addEventListener('click', filterCustomers);
    }

    const filterTransactionsBtn = document.getElementById('filterTransactionsBtn');
    if (filterTransactionsBtn) {
        filterTransactionsBtn.addEventListener('click', filterTransactions);
    }

    const filterApprovalsBtn = document.getElementById('filterApprovalsBtn');
    if (filterApprovalsBtn) {
        filterApprovalsBtn.addEventListener('click', filterApprovals);
    }

    // Modal close
    document.querySelectorAll('.close-modal').forEach(btn => {
        btn.addEventListener('click', closeAllModals);
    });

    // Settings
    const saveSettingsBtn = document.getElementById('saveSettingsBtn');
    if (saveSettingsBtn) {
        saveSettingsBtn.addEventListener('click', saveSettings);
    }

    console.log('Event listeners setup complete');
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
        customers: 'Customer Management',
        transactions: 'Transaction Management',
        analytics: 'Banking Analytics',
        approvals: 'Account Approvals',
        settings: 'Admin Settings'
    };
    document.getElementById('sectionTitle').textContent = titles[section] || 'Dashboard';

    // Reload section-specific data
    if (section === 'customers') {
        loadCustomers();
    } else if (section === 'transactions') {
        loadTransactions();
    } else if (section === 'analytics') {
        initializeAnalyticsCharts();
    } else if (section === 'approvals') {
        loadApprovals();
    }
}

// ==================== LOAD ADMIN DATA ====================
function loadAdminData() {
    const adminName = localStorage.getItem('adminName');
    document.getElementById('adminName').textContent = adminName || 'Administrator';
}

// ==================== LOAD DASHBOARD STATS ====================
async function loadDashboardStats() {
    console.log('Loading dashboard stats...');
    try {
        const [usersRes, transactionsRes, balanceRes, approvalsRes] = await Promise.all([
            fetch(`${API_BASE_URL}/admin/stats/users`, { headers: getAdminAuthHeaders() }),
            fetch(`${API_BASE_URL}/admin/stats/transactions`, { headers: getAdminAuthHeaders() }),
            fetch(`${API_BASE_URL}/admin/stats/balance`, { headers: getAdminAuthHeaders() }),
            fetch(`${API_BASE_URL}/admin/stats/pending-approvals`, { headers: getAdminAuthHeaders() })
        ]);

        console.log('API responses:', {
            users: usersRes.status,
            transactions: transactionsRes.status,
            balance: balanceRes.status,
            approvals: approvalsRes.status
        });

        if (usersRes.ok) {
            const data = await usersRes.json();
            console.log('Users data:', data);
            document.getElementById('totalUsers').textContent = data.total || 0;
        }

        if (transactionsRes.ok) {
            const data = await transactionsRes.json();
            console.log('Transactions data:', data);
            document.getElementById('totalTransactions').textContent = data.total || 0;
        }

        if (balanceRes.ok) {
            const data = await balanceRes.json();
            console.log('Balance data:', data);
            const balance = parseFloat(data.totalBalance || 0).toFixed(2);
            document.getElementById('totalBankBalance').textContent = `₹${balance}`;
        }

        if (approvalsRes.ok) {
            const data = await approvalsRes.json();
            console.log('Approvals data:', data);
            document.getElementById('pendingApprovals').textContent = data.pending || 0;
        }
    } catch (error) {
        console.error('Error loading dashboard stats:', error);
    }
}

// ==================== LOAD CUSTOMERS ====================
async function loadCustomers() {
    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/admin/customers`, {
            headers: getAdminAuthHeaders()
        });

        if (response.ok) {
            const customers = await response.json();
            displayCustomers(customers);
        }
    } catch (error) {
        console.error('Error loading customers:', error);
        showToast('Could not load customers', 'error');
    } finally {
        showLoading(false);
    }
}

function displayCustomers(customers) {
    const tbody = document.getElementById('customersList');

    if (customers.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="no-data">No customers found</td></tr>';
        return;
    }

    tbody.innerHTML = customers.map(customer => `
        <tr>
            <td>${customer.id || '-'}</td>
            <td>${customer.fullName || '-'}</td>
            <td>${customer.email || '-'}</td>
            <td>${customer.mobileNumber || '-'}</td>
            <td>${customer.accountType || '-'}</td>
            <td>₹${(customer.balance || 0).toFixed(2)}</td>
            <td><span class="status-badge success">Active</span></td>
            <td>${formatDate(customer.createdAt)}</td>
            <td>
                <button class="btn-action view-btn" onclick="viewCustomer('${customer.id}')">View</button>
                <button class="btn-action delete-btn" onclick="deleteCustomer('${customer.id}')">Delete</button>
            </td>
        </tr>
    `).join('');
}

// ==================== SEARCH CUSTOMERS ====================
function searchCustomers() {
    const query = searchBox.value.toLowerCase();
    const rows = document.querySelectorAll('#customersList tr');

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(query) ? '' : 'none';
    });
}

// ==================== FILTER CUSTOMERS ====================
async function filterCustomers() {
    const search = document.getElementById('customerSearch').value;
    const accountType = document.getElementById('accountTypeFilter').value;

    try {
        const params = new URLSearchParams();
        if (search) params.append('search', search);
        if (accountType) params.append('accountType', accountType);

        const response = await fetch(`${API_BASE_URL}/admin/customers?${params}`, {
            headers: getAdminAuthHeaders()
        });

        if (response.ok) {
            const customers = await response.json();
            displayCustomers(customers);
            showToast('Customers filtered successfully', 'success');
        }
    } catch (error) {
        console.error('Error filtering customers:', error);
        showToast('Error filtering customers', 'error');
    }
}

// ==================== DELETE CUSTOMER ====================
async function deleteCustomer(customerId) {
    if (!confirm('Are you sure you want to delete this customer?')) {
        return;
    }

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/admin/customers/${customerId}`, {
            method: 'DELETE',
            headers: getAdminAuthHeaders()
        });

        if (response.ok) {
            showToast('Customer deleted successfully', 'success');
            loadCustomers();
        } else {
            showToast('Failed to delete customer', 'error');
        }
    } catch (error) {
        console.error('Error deleting customer:', error);
        showToast('An error occurred', 'error');
    } finally {
        showLoading(false);
    }
}

// ==================== LOAD TRANSACTIONS ====================
async function loadTransactions() {
    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/admin/transactions`, {
            headers: getAdminAuthHeaders()
        });

        if (response.ok) {
            const transactions = await response.json();
            displayTransactions(transactions);
        }
    } catch (error) {
        console.error('Error loading transactions:', error);
        showToast('Could not load transactions', 'error');
    } finally {
        showLoading(false);
    }
}

function displayTransactions(transactions) {
    const tbody = document.getElementById('transactionsList');

    if (transactions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="no-data">No transactions found</td></tr>';
        return;
    }

    tbody.innerHTML = transactions.map(tx => {
        const date = new Date(tx.createdAt);
        return `
            <tr>
                <td>${tx.id || '-'}</td>
                <td>${tx.accountNumber || '-'}</td>
                <td>${tx.customerName || '-'}</td>
                <td>${tx.type || '-'}</td>
                <td class="${tx.type === 'DEPOSIT' ? 'success' : 'danger'}">
                    ${tx.type === 'DEPOSIT' || tx.type === 'TRANSFER_RECEIVED' ? '+' : '-'}₹${Math.abs(tx.amount).toFixed(2)}
                </td>
                <td>₹${tx.balanceAfter.toFixed(2)}</td>
                <td><span class="status-badge ${tx.status.toLowerCase()}">${tx.status}</span></td>
                <td>${date.toLocaleDateString()} ${date.toLocaleTimeString()}</td>
                <td>
                    <button class="btn-action view-btn" onclick="viewTransaction('${tx.id}')">View</button>
                </td>
            </tr>
        `;
    }).join('');
}

// ==================== FILTER TRANSACTIONS ====================
function filterTransactions() {
    const type = document.getElementById('transactionTypeFilter').value;
    const date = document.getElementById('transactionDateFilter').value;

    showToast('Advanced filtering will be implemented soon', 'info');
}

// ==================== LOAD APPROVALS ====================
async function loadApprovals() {
    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/admin/approvals`, {
            headers: getAdminAuthHeaders()
        });

        if (response.ok) {
            const approvals = await response.json();
            displayApprovals(approvals);
        }
    } catch (error) {
        console.error('Error loading approvals:', error);
        showToast('Could not load approvals', 'error');
    } finally {
        showLoading(false);
    }
}

function displayApprovals(approvals) {
    const tbody = document.getElementById('approvalsList');

    if (approvals.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="no-data">No approval requests found</td></tr>';
        return;
    }

    tbody.innerHTML = approvals.map(approval => `
        <tr>
            <td>${approval.customerName || '-'}</td>
            <td>${approval.email || '-'}</td>
            <td>${approval.accountType || '-'}</td>
            <td>${approval.aadhaarNumber ? approval.aadhaarNumber.slice(-4) : '-'}</td>
            <td>${approval.panNumber || '-'}</td>
            <td><span class="status-badge ${approval.status.toLowerCase()}">${approval.status}</span></td>
            <td>${formatDate(approval.createdAt)}</td>
            <td>
                <button class="btn-action" onclick="approveAccount('${approval.id}')">Approve</button>
                <button class="btn-action delete-btn" onclick="rejectAccount('${approval.id}')">Reject</button>
            </td>
        </tr>
    `).join('');
}

// ==================== APPROVE ACCOUNT ====================
async function approveAccount(approvalId) {
    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/admin/approvals/${approvalId}/approve`, {
            method: 'PUT',
            headers: getAdminAuthHeaders()
        });

        if (response.ok) {
            showToast('Account approved successfully', 'success');
            loadApprovals();
            loadDashboardStats();
        } else {
            showToast('Failed to approve account', 'error');
        }
    } catch (error) {
        console.error('Error approving account:', error);
        showToast('An error occurred', 'error');
    } finally {
        showLoading(false);
    }
}

// ==================== REJECT ACCOUNT ====================
async function rejectAccount(approvalId) {
    if (!confirm('Are you sure you want to reject this account?')) {
        return;
    }

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/admin/approvals/${approvalId}/reject`, {
            method: 'PUT',
            headers: getAdminAuthHeaders()
        });

        if (response.ok) {
            showToast('Account rejected', 'success');
            loadApprovals();
        } else {
            showToast('Failed to reject account', 'error');
        }
    } catch (error) {
        console.error('Error rejecting account:', error);
        showToast('An error occurred', 'error');
    } finally {
        showLoading(false);
    }
}

// ==================== CHARTS ====================
function initializeCharts() {
    // Daily Transactions Chart
    const dailyCtx = document.getElementById('dailyTransactionsChart');
    if (dailyCtx) {
        dailyTransactionsChart = new Chart(dailyCtx, {
            type: 'bar',
            data: {
                labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                datasets: [{
                    label: 'Transactions',
                    data: [12, 19, 3, 5, 2, 3, 7],
                    backgroundColor: '#6366f1',
                    borderColor: '#6366f1',
                    borderRadius: 5,
                    borderSkipped: false
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
                            color: 'rgba(255, 255, 255, 0.7)'
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

    // User Growth Chart
    const growthCtx = document.getElementById('userGrowthChart');
    if (growthCtx) {
        userGrowthChart = new Chart(growthCtx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                datasets: [{
                    label: 'New Users',
                    data: [10, 25, 40, 60, 85, 110],
                    borderColor: '#10b981',
                    backgroundColor: 'rgba(16, 185, 129, 0.1)',
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#10b981',
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
                            color: 'rgba(255, 255, 255, 0.7)'
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
}

function initializeAnalyticsCharts() {
    // Deposit vs Withdrawal Chart
    const depositCtx = document.getElementById('depositWithdrawalChart');
    if (depositCtx && !depositWithdrawalChart) {
        depositWithdrawalChart = new Chart(depositCtx, {
            type: 'bar',
            data: {
                labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
                datasets: [
                    {
                        label: 'Deposits',
                        data: [5000, 6000, 7000, 8000],
                        backgroundColor: '#10b981'
                    },
                    {
                        label: 'Withdrawals',
                        data: [3000, 3500, 4000, 4500],
                        backgroundColor: '#ef4444'
                    }
                ]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
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
    const typesCtx = document.getElementById('transactionTypesChart');
    if (typesCtx && !transactionTypesChart) {
        transactionTypesChart = new Chart(typesCtx, {
            type: 'doughnut',
            data: {
                labels: ['Deposits', 'Withdrawals', 'Transfers'],
                datasets: [{
                    data: [40, 30, 30],
                    backgroundColor: ['#10b981', '#ef4444', '#6366f1'],
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
                            color: 'rgba(255, 255, 255, 0.7)'
                        }
                    }
                }
            }
        });
    }
}

// ==================== SETTINGS ====================
function saveSettings() {
    showToast('Settings saved successfully', 'success');
}

// ==================== UTILITY FUNCTIONS ====================
function getAdminAuthHeaders() {
    const token = localStorage.getItem('adminToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

function logoutAdmin() {
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminName');
    localStorage.removeItem('adminEmail');
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
    console.log('showLoading called with:', show);
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
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN');
}

function viewCustomer(customerId) {
    showToast(`Viewing customer ${customerId}`, 'info');
}

function viewTransaction(transactionId) {
    showToast(`Viewing transaction ${transactionId}`, 'info');
}

// ==================== RESPONSIVE ====================
window.addEventListener('resize', () => {
    if (window.innerWidth > 768) {
        sidebar.classList.remove('show');
    }
});

console.log('Admin Dashboard loaded successfully');
