#!/bin/bash

echo "Firebase Environment Setup"
echo "========================="
echo ""
echo "This script will help you create a .env file with Firebase configuration."
echo ""

# Check if .env file exists
if [ -f ".env" ]; then
    echo "✅ .env file already exists"
    echo "Current .env contents:"
    cat .env
    echo ""
else
    echo "❌ .env file not found"
    echo "Creating .env file..."
    
    cat > .env << 'EOF'
# Firebase Web App Configuration (for client-side authentication)
# Replace these values with your actual Firebase web app configuration
FIREBASE_API_KEY=your-api-key-here
FIREBASE_AUTH_DOMAIN=fir-webdev-project.firebaseapp.com
FIREBASE_PROJECT_ID=fir-webdev-project
FIREBASE_STORAGE_BUCKET=fir-webdev-project.appspot.com
FIREBASE_MESSAGING_SENDER_ID=your-messaging-sender-id-here
FIREBASE_APP_ID=your-app-id-here

# Server-side Firebase Admin SDK (already configured via serviceAccountKey.json)
GOOGLE_APPLICATION_CREDENTIALS=src/main/resources/serviceAccountKey.json
EOF

    echo "✅ .env file created"
    echo ""
fi

echo "To get your Firebase web app configuration:"
echo "1. Go to https://console.firebase.google.com/"
echo "2. Select your project: fir-webdev-project"
echo "3. Click the gear icon (⚙️) next to 'Project Overview'"
echo "4. Select 'Project settings'"
echo "5. Scroll down to 'Your apps' section"
echo "6. If you don't have a web app, click the web icon (</>) and register your app"
echo "7. Copy the configuration values and update your .env file"
echo ""
echo "The configuration should look like this:"
echo "const firebaseConfig = {"
echo "  apiKey: 'AIzaSyBXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX',"
echo "  authDomain: 'fir-webdev-project.firebaseapp.com',"
echo "  projectId: 'fir-webdev-project',"
echo "  storageBucket: 'fir-webdev-project.appspot.com',"
echo "  messagingSenderId: '123456789012',"
echo "  appId: '1:123456789012:web:abcdefghijklmnop'"
echo "};"
echo ""
echo "Then update your .env file with these values:"
echo "FIREBASE_API_KEY=AIzaSyBXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
echo "FIREBASE_MESSAGING_SENDER_ID=123456789012"
echo "FIREBASE_APP_ID=1:123456789012:web:abcdefghijklmnop"
