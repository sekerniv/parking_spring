#!/bin/bash

echo "Firebase Setup Script"
echo "====================="
echo ""
echo "This script will help you set up Firebase environment variables."
echo ""

# Check if FIREBASE_API_KEY is already set
if [ -n "$FIREBASE_API_KEY" ]; then
    echo "✅ FIREBASE_API_KEY is already set: ${FIREBASE_API_KEY:0:10}..."
else
    echo "❌ FIREBASE_API_KEY is not set"
    echo ""
    echo "To get your Firebase API Key:"
    echo "1. Go to https://console.firebase.google.com/"
    echo "2. Select your project: fir-webdev-project"
    echo "3. Click the gear icon (⚙️) next to 'Project Overview'"
    echo "4. Select 'Project settings'"
    echo "5. Scroll down to 'Your apps' section"
    echo "6. If you don't have a web app, click the web icon (</>) and register your app"
    echo "7. Copy the 'apiKey' value from the configuration object"
    echo ""
    echo "Then run: export FIREBASE_API_KEY='your-api-key-here'"
    echo ""
fi

echo ""
echo "Current environment variables:"
echo "FIREBASE_API_KEY: ${FIREBASE_API_KEY:-'NOT SET'}"
echo "FIREBASE_MESSAGING_SENDER_ID: ${FIREBASE_MESSAGING_SENDER_ID:-'NOT SET'}"
echo "FIREBASE_APP_ID: ${FIREBASE_APP_ID:-'NOT SET'}"
echo ""

if [ -n "$FIREBASE_API_KEY" ]; then
    echo "✅ You're ready to run the application!"
    echo "Run: ./mvnw spring-boot:run"
else
    echo "❌ Please set the FIREBASE_API_KEY environment variable first"
fi
