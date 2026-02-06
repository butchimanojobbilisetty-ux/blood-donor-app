# Blood Donor App - Authentication API Documentation

## Overview
This document provides API endpoints and implementation guidelines for UI developers to integrate login and logout functionality in the Blood Donor Application.

## Base URL
```
http://localhost:8080/api
```

## Authentication Flow
1. User submits login credentials
2. Server validates credentials and returns JWT token
3. Client stores JWT token (localStorage/sessionStorage)
4. Include JWT token in Authorization header for protected requests
5. Logout by clearing stored token

---

## 1. Donor Login API

### Endpoint
```
POST /donors/login
```

### Request Body
```json
{
  "email": "user@example.com",
  "password": "userpassword123"
}
```

### Request Headers
```
Content-Type: application/json
```

### Success Response (200 OK)
```json
{
  "success": true,
  "message": "Login successful!",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "role": "DONOR"
  }
}
```

### Error Response (400 Bad Request)
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null
}
```

### Error Response (400 Bad Request) - Account Not Verified
```json
{
  "success": false,
  "message": "Account not verified",
  "data": null
}
```

---

## 2. Admin Login API

### Endpoint
```
POST /admin/login
```

### Request Body
```json
{
  "username": "admin",
  "password": "admin123"
}
```

### Request Headers
```
Content-Type: application/json
```

### Success Response (200 OK)
```json
{
  "success": true,
  "message": "Admin login successful!",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 0,
    "email": "admin",
    "name": "Administrator",
    "role": "ADMIN"
  }
}
```

### Error Response (400 Bad Request)
```json
{
  "success": false,
  "message": "Invalid admin credentials",
  "data": null
}
```

---

## 3. Logout Functionality

### Important Note
There is no explicit logout API endpoint. Logout is handled client-side by clearing the stored JWT token.

### Client-Side Logout Implementation
```javascript
// Clear stored token
localStorage.removeItem('authToken');
sessionStorage.removeItem('authToken');

// Clear user data
localStorage.removeItem('userData');
sessionStorage.removeItem('userData');

// Redirect to login page
window.location.href = '/login';
```

---

## 4. Using JWT Token for Protected Requests

### Authorization Header Format
```
Authorization: Bearer <your-jwt-token>
```

### Example Request with Authentication
```javascript
// Using fetch API
const response = await fetch('/api/admin/donors', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  }
});

