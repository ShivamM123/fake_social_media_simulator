# Quick Setup Guide

## Step 1: Reset Database (IMPORTANT!)

If you're getting errors about missing columns or tables, you need to reset the database:

```bash
mysql -u root -p12345678 < src/main/resources/reset_database.sql
```

This will:
- Drop the old database
- Create a fresh database with all required tables and columns
- Set up the correct schema

## Step 2: Build the Project

```bash
mvn clean compile
```

## Step 3: Run the Application

```bash
mvn exec:java
```

## Step 4: Login

On first run, a default admin account is automatically created:
- **Username**: `admin`
- **Password**: `admin123`

You can also register a new account from the login screen.

## Troubleshooting

### "Unknown column 'is_ai_generated'" Error
**Solution**: Reset the database using the command in Step 1.

### "Table 'comments' doesn't exist" Error
**Solution**: Reset the database using the command in Step 1.

### "Cannot connect to database" Error
**Solution**: 
1. Make sure MySQL is running
2. Check database credentials in `DatabaseConnection.java`
3. Verify the database exists: `mysql -u root -p12345678 -e "SHOW DATABASES;"`

### Login Not Working
**Solution**:
1. Make sure the database was reset
2. Try logging in with the default admin account: `admin` / `admin123`
3. Register a new account if needed

### Application Won't Start
**Solution**:
1. Make sure Java 11+ is installed: `java -version`
2. Make sure Maven is installed: `mvn -version`
3. Clean and rebuild: `mvn clean compile`

## What Gets Created Automatically

When you first run the application:
1. Default admin user (username: `admin`, password: `admin123`)
2. 10 fake users for AI content generation
3. AI simulator starts generating posts and comments

## Next Steps

1. Login with admin account
2. Explore the admin panel (admin users only)
3. Create posts and comments
4. Watch AI-generated content appear automatically

