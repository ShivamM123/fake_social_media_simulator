#!/bin/bash

# Fake Social Media Simulator - Run Script

echo "==================================="
echo "Fake Social Media Simulator"
echo "==================================="
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed!"
    echo "Please install Maven first:"
    echo "  brew install maven"
    exit 1
fi

echo "✅ Maven found"
echo ""

# Check if MySQL is accessible
echo "Checking MySQL connection..."
if mysql -u root -p12345678 -e "SELECT 1;" &> /dev/null; then
    echo "✅ MySQL connection successful"
else
    echo "❌ Cannot connect to MySQL"
    echo "Please make sure MySQL is running and credentials are correct"
    exit 1
fi
echo ""

# Check if database exists
echo "Checking database..."
if mysql -u root -p12345678 -e "USE fake_social_db;" &> /dev/null; then
    echo "✅ Database 'fake_social_db' exists"
else
    echo "⚠️  Database 'fake_social_db' does not exist"
    echo "Creating database from schema.sql..."
    mysql -u root -p12345678 < src/main/resources/schema.sql
    if [ $? -eq 0 ]; then
        echo "✅ Database created successfully"
    else
        echo "❌ Failed to create database"
        exit 1
    fi
fi
echo ""

# Build the project
echo "Building project..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi
echo "✅ Build successful"
echo ""

# Run the application
echo "Starting application..."
echo ""
mvn exec:java

