#!/bin/bash

echo "==================================="
echo "Fake Social Media - Setup Script"
echo "==================================="
echo ""

# Check MySQL connection
echo "Checking MySQL connection..."
if mysql -u root -p12345678 -e "SELECT 1;" &> /dev/null; then
    echo "✅ MySQL connection successful"
else
    echo "❌ Cannot connect to MySQL"
    echo "Please make sure MySQL is running and credentials are correct"
    exit 1
fi
echo ""

# Reset database
echo "Resetting database..."
mysql -u root -p12345678 < src/main/resources/reset_database.sql
if [ $? -eq 0 ]; then
    echo "✅ Database reset successfully"
else
    echo "❌ Database reset failed"
    exit 1
fi
echo ""

# Build project
echo "Building project..."
mvn clean compile
if [ $? -eq 0 ]; then
    echo "✅ Build successful"
else
    echo "❌ Build failed"
    exit 1
fi
echo ""

echo "==================================="
echo "Setup Complete!"
echo "==================================="
echo ""
echo "Default admin account:"
echo "  Username: admin"
echo "  Password: admin123"
echo ""
echo "To run the application:"
echo "  mvn exec:java"
echo ""

