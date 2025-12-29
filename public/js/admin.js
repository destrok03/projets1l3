/**
 * Brasil Burger - Admin JavaScript
 */

document.addEventListener('DOMContentLoaded', function() {
    
    // ===========================================
    // Sidebar Toggle (Mobile)
    // ===========================================
    const sidebar = document.getElementById('sidebar');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebarToggleBtn = document.getElementById('sidebarToggleBtn');
    const sidebarOverlay = document.getElementById('sidebarOverlay');
    
    function openSidebar() {
        sidebar.classList.add('show');
        sidebarOverlay.classList.add('show');
        document.body.style.overflow = 'hidden';
    }
    
    function closeSidebar() {
        sidebar.classList.remove('show');
        sidebarOverlay.classList.remove('show');
        document.body.style.overflow = '';
    }
    
    if (sidebarToggleBtn) {
        sidebarToggleBtn.addEventListener('click', openSidebar);
    }
    
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', closeSidebar);
    }
    
    if (sidebarOverlay) {
        sidebarOverlay.addEventListener('click', closeSidebar);
    }
    
    // ===========================================
    // Auto-dismiss alerts
    // ===========================================
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            bsAlert.close();
        }, 5000);
    });
    
    // ===========================================
    // Confirm delete actions
    // ===========================================
    document.querySelectorAll('[data-confirm]').forEach(function(element) {
        element.addEventListener('click', function(e) {
            const message = this.dataset.confirm || 'Êtes-vous sûr ?';
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });
    
    // ===========================================
    // Loading state for buttons
    // ===========================================
    document.querySelectorAll('form').forEach(function(form) {
        form.addEventListener('submit', function() {
            const submitBtn = form.querySelector('[type="submit"]');
            if (submitBtn && !submitBtn.dataset.noLoading) {
                submitBtn.disabled = true;
                const originalHtml = submitBtn.innerHTML;
                submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Chargement...';
                submitBtn.dataset.originalHtml = originalHtml;
            }
        });
    });
    
    // ===========================================
    // Tooltip initialization
    // ===========================================
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipTriggerList.forEach(function(tooltipTriggerEl) {
        new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // ===========================================
    // Format numbers with spaces
    // ===========================================
    window.formatNumber = function(number) {
        return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
    };
    
    // ===========================================
    // AJAX helper
    // ===========================================
    window.ajaxRequest = async function(url, options = {}) {
        const defaultOptions = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest'
            }
        };
        
        const mergedOptions = { ...defaultOptions, ...options };
        
        try {
            const response = await fetch(url, mergedOptions);
            return await response.json();
        } catch (error) {
            console.error('AJAX Error:', error);
            throw error;
        }
    };
    
    // ===========================================
    // Date formatting
    // ===========================================
    window.formatDate = function(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };
    
    window.formatDateTime = function(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };
    
    // ===========================================
    // Notification toast
    // ===========================================
    window.showToast = function(message, type = 'success') {
        const toastContainer = document.getElementById('toast-container') || createToastContainer();
        
        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-white bg-${type === 'error' ? 'danger' : type} border-0`;
        toast.setAttribute('role', 'alert');
        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        `;
        
        toastContainer.appendChild(toast);
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
        
        toast.addEventListener('hidden.bs.toast', function() {
            toast.remove();
        });
    };
    
    function createToastContainer() {
        const container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '1100';
        document.body.appendChild(container);
        return container;
    }
    
    // ===========================================
    // Print functionality
    // ===========================================
    window.printElement = function(elementId) {
        const element = document.getElementById(elementId);
        if (!element) return;
        
        const printWindow = window.open('', '_blank');
        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <title>Impression</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
                <style>
                    body { padding: 20px; }
                    @media print { .no-print { display: none; } }
                </style>
            </head>
            <body>
                ${element.innerHTML}
            </body>
            </html>
        `);
        printWindow.document.close();
        printWindow.focus();
        setTimeout(function() {
            printWindow.print();
            printWindow.close();
        }, 250);
    };
    
    console.log('Brasil Burger Admin JS loaded');
});

// Theme toggle: set/get theme and persist in localStorage
(function() {
    function applyTheme(theme) {
        if (theme === 'dark') {
            document.documentElement.setAttribute('data-theme', 'dark');
            const icon = document.getElementById('themeIcon');
            if (icon) { icon.classList.remove('fa-sun'); icon.classList.add('fa-moon'); }
        } else {
            document.documentElement.removeAttribute('data-theme');
            const icon = document.getElementById('themeIcon');
            if (icon) { icon.classList.remove('fa-moon'); icon.classList.add('fa-sun'); }
        }
    }

    function toggleTheme() {
        const current = document.documentElement.getAttribute('data-theme');
        const next = current === 'dark' ? 'light' : 'dark';
        applyTheme(next === 'dark' ? 'dark' : 'light');
        try { localStorage.setItem('bb_theme', next); } catch (e) {}
    }

    // Initialize on DOMContentLoaded
    document.addEventListener('DOMContentLoaded', function() {
        try {
            const saved = localStorage.getItem('bb_theme');
            if (saved === 'dark') applyTheme('dark');
            else applyTheme('light');
        } catch (e) {}

        const btn = document.getElementById('themeToggle');
        if (btn) btn.addEventListener('click', toggleTheme);
    });
})();
