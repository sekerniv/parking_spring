# Firebase Authentication Setup

This application now uses Firebase Authentication for user registration and login. Follow these steps to set up Firebase:

## 1. Create a Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or select an existing project
3. Follow the setup wizard

## 2. Enable Authentication

1. In your Firebase project, go to "Authentication" in the left sidebar
2. Click "Get started"
3. Go to the "Sign-in method" tab
4. Enable "Email/Password" authentication
5. Click "Save"

## 3. Get Firebase Configuration

1. In your Firebase project, click the gear icon (⚙️) next to "Project Overview"
2. Select "Project settings"
3. Scroll down to "Your apps" section
4. Click the web icon (</>)
5. Register your app with a nickname
6. Copy the Firebase configuration object

## 4. Set Environment Variables

For your project (`fir-webdev-project`), you only need to set the Firebase API key:

```bash
export FIREBASE_API_KEY="your-api-key"
```

### How to get your Firebase API Key:

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: `fir-webdev-project`
3. Click the gear icon (⚙️) next to "Project Overview"
4. Select "Project settings"
5. Scroll down to "Your apps" section
6. If you don't have a web app, click the web icon (</>) and register your app
7. Copy the `apiKey` value from the configuration object

The other Firebase configuration values are automatically set based on your project ID.

## 5. Update Firebase Security Rules (Optional)

If you want to restrict access to your Firestore database, update the security rules in the Firebase Console:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 6. Test the Application

1. Start the application
2. Go to `/register` to create a new account
3. Go to `/login` to sign in
4. Test the settings page and other authenticated features

## Features

- ✅ User registration with email/password
- ✅ Secure login with Firebase tokens
- ✅ Session management
- ✅ User settings persistence
- ✅ Proper error handling
- ✅ No fake user creation on failed login

## Security Notes

- Firebase handles password hashing and security
- ID tokens are verified on the backend
- User sessions are managed securely
