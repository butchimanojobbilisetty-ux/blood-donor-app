# Railway Deployment Guide

## 🚀 Deploy Blood Donor App on Railway

### Prerequisites
- GitHub account with your code pushed
- Railway account (sign up with GitHub)
- Railway CLI (optional but recommended)

---

## 📋 Step 1: Prepare Your Code for Railway

### 1.1 Push Code to GitHub
```bash
git add .
git commit -m "Ready for Railway deployment"
git push origin main
```

### 1.2 Create Railway Configuration File
Create `railway.toml` in your project root:

```toml
[build]
builder = "nixpacks"

[deploy]
healthcheckPath = "/actuator/health"
healthcheckTimeout = 100
restartPolicyType = "on_failure"
restartPolicyMaxRetries = 10

[[services]]
name = "blood-donor-app"

[services.variables]
PORT = "8080"
```

---

## 🗄️ Step 2: Set Up MySQL Database on Railway

### 2.1 Create Database Service
1. Go to [Railway Dashboard](https://railway.app/dashboard)
2. Click **"New Project"**
3. Click **"Add Service"**
4. Select **"MySQL"**
5. Choose **"MySQL"** from the database options
6. Click **"Add MySQL"**

### 2.2 Get Database Connection Details
1. Once created, click on your MySQL service
2. Go to **"Connect"** tab
3. Copy the **DATABASE_URL** (it looks like: `mysql://user:password@host:port/database`)

### 2.3 Update Your Database URL
The Railway MySQL URL format needs to be converted to Spring Boot format:
```properties
# Railway provides: mysql://user:password@host:port/database
# Convert to: jdbc:mysql://host:port/database
```

---

## 🔧 Step 3: Configure Environment Variables

### 3.1 Set Environment Variables in Railway
1. Go to your project settings
2. Click **"Variables"** tab
3. Add the following variables:

```bash
# Database Configuration
DB_URL=jdbc:mysql://your-railway-mysql-host:3306/railway
DB_USERNAME=your-railway-mysql-user
DB_PASSWORD=your-railway-mysql-password

# Email Configuration
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-gmail-app-password

# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-here
JWT_EXPIRATION=86400000

# Admin Configuration
ADMIN_USERNAME=admin
ADMIN_PASSWORD=M@noj98491

# Production Settings
SHOW_SQL=false
FORMAT_SQL=false
LOG_LEVEL=INFO
SECURITY_LOG_LEVEL=WARN
SQL_LOG_LEVEL=WARN

# CORS (update with your frontend domain)
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
```

### 3.2 Generate Strong JWT Secret
Use this command to generate a secure JWT secret:
```bash
openssl rand -base64 32
```

---

## 🚀 Step 4: Deploy Backend Application

### 4.1 Create Backend Service
1. In your Railway project, click **"Add Service"**
2. Select **"GitHub Repo"**
3. Choose your blood-donor-app repository
4. Select the branch (usually `main`)
5. Click **"Deploy Now"**

### 4.2 Configure Build Settings
Railway will automatically detect it's a Spring Boot app and build it using Maven.

### 4.3 Wait for Deployment
- Railway will build and deploy your application
- You can monitor the logs in the **"Logs"** tab
- Once deployed, you'll get a URL like `https://your-app-name.up.railway.app`

---

## ✅ Step 5: Verify Deployment

### 5.1 Check Health Endpoint
Visit: `https://your-app-url.up.railway.app/actuator/health`

You should see:
```json
{"status":"UP"}
```

### 5.2 Test API Endpoints
- Admin Login: `POST /api/admin/login`
- Donor Registration: `POST /api/donors/register`

### 5.3 Check Database Connection
If your app starts successfully, the database connection is working.

---

## 🔧 Step 6: Update Frontend Configuration

Update your frontend to use the new Railway backend URL:
```javascript
const API_BASE_URL = 'https://your-app-name.up.railway.app/api';
```

---

## 📊 Step 7: Monitor and Maintain

### 7.1 View Logs
- Go to your service in Railway dashboard
- Click **"Logs"** tab to see application logs

### 7.2 Set Up Monitoring
- Railway provides basic metrics in the dashboard
- Check response times and error rates

### 7.3 Database Management
- Access your MySQL database through Railway's built-in interface
- Or connect externally using the connection details

---

## 🚨 Troubleshooting

### Common Issues:

#### 1. Database Connection Failed
- Check if `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` are correct
- Ensure the URL format is: `jdbc:mysql://host:port/database`

#### 2. Application Won't Start
- Check the **"Logs"** tab for error messages
- Verify all environment variables are set

#### 3. CORS Errors
- Update `CORS_ALLOWED_ORIGINS` with your frontend domain
- Separate multiple domains with commas

#### 4. Memory Issues
- Railway free tier has limited memory
- Optimize your application if needed

---

## 💡 Pro Tips

### 1. Custom Domain
1. Go to **"Settings"** → **"Custom Domains"**
2. Add your domain name
3. Update DNS records as instructed

### 2. Auto-Deployments
- Enable auto-deployments in service settings
- Your app will automatically redeploy on git push

### 3. Environment-Specific Configs
- Use different Railway projects for staging/production
- Each can have different environment variables

### 4. Backup Strategy
- Railway automatically backs up MySQL databases
- Export your database regularly for additional safety

---

## 📝 Cost Summary

### Railway Free Tier:
- **$5 credit/month** (renews monthly)
- **MySQL**: ~$2-3/month
- **Spring Boot App**: ~$1-2/month
- **Total**: Within free tier limits

### What's Included:
- 512MB RAM per service
- Shared CPU
- 100GB bandwidth
- Automatic SSL certificates

---

## 🎉 You're Live!

Your Blood Donor App is now deployed on Railway with:
- ✅ MySQL database
- ✅ Spring Boot backend
- ✅ HTTPS security
- ✅ Automatic deployments
- ✅ Environment variable management

For any issues, check the Railway logs or contact Railway support.