// Using axios
const response = await axios.get('/api/admin/donors', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

---

## 5. Frontend Implementation Guide

### 5.1 Login Form Implementation

#### HTML Structure
```html
<form id="loginForm">
  <div class="form-group">
    <label for="email">Email:</label>
    <input type="email" id="email" name="email" required>
  </div>
  <div class="form-group">
    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required>
  </div>
  <button type="submit">Login</button>
  <div id="errorMessage" class="error-message"></div>
</form>
```

#### JavaScript Implementation
```javascript
// Login function
async function login(email, password, isAdmin = false) {
  const endpoint = isAdmin ? '/api/admin/login' : '/api/donors/login';
  
  try {
    const response = await fetch(endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: email,
        password: password
      })
    });

    const data = await response.json();

    if (data.success) {
      // Store token and user data
      localStorage.setItem('authToken', data.data.token);
      localStorage.setItem('userData', JSON.stringify(data.data));
      
      // Redirect based on role
      if (data.data.role === 'ADMIN') {
        window.location.href = '/admin/dashboard';
      } else {
        window.location.href = '/donor/dashboard';
      }
    } else {
      // Show error message
      document.getElementById('errorMessage').textContent = data.message;
    }
  } catch (error) {
    console.error('Login error:', error);
    document.getElementById('errorMessage').textContent = 'Login failed. Please try again.';
  }
}

// Form submit handler
document.getElementById('loginForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const email = document.getElementById('email').value;
  const password = document.getElementById('password').value;
  
  await login(email, password);
});
```

### 5.2 Authentication Service

#### Auth Service Class
```javascript
class AuthService {
  constructor() {
    this.token = localStorage.getItem('authToken') || sessionStorage.getItem('authToken');
    this.userData = JSON.parse(localStorage.getItem('userData') || sessionStorage.getItem('userData') || '{}');
  }

  // Check if user is authenticated
  isAuthenticated() {
    return !!this.token;
  }

  // Get current user role
  getUserRole() {
    return this.userData.role || null;
  }

  // Get user data
  getUserData() {
    return this.userData;
  }

  // Get authorization header
  getAuthHeader() {
    return this.token ? `Bearer ${this.token}` : null;
  }

  // Store authentication data
  storeAuthData(token, userData, rememberMe = false) {
    const storage = rememberMe ? localStorage : sessionStorage;
    storage.setItem('authToken', token);
    storage.setItem('userData', JSON.stringify(userData));
    this.token = token;
    this.userData = userData;
  }

  // Clear authentication data (logout)
  clearAuthData() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('userData');
    this.token = null;
    this.userData = {};
  }

  // Check if token is expired (basic check)
  isTokenExpired() {
    if (!this.token) return true;
    
    try {
      const payload = JSON.parse(atob(this.token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp < currentTime;
    } catch (error) {
      return true;
    }
  }
}

// Export for use in components
const authService = new AuthService();
```

### 5.3 API Interceptor for Authentication

#### Axios Interceptor
```javascript
import axios from 'axios';

// Create axios instance
const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// Request interceptor - add auth token
api.interceptors.request.use(
  (config) => {
    const token = authService.getAuthHeader();
    if (token) {
      config.headers.Authorization = token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - handle token expiration
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      authService.clearAuthData();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

---

## 6. Protected Routes Implementation

### Route Guards
```javascript
// Route guard function
function requireAuth(role = null) {
  const userData = authService.getUserData();
  
  if (!authService.isAuthenticated()) {
    window.location.href = '/login';
    return false;
  }
  
  if (role && userData.role !== role) {
    window.location.href = '/unauthorized';
    return false;
  }
  
  return true;
}

// Usage examples
// Donor dashboard
if (requireAuth('DONOR')) {
  // Load donor dashboard
}

// Admin dashboard
if (requireAuth('ADMIN')) {
  // Load admin dashboard
}
```

---

## 7. Error Handling

### Common Error Codes and Messages

| Status Code | Message | Action |
|-------------|---------|--------|
| 400 | "Invalid email or password" | Show invalid credentials error |
| 400 | "Account not verified" | Show verification required message |
| 401 | "Unauthorized" | Redirect to login |
| 403 | "Forbidden" | Show access denied message |
| 500 | "Internal server error" | Show generic error message |

### Error Handling Implementation
```javascript
function handleApiError(error) {
  if (error.response) {
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        showErrorMessage(data.message || 'Bad request');
        break;
      case 401:
        authService.clearAuthData();
        window.location.href = '/login';
        break;
      case 403:
        showErrorMessage('Access denied');
        break;
      case 500:
        showErrorMessage('Server error. Please try again later.');
        break;
      default:
        showErrorMessage('An error occurred. Please try again.');
    }
  } else {
    showErrorMessage('Network error. Please check your connection.');
  }
}
```

---

## 8. Testing the APIs

### Using curl Commands

#### Donor Login
```bash
curl -X POST http://localhost:8080/api/donors/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "userpassword123"
  }'
```

#### Admin Login
```bash
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

#### Protected API Call
```bash
curl -X GET http://localhost:8080/api/admin/donors \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## 9. Security Best Practices

### Frontend Security
1. **Token Storage**: Use localStorage for "Remember Me" feature, sessionStorage otherwise
2. **Token Expiration**: Check token expiration before making requests
3. **HTTPS**: Always use HTTPS in production
4. **Input Validation**: Validate form inputs before sending
5. **Error Messages**: Don't expose sensitive information in error messages

### Token Management
1. Store tokens securely
2. Clear tokens on logout
3. Handle token expiration gracefully
4. Don't store tokens in cookies (to prevent CSRF attacks)

---

## 10. Quick Implementation Checklist

### Login Page
- [ ] Create login form with email/password fields
- [ ] Implement form validation
- [ ] Add login API integration
- [ ] Handle success/error responses
- [ ] Store JWT token on successful login
- [ ] Redirect based on user role

### Logout Functionality
- [ ] Create logout button/component
- [ ] Clear stored tokens and user data
- [ ] Redirect to login page

### Authentication Guards
- [ ] Implement route protection
- [ ] Add role-based access control
- [ ] Handle token expiration
- [ ] Redirect unauthorized users

### API Integration
- [ ] Set up axios interceptors
- [ ] Add authorization headers
- [ ] Handle 401/403 responses
- [ ] Implement error handling

---

## 11. Support

For any issues or questions regarding the authentication APIs, please contact the backend development team.

**Note**: This documentation assumes the backend is running on `localhost:8080`. Update the base URL for production environments.
