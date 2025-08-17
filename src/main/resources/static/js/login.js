// Login page JavaScript
console.log('Login page detected, setting up handlers...');

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM fully loaded on login page');
    
    const form = document.getElementById('loginForm');
    const button = document.getElementById('loginBtn');
    
    console.log('Login form element:', form);
    console.log('Login button element:', button);
    
    if (form) {
        form.addEventListener('submit', function(e) {
            console.log('Login form submit event triggered');
            e.preventDefault();
            handleLogin();
        });
    }
    
    if (button) {
        console.log('Login button found, adding click listener');
        button.addEventListener('click', function(e) {
            console.log('Login button click event triggered');
            e.preventDefault();
            handleLogin();
        });
    } else {
        console.error('Login button element not found!');
    }
});

// Handle login form submission
function handleLogin() {
    console.log('Login handler called');
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    console.log('Login data:', { email, password: password ? '***' : 'empty' });
    
    // Hide any existing messages
    hideLoginMessages();
    
    // Disable button and show loading state
    const loginBtn = document.getElementById('loginBtn');
    const originalText = loginBtn.innerHTML;
    loginBtn.disabled = true;
    loginBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Signing In...';
    
    // Sign in with Firebase Auth
    firebase.auth().signInWithEmailAndPassword(email, password)
        .then((userCredential) => {
            const user = userCredential.user;
            console.log('Firebase login successful:', user.email);
            
            // Get the ID token
            return user.getIdToken();
        })
        .then((idToken) => {
            console.log('Got ID token, sending to backend...');
            // Send the token to your backend for verification
            return fetch('/auth/verify', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    idToken: idToken
                })
            });
        })
        .then((response) => {
            console.log('Backend response status:', response.status);
            if (response.ok) {
                console.log('Login successful, redirecting to home...');
                // Redirect to home page
                window.location.href = '/';
            } else {
                throw new Error('Authentication failed');
            }
        })
        .catch((error) => {
            console.log('Login error:', error);
            console.log('Error code:', error.code);
            console.log('Error message:', error.message);
            
            let errorMessage = 'An error occurred during sign in';
            
            switch (error.code) {
                case 'auth/user-not-found':
                    errorMessage = 'No account found with this email address';
                    break;
                case 'auth/wrong-password':
                    errorMessage = 'Incorrect password';
                    break;
                case 'auth/invalid-email':
                    errorMessage = 'Please enter a valid email address';
                    break;
                case 'auth/user-disabled':
                    errorMessage = 'This account has been disabled';
                    break;
                case 'auth/too-many-requests':
                    errorMessage = 'Too many failed attempts. Please try again later';
                    break;
                case 'auth/network-request-failed':
                    errorMessage = 'Network error. Please check your internet connection';
                    break;
                default:
                    errorMessage = `Login failed: ${error.message}`;
            }
            
            showLoginError(errorMessage);
        })
        .finally(() => {
            // Re-enable button
            loginBtn.disabled = false;
            loginBtn.innerHTML = originalText;
        });
}

function showLoginError(message) {
    const errorDiv = document.getElementById('errorMessage');
    const errorText = document.getElementById('errorText');
    errorText.textContent = message;
    errorDiv.classList.remove('d-none');
    errorDiv.scrollIntoView({ behavior: 'smooth' });
}

function hideLoginMessages() {
    document.getElementById('errorMessage').classList.add('d-none');
}
