# Blood Donor Application

A comprehensive blood donor management system built with Spring Boot and React.

## Features

### Public Features
- **Donor Registration**: Register as a blood donor with email OTP verification
- **Search Donors**: Search for donors by blood group, city, and availability
- **Report Donors**: Report unavailable donors with automatic status updates

### Admin Features
- **Donor Management**: Add, update, delete donors
- **Status Management**: Update donor availability status
- **Report Management**: View and manage donor reports
- **Dashboard**: View statistics and pending reports

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.1
- Spring Data JPA
- Spring Security
- MySQL Database
- Spring Mail (for OTP emails)
- Lombok

### Frontend (To be built)
- React
- Axios for API calls
- React Router
- Tailwind CSS (suggested)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Gmail account (for sending OTP emails)

## Setup Instructions

### 1. Database Setup

```bash
# Login to MySQL
mysql -u root -p

# Run the schema file
source /path/to/schema.sql
```

### 2. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

# Email Configuration (Gmail)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

**Important**: For Gmail, you need to:
1. Enable 2-Factor Authentication
2. Generate an "App Password" from Google Account settings
3. Use that App Password in the configuration

### 3. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Public Endpoints

#### Donor Registration
```http
POST /api/donors/register/initiate
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "bloodGroup": "O+",
  "area": "Banjara Hills",
  "city": "Hyderabad"
}
```

```http
POST /api/donors/register/complete?otp=123456
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "bloodGroup": "O+",
  "area": "Banjara Hills",
  "city": "Hyderabad"
}
```

#### Search Donors
```http
POST /api/donors/search
Content-Type: application/json

{
  "bloodGroup": "O+",
  "city": "Hyderabad",
  "availabilityStatus": "AVAILABLE"
}
```

#### Get Donor by ID
```http
GET /api/donors/{id}
```

#### Report Donor
```http
POST /api/reports
Content-Type: application/json

{
  "donorId": 1,
  "reporterName": "Jane Doe",
  "reporterEmail": "jane@example.com",
  "reporterPhone": "9876543211",
  "reason": "ALREADY_DONATED",
  "reasonDetails": "Donated last week"
}
```

### Admin Endpoints

#### Get All Donors
```http
GET /api/admin/donors
```

#### Add Donor (Admin)
```http
POST /api/admin/donors
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "bloodGroup": "O+",
  "area": "Banjara Hills",
  "city": "Hyderabad"
}
```

#### Update Donor
```http
PUT /api/admin/donors/{id}
Content-Type: application/json

{
  "name": "John Doe Updated",
  "email": "john@example.com",
  "phone": "9876543210",
  "bloodGroup": "O+",
  "area": "Jubilee Hills",
  "city": "Hyderabad"
}
```

#### Delete Donor
```http
DELETE /api/admin/donors/{id}
```

#### Update Donor Status
```http
PUT /api/admin/donors/{id}/status?status=NOT_AVAILABLE&monthsUnavailable=3
```

#### Get All Reports
```http
GET /api/admin/reports
```

#### Get Pending Reports
```http
GET /api/admin/reports/pending
```

#### Update Report Status
```http
PUT /api/admin/reports/{id}/status?status=CONFIRMED
```

## Data Models

### Blood Groups Supported
- A+, A-, B+, B-, AB+, AB-, O+, O-

### Availability Status
- AVAILABLE
- NOT_AVAILABLE

### Report Reasons
- ALREADY_DONATED
- WRONG_NUMBER
- REFUSED_TO_DONATE
- OTHER

### Report Status
- PENDING
- CONFIRMED
- REJECTED
- AUTO_CONFIRMED

## Scheduled Tasks

The application runs two scheduled tasks:

1. **Process Expired Reports**: Runs every hour
   - Checks for reports pending for more than 24 hours
   - Auto-confirms and updates donor status to NOT_AVAILABLE

2. **Cleanup Expired OTPs**: Runs every 30 minutes
   - Removes expired OTP entries from database

## Security Notes

- The default admin credentials are:
  - Username: `admin`
  - Password: `admin123`
  - **IMPORTANT**: Change this in production!

- Always use HTTPS in production
- Keep your JWT secret key secure
- Use environment variables for sensitive data

## Development

### Running Tests
```bash
mvn test
```

### Debugging
Set logging level to DEBUG in `application.properties`:
```properties
logging.level.com.blooddonor=DEBUG
```

## Next Steps

1. Build the React frontend
2. Add authentication/authorization with JWT
3. Add pagination for large datasets
4. Add email templates for better-looking emails
5. Add SMS notifications (optional)
6. Deploy to production

## Contributing

This is a learning project. Feel free to extend and improve it!

## License

Free to use for educational purposes.
# blood-donor
