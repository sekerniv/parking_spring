// Parking Finder JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Handle resident checkbox for guest users
    const residentCheckbox = document.getElementById('residentCheckbox');
    if (residentCheckbox) {
        // Store the resident preference in localStorage for guest users
        residentCheckbox.addEventListener('change', function() {
            if (this.checked) {
                localStorage.setItem('guestResidentStatus', 'true');
            } else {
                localStorage.setItem('guestResidentStatus', 'false');
            }
        });
        
        // Load saved preference for guest users
        const savedResidentStatus = localStorage.getItem('guestResidentStatus');
        if (savedResidentStatus === 'true') {
            residentCheckbox.checked = true;
        }
        
        // Also check URL parameters for resident status
        const urlParams = new URLSearchParams(window.location.search);
        const residentParam = urlParams.get('resident');
        if (residentParam === 'true') {
            residentCheckbox.checked = true;
            localStorage.setItem('guestResidentStatus', 'true');
        }
    }
    
    // Handle search buttons
    const searchNearMeBtn = document.getElementById('searchNearMeBtn');
    const searchNearHomeBtn = document.getElementById('searchNearHomeBtn');
    
    if (searchNearMeBtn) {
        searchNearMeBtn.addEventListener('click', function() {
            // Get current location and search
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    function(position) {
                        const lat = position.coords.latitude;
                        const lng = position.coords.longitude;
                        performSearch(lat, lng, 'current');
                    },
                    function(error) {
                        alert('Unable to get your location. Please enable location services or use a different search method.');
                    }
                );
            } else {
                alert('Geolocation is not supported by this browser.');
            }
        });
    }
    
    if (searchNearHomeBtn) {
        searchNearHomeBtn.addEventListener('click', function() {
            // This would use the user's saved home location
            // For now, we'll just reload the page to get fresh data
            window.location.reload();
        });
    }
});

function performSearch(lat, lng, searchType) {
    // Show loading state
    const loadingDiv = document.getElementById('resultsLoading');
    const emptyDiv = document.getElementById('resultsEmpty');
    const resultsList = document.getElementById('parkingLots');
    
    if (loadingDiv) loadingDiv.classList.remove('d-none');
    if (emptyDiv) emptyDiv.classList.add('d-none');
    if (resultsList) resultsList.classList.add('d-none');
    
    // Get resident status from checkbox
    const residentCheckbox = document.getElementById('residentCheckbox');
    const isResident = residentCheckbox ? residentCheckbox.checked : false;
    
    // In a real implementation, you would make an AJAX call here
    // For now, we'll just reload the page with the new coordinates
    const url = new URL(window.location);
    url.searchParams.set('lat', lat);
    url.searchParams.set('lng', lng);
    url.searchParams.set('searchType', searchType);
    if (isResident) {
        url.searchParams.set('resident', 'true');
    }
    window.location.href = url.toString();
}
