// Home page JavaScript
console.log('Home page detected, setting up handlers...');

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM fully loaded on home page');
    
    const searchBtn = document.getElementById('searchBtn');
    const currentLocationRadio = document.getElementById('currentLocation');
    const homeAddressRadio = document.getElementById('homeAddress');
    const residentCheckbox = document.getElementById('residentCheckbox');
    const searchHint = document.getElementById('searchHint');
    
    console.log('Search button:', searchBtn);
    console.log('Current location radio:', currentLocationRadio);
    console.log('Home address radio:', homeAddressRadio);
    console.log('Resident checkbox:', residentCheckbox);
    
    // Handle location toggle changes
    if (currentLocationRadio) {
        currentLocationRadio.addEventListener('change', function() {
            updateSearchHint();
        });
    }
    
    if (homeAddressRadio) {
        homeAddressRadio.addEventListener('change', function() {
            updateSearchHint();
        });
    }
    
    // Handle search button click
    if (searchBtn) {
        searchBtn.addEventListener('click', function() {
            handleSearch();
        });
    }
    
    // Handle resident checkbox change
    if (residentCheckbox) {
        residentCheckbox.addEventListener('change', function() {
            // Store the user's choice locally
            localStorage.setItem('userResidentChoice', this.checked);
        });
    }
    
    // Initialize resident checkbox from localStorage if available
    if (residentCheckbox) {
        const savedChoice = localStorage.getItem('userResidentChoice');
        if (savedChoice !== null) {
            residentCheckbox.checked = savedChoice === 'true';
        }
    }
    
    function updateSearchHint() {
        if (!searchHint) return;
        
        // Check which radio button is selected
        const useHomeLocation = homeAddressRadio && homeAddressRadio.checked;
        
        if (useHomeLocation) {
            searchHint.innerHTML = '<i class="bi bi-house-door-fill me-1"></i> Uses your home address';
        } else {
            searchHint.innerHTML = '<i class="bi bi-geo-alt-fill me-1"></i> Uses your current location (browser permission required)';
        }
    }
    
    function handleSearch() {
        const isResident = residentCheckbox ? residentCheckbox.checked : false;
        const useHomeLocation = homeAddressRadio ? homeAddressRadio.checked : false;
        
        console.log('Search parameters:', { isResident, useHomeLocation });
        
        // Build search URL with location parameter
        let searchUrl = '/?resident=' + (isResident ? 'true' : 'false') + '&location=' + (useHomeLocation ? 'home' : 'current');
        
        if (useHomeLocation) {
            // For home location, just redirect with the location parameter
            window.location.href = searchUrl;
        } else {
            // For current location, get user's location first
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                    const lat = position.coords.latitude;
                    const lng = position.coords.longitude;
                    searchUrl += '&lat=' + lat + '&lng=' + lng;
                    window.location.href = searchUrl;
                }, function(error) {
                    console.error('Geolocation error:', error);
                    // Fallback to default location
                    window.location.href = searchUrl;
                });
            } else {
                // Fallback to default location
                window.location.href = searchUrl;
            }
        }
    }
    
    // Initialize search hint
    updateSearchHint();
});
