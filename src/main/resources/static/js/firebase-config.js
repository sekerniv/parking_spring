// Firebase configuration and initialization
document.addEventListener('DOMContentLoaded', function() {
    // Initialize Firebase if config is available
    if (window.firebaseConfig && window.firebaseConfig.apiKey && window.firebaseConfig.apiKey !== '') {
        if (typeof firebase !== 'undefined') {
            firebase.initializeApp(window.firebaseConfig);
            console.log('Firebase initialized successfully');
        } else {
            console.error('Firebase SDK not loaded');
        }
    } else {
        console.warn('Firebase config not available or invalid');
    }
});
