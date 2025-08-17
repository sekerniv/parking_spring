// Home page JavaScript
console.log('Home page detected, setting up handlers...');

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM fully loaded on home page');
    
    const searchBtn = document.getElementById('searchBtn');
    const searchLocationToggle = document.getElementById('searchLocationToggle');
    const residentCheckbox = document.getElementById('residentCheckbox');
    const searchHint = document.getElementById('searchHint');
    
    console.log('Search button:', searchBtn);
    console.log('Search location toggle:', searchLocationToggle);
    console.log('Resident checkbox:', residentCheckbox);
    
    // Handle search location toggle
    if (searchLocationToggle) {
        searchLocationToggle.addEventListener('change', function() {
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
        if (!searchHint || !searchLocationToggle) return;
        
        if (searchLocationToggle.checked) {
            searchHint.innerHTML = '<i class="bi bi-house-door-fill me-1"></i> Uses your home location';
        } else {
            searchHint.innerHTML = '<i class="bi bi-geo-alt-fill me-1"></i> Uses your current location';
        }
    }
    
    function handleSearch() {
        const isResident = residentCheckbox ? residentCheckbox.checked : false;
        const useHomeLocation = searchLocationToggle ? searchLocationToggle.checked : false;
        const radius = document.getElementById('radiusSelect') ? document.getElementById('radiusSelect').value : '1000';
        
        console.log('Search parameters:', { isResident, useHomeLocation, radius });
        
        // Build search URL
        let searchUrl = '/?resident=' + (isResident ? 'true' : 'false') + '&radius=' + radius;
        
        if (useHomeLocation) {
            // For home location, we'll need to get the coordinates from the server
            // For now, just redirect to the home page with resident parameter
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
