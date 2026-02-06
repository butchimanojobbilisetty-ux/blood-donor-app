# Blood Donor App - API Documentation

Base URL: `http://localhost:8080`

## Table of Contents
1. [Public Endpoints](#public-endpoints)
2. [Admin Endpoints](#admin-endpoints)
3. [Response Format](#response-format)
4. [Error Codes](#error-codes)

---

## Public Endpoints

### 1. Donor Registration

#### Initiate Registration
Sends OTP to donor's email for verification.

**Endpoint**: `POST /api/donors/register/initiate`

**Request Body**:
```json
{
  "name": "Rajesh Kumar",
  "email": "rajesh@example.com",
  "phone": "9876543210",
  "bloodGroup": "O+",
  "area": "Banjara Hills",
  "city": "Hyderabad"
}
```

**Validation Rules**:
- name: Required, not blank
- email: Required, valid email format
- phone: Required, 10-15 digits
- bloodGroup: Required, valid blood group (A+, A-, B+, B-, AB+, AB-, O+, O-)
- area: Required
- city: Required

**Success Response** (200):
```json
{
  "success": true,
  "message": "OTP sent to your email. Please verify to complete registration.",
  "data": null
}
```

**Error Response** (400):
```json
{
  "success": false,
  "message": "Email already registered",
  "data": null
}
```

---

#### Complete Registration
Completes registration after OTP verification.

**Endpoint**: `POST /api/donors/register/complete?otp=123456`

**Query Parameters**:
- otp: 6-digit OTP code (required)

**Request Body**: Same as initiate registration

**Success Response** (200):
```json
{
  "success": true,
  "message": "Registration completed successfully!",
  "data": {
    "id": 1,
    "name": "Rajesh Kumar",
    "email": "rajesh@example.com",
    "phone": "9876543210",
    "bloodGroup": "O+",
    "area": "Banjara Hills",
    "city": "Hyderabad",
    "availabilityStatus": "AVAILABLE",
    "notAvailableUntil": null,
    "createdAt": "2025-01-30T10:30:00",
    "updatedAt": "2025-01-30T10:30:00",
    "isVerified": true
  }
}
```

---

### 2. Search Donors

**Endpoint**: `POST /api/donors/search`

**Request Body** (all fields optional):
```json
{
  "bloodGroup": "O+",
  "city": "Hyderabad",
  "availabilityStatus": "AVAILABLE"
}
```

**Success Response** (200):
```json
{
  "success": true,
  "message": "Donors retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Rajesh Kumar",
      "email": "rajesh@example.com",
      "phone": "9876543210",
      "bloodGroup": "O+",
      "area": "Banjara Hills",
      "city": "Hyderabad",
      "availabilityStatus": "AVAILABLE",
      "notAvailableUntil": null
    }
  ]
}
```

---

### 3. Get Donor by ID

**Endpoint**: `GET /api/donors/{id}`

**Path Parameters**:
- id: Donor ID (Long)

**Success Response** (200):
```json
{
  "success": true,
  "message": "Donor retrieved successfully",
  "data": {
    "id": 1,
    "name": "Rajesh Kumar",
    "email": "rajesh@example.com",
    "phone": "9876543210",
    "bloodGroup": "O+",
    "area": "Banjara Hills",
    "city": "Hyderabad",
    "availabilityStatus": "AVAILABLE"
  }
}
```

---

### 4. Report Donor

**Endpoint**: `POST /api/reports`

**Request Body**:
```json
{
  "donorId": 1,
  "reporterName": "Jane Doe",
  "reporterEmail": "jane@example.com",
  "reporterPhone": "9876543211",
  "reason": "ALREADY_DONATED",
  "reasonDetails": "Donated last week at City Hospital"
}
```

**Validation**:
- donorId: Required
- At least one of reporterEmail or reporterPhone must be provided
- reason: Required, one of: ALREADY_DONATED, WRONG_NUMBER, REFUSED_TO_DONATE, OTHER

**Success Response** (200):
```json
{
  "success": true,
  "message": "Report submitted successfully. Donor has been notified and will be updated if no response within 24 hours.",
  "data": {
    "id": 1,
    "donor": {...},
    "reporterName": "Jane Doe",
    "reporterEmail": "jane@example.com",
    "reporterPhone": "9876543211",
    "reason": "ALREADY_DONATED",
    "reasonDetails": "Donated last week at City Hospital",
    "reportStatus": "PENDING",
    "reportedAt": "2025-01-30T10:30:00"
  }
}
```

---

## Admin Endpoints

### 1. Get All Donors

**Endpoint**: `GET /api/admin/donors`

**Success Response** (200):
```json
{
  "success": true,
  "message": "Donors retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Rajesh Kumar",
      "email": "rajesh@example.com",
      ...
    }
  ]
}
```

---

### 2. Add Donor (Admin)

**Endpoint**: `POST /api/admin/donors`

**Request Body**: Same as donor registration

**Note**: Admin can add donors directly without OTP verification.

---

### 3. Update Donor

**Endpoint**: `PUT /api/admin/donors/{id}`

**Path Parameters**:
- id: Donor ID

**Request Body**: Same as donor registration

---

### 4. Delete Donor

**Endpoint**: `DELETE /api/admin/donors/{id}`

**Path Parameters**:
- id: Donor ID

**Success Response** (200):
```json
{
  "success": true,
  "message": "Donor deleted successfully",
  "data": null
}
```

---

### 5. Update Donor Status

**Endpoint**: `PUT /api/admin/donors/{id}/status`

**Path Parameters**:
- id: Donor ID

**Query Parameters**:
- status: AVAILABLE or NOT_AVAILABLE (required)
- monthsUnavailable: Number of months (optional, only for NOT_AVAILABLE)

**Example**: `PUT /api/admin/donors/1/status?status=NOT_AVAILABLE&monthsUnavailable=3`

---

### 6. Get All Reports

**Endpoint**: `GET /api/admin/reports`

**Success Response** (200):
```json
{
  "success": true,
  "message": "Reports retrieved successfully",
  "data": [...]
}
```

---

### 7. Get Pending Reports

**Endpoint**: `GET /api/admin/reports/pending`

Returns only reports with status "PENDING".

---

### 8. Update Report Status

**Endpoint**: `PUT /api/admin/reports/{id}/status`

**Path Parameters**:
- id: Report ID

**Query Parameters**:
- status: PENDING, CONFIRMED, REJECTED, AUTO_CONFIRMED (required)

**Example**: `PUT /api/admin/reports/1/status?status=CONFIRMED`

**Note**: When status is set to CONFIRMED, donor's availability is automatically updated to NOT_AVAILABLE for 3 months.

---

## Response Format

All API responses follow this structure:

```json
{
  "success": boolean,
  "message": "string",
  "data": object | array | null
}
```

---

## Error Codes

| HTTP Code | Meaning |
|-----------|---------|
| 200 | Success |
| 400 | Bad Request (validation error, business logic error) |
| 404 | Resource not found |
| 500 | Internal server error |

---

## Common Error Messages

- "Email already registered"
- "Invalid or expired OTP"
- "Donor not found"
- "At least one contact method (email or phone) is required"
- "Invalid blood group"

---

## Scheduled Tasks

The application automatically runs these tasks:

1. **Process Expired Reports** (Every 1 hour)
   - Checks reports pending for >24 hours
   - Auto-confirms and updates donor status

2. **Cleanup Expired OTPs** (Every 30 minutes)
   - Removes expired OTP entries

---

## Testing with cURL

### Search Donors
```bash
curl -X POST http://localhost:8080/api/donors/search \
  -H "Content-Type: application/json" \
  -d '{"bloodGroup":"O+","city":"Hyderabad"}'
```

### Register Donor
```bash
curl -X POST http://localhost:8080/api/donors/register/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "phone": "9876543210",
    "bloodGroup": "O+",
    "area": "Test Area",
    "city": "Hyderabad"
  }'
```

### Report Donor
```bash
curl -X POST http://localhost:8080/api/reports \
  -H "Content-Type: application/json" \
  -d '{
    "donorId": 1,
    "reporterEmail": "reporter@example.com",
    "reason": "ALREADY_DONATED",
    "reasonDetails": "Donated yesterday"
  }'
```

---

## Notes

- All timestamps are in ISO 8601 format
- Blood groups must be one of: A+, A-, B+, B-, AB+, AB-, O+, O-
- Phone numbers should be 10-15 digits
- OTPs are valid for 10 minutes
- Reports auto-confirm after 24 hours if donor doesn't respond
