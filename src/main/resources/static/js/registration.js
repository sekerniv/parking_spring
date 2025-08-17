// Registration page JavaScript
console.log('Registration page detected, setting up handlers...');

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM fully loaded on registration page');
    
    const form = document.getElementById('registrationForm');
    const button = document.getElementById('registerBtn');
    
    console.log('Form element:', form);
    console.log('Button element:', button);
    
    if (form) {
        form.addEventListener('submit', function(e) {
            console.log('Form submit event triggered');
            e.preventDefault();
            handleRegistration();
        });
    }
    
    if (button) {
        console.log('Button found, adding click listener');
        button.addEventListener('click', function(e) {
            console.log('Button click event triggered');
            e.preventDefault();
            handleRegistration();
        });
        
        // Test if button is clickable
        button.onclick = function() {
            console.log('Button onclick triggered');
        };
    } else {
        console.error('Button element not found!');
    }
});

// Handle form submission
function handleRegistration() {
    console.log('Registration handler called');
    console.log('Firebase config:', window.firebaseConfig);
    console.log('Firebase apps:', firebase.apps);
    console.log('Firebase object:', typeof firebase);
    console.log('firebase.auth:', typeof firebase.auth);
    
    // Check if Firebase is initialized
    if (!firebase.apps.length) {
        showError('Firebase is not initialized. Please check your configuration.');
        return;
    }
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    console.log('Registration data:', { email, password: password ? '***' : 'empty', confirmPassword: confirmPassword ? '***' : 'empty' });
    
    // Hide any existing messages
    hideMessages();
    
    // Validate passwords match
    if (password !== confirmPassword) {
        showError('Passwords do not match');
        return;
    }
    
    // Validate password length
    if (password.length < 6) {
        showError('Password must be at least 6 characters long');
        return;
    }
    
    // Disable button and show loading state
    const registerBtn = document.getElementById('registerBtn');
    const originalText = registerBtn.innerHTML;
    registerBtn.disabled = true;
    registerBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Creating Account...';
    
    // Create user with Firebase Auth
    firebase.auth().createUserWithEmailAndPassword(email, password)
        .then((userCredential) => {
            const user = userCredential.user;
            showSuccess('Account created successfully! Redirecting to login...');
            
            // Redirect to login after a short delay
            setTimeout(() => {
                window.location.href = '/login?message=Account created successfully! You can now sign in.';
            }, 2000);
        })
        .catch((error) => {
            console.log('Firebase error:', error);
            console.log('Error code:', error.code);
            console.log('Error message:', error.message);
            
            let errorMessage = 'An error occurred during registration';
            
            switch (error.code) {
                case 'auth/email-already-in-use':
                    errorMessage = 'An account with this email already exists';
                    break;
                case 'auth/invalid-email':
                    errorMessage = 'Please enter a valid email address';
                    break;
                case 'auth/weak-password':
                    errorMessage = 'Password is too weak. Please choose a stronger password (at least 6 characters)';
                    break;
                case 'auth/network-request-failed':
                    errorMessage = 'Network error. Please check your internet connection';
                    break;
                case 'auth/operation-not-allowed':
                    errorMessage = 'Email/password accounts are not enabled. Please contact support.';
                    break;
                default:
                    errorMessage = `Registration failed: ${error.message}`;
            }
            
            showError(errorMessage);
        })
        .finally(() => {
            // Re-enable button
            registerBtn.disabled = false;
            registerBtn.innerHTML = originalText;
        });
}

function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    const errorText = document.getElementById('errorText');
    errorText.textContent = message;
    errorDiv.classList.remove('d-none');
    errorDiv.scrollIntoView({ behavior: 'smooth' });
}

function showSuccess(message) {
    const successDiv = document.getElementById('successMessage');
    const successText = document.getElementById('successText');
    successText.textContent = message;
    successDiv.classList.remove('d-none');
    successDiv.scrollIntoView({ behavior: 'smooth' });
}

function hideMessages() {
    document.getElementById('errorMessage').classList.add('d-none');
    document.getElementById('successMessage').classList.add('d-none');
}
