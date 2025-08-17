# JavaScript Files Organization

This directory contains the JavaScript files for the Parking Finder application, organized by functionality.

## File Structure

### Core Files
- **`firebase-config.js`** - Firebase configuration and initialization
- **`script.js`** - General application functionality (resident checkbox, search buttons)

### Page-Specific Files
- **`registration.js`** - Registration page functionality (form handling, Firebase auth)
- **`login.js`** - Login page functionality (form handling, Firebase auth)
- **`home.js`** - Home page functionality (search controls, location handling)

## Loading Strategy

Each page template explicitly includes its own page-specific scripts using Thymeleaf's layout fragment system:

- **Registration page** (`register.html`) includes `registration.js`
- **Login page** (`login.html`) includes `login.js`
- **Home page** (`home.html`) includes `home.js`
- **All pages** inherit core files from layout (`script.js`, `firebase-config.js`)

This approach is cleaner and more explicit - each page is responsible for including its own scripts.

## Firebase Integration

Firebase configuration is passed from the server to the client via Thymeleaf and stored in `window.firebaseConfig`. The `firebase-config.js` file handles initialization when the config is available.

## Best Practices

- Each page-specific file is self-contained and handles its own DOM events
- Common functionality is kept in `script.js`
- Firebase initialization is centralized in `firebase-config.js`
- All files use proper error handling and logging
