# Quick Start Guide - Blood Donor Application

## Setup in 5 Minutes

### Step 1: Install Prerequisites
- Install Java 17
- Install MySQL 8.0
- Install Maven (or use IntelliJ's built-in Maven)

### Step 2: Database Setup
```bash
mysql -u root -p
CREATE DATABASE blood_donor_db;
USE blood_donor_db;
source schema.sql;
```

### Step 3: Configure Email (Gmail)

1. Go to your Google Account → Security
2. Enable 2-Step Verification
3. Search for "App Passwords"
4. Generate a new app password for "Mail"
5. Copy the 16-character password

Edit `application.properties`:
```properties
spring.mail.username=youremail@gmail.com
spring.mail.password=your-16-char-app-password
```

### Step 4: Configure Database Password

Edit `application.properties`:
```properties
spring.datasource.password=your_mysql_password
```

### Step 5: Run the Application

**Option A: Using IntelliJ IDEA**
1. Open IntelliJ IDEA
2. File → Open → Select the `blood-donor-app` folder
3. Wait for Maven to download dependencies
4. Right-click on `BloodDonorApplication.java`
5. Click "Run"

**Option B: Using Command Line**
```bash
cd blood-donor-app
mvn clean install
mvn spring-boot:run
```

### Step 6: Test the API

Open browser or Postman and test:
```
http://localhost:8080/api/donors/search
```

Sample POST request body:
```json
{
  "city": "Hyderabad"
}
```

## Quick Test Flow

### 1. Register a Donor
```bash
POST http://localhost:8080/api/donors/register/initiate

{
  "name": "Test User",
  "email": "test@example.com",
  "phone": "9876543210",
  "bloodGroup": "O+",
  "area": "Test Area",
  "city": "Hyderabad"
}
```

Check your email for OTP!

### 2. Complete Registration
```bash
POST http://localhost:8080/api/donors/register/complete?otp=YOUR_OTP

{
  "name": "Test User",
  "email": "test@example.com",
  "phone": "9876543210",
  "bloodGroup": "O+",
  "area": "Test Area",
  "city": "Hyderabad"
}
```

### 3. Search for Donors
```bash
POST http://localhost:8080/api/donors/search

{
  "bloodGroup": "O+",
  "city": "Hyderabad"
}
```

## Common Issues

### Issue: "Access denied for user"
**Solution**: Check MySQL username/password in `application.properties`

### Issue: "Mail server connection failed"
**Solution**: 
- Make sure you're using App Password, not your regular Gmail password
- Enable "Less secure app access" is NO LONGER NEEDED (deprecated)

### Issue: "Port 8080 already in use"
**Solution**: Change port in `application.properties`:
```properties
server.port=8081
```

### Issue: Dependencies not downloading
**Solution**: 
```bash
mvn clean install -U
```

## Default Admin Login
- Username: admin
- Password: admin123
- **Change this immediately in production!**

## API Testing Tools

**Recommended**:
- Postman (easiest for beginners)
- Thunder Client (VS Code extension)
- cURL (command line)

**Example cURL**:
```bash
curl -X POST http://localhost:8080/api/donors/search \
  -H "Content-Type: application/json" \
  -d '{"city":"Hyderabad"}'
```

## Next: Build the Frontend

Once backend is running, create React app:
```bash
npx create-react-app blood-donor-frontend
cd blood-donor-frontend
npm install axios react-router-dom
npm start
```

## Need Help?

Common debugging steps:
1. Check console logs for errors
2. Verify MySQL is running: `mysql -u root -p`
3. Check if port 8080 is free
4. Make sure Java 17 is installed: `java -version`
5. Clear Maven cache: `mvn clean`

Enjoy building! 🩸
