# Production Deployment Guide

## 🚀 Environment Setup

### 1. Set Environment Variables

Create a `.env` file in your project root with your production values:

```bash
# Copy the template
cp .env.template .env

# Update with your actual values
nano .env
```

### 2. Required Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | Database connection string | `jdbc:mysql://your-db:3306/blood_donor_db` |
| `DB_USERNAME` | Database username | `your_db_user` |
| `DB_PASSWORD` | Database password | `your_secure_password` |
| `EMAIL_USERNAME` | Gmail address | `your-email@gmail.com` |
| `EMAIL_PASSWORD` | Gmail app password | `your-app-password` |
| `JWT_SECRET` | 256-bit secret key | `your-256-bit-secret-key` |
| `ADMIN_PASSWORD` | Admin login password | `your-admin-password` |
| `CORS_ALLOWED_ORIGINS` | Allowed domains | `https://yourdomain.com` |

### 3. Security Requirements

#### Database Security
- Use strong password for database
- Enable SSL connection in production
- Use dedicated database user with limited privileges

#### Email Security
- Use Gmail App Password (not regular password)
- Enable 2FA on Gmail account
- Consider using dedicated email service like SendGrid

#### JWT Security
- Generate a strong 256-bit secret key
- Store securely in environment variables
- Rotate keys periodically

#### Admin Security
- Use strong admin password
- Consider implementing 2FA for admin
- Use IP whitelisting if possible

### 4. Deployment Commands

#### Using Docker (Recommended)
```bash
# Build the application
mvn clean package

# Run with environment variables
docker run -d \
  --name blood-donor-app \
  --env-file .env \
  -p 8080:8080 \
  blood-donor-app:latest
```

#### Using Systemd (Linux)
```bash
# Create service file
sudo nano /etc/systemd/system/blood-donor.service

# Copy your .env to system location
sudo cp .env /etc/blood-donor/.env

# Start service
sudo systemctl start blood-donor
sudo systemctl enable blood-donor
```

#### Direct Java
```bash
# Load environment variables
source .env

# Run application
java -jar target/blood-donor-app-1.0.0.jar
```

### 5. Production Checklist

- [ ] All environment variables set
- [ ] Database connection tested
- [ ] Email service tested
- [ ] JWT secret is strong and unique
- [ ] Admin password is strong
- [ ] CORS configured for production domain
- [ ] SSL/TLS configured
- [ ] Firewall configured
- [ ] Backup strategy in place
- [ ] Monitoring set up
- [ ] Log rotation configured

### 6. Monitoring

#### Health Check Endpoint
```bash
curl http://localhost:8080/actuator/health
```

#### Application Info
```bash
curl http://localhost:8080/actuator/info
```

### 7. Troubleshooting

#### Common Issues
1. **Database Connection Failed**
   - Check DB_URL format
   - Verify credentials
   - Check network connectivity

2. **Email Not Sending**
   - Verify Gmail app password
   - Check SMTP settings
   - Ensure 2FA is enabled

3. **JWT Token Issues**
   - Verify JWT_SECRET is set
   - Check token expiration
   - Ensure secret is same across restarts

4. **CORS Issues**
   - Update CORS_ALLOWED_ORIGINS
   - Check domain configuration
   - Verify preflight requests

### 8. Security Best Practices

1. **Never commit `.env` file to version control**
2. **Use different credentials for each environment**
3. **Rotate secrets regularly**
4. **Monitor application logs**
5. **Implement rate limiting**
6. **Use HTTPS in production**
7. **Regular security updates**

### 9. Backup Strategy

#### Database Backup
```bash
# Daily backup
mysqldump -u $DB_USERNAME -p blood_donor_db > backup_$(date +%Y%m%d).sql

# Automated backup script
0 2 * * * /path/to/backup_script.sh
```

#### Application Backup
- Store JAR file version
- Backup configuration files
- Document environment variables

### 10. Performance Optimization

#### Database
- Use connection pooling (configured)
- Add indexes for frequently queried fields
- Monitor slow queries

#### Application
- Enable GZIP compression
- Use CDN for static assets
- Implement caching where appropriate

## 🎯 You're Ready for Production!

After completing the above steps, your application will be:
- ✅ Secure with environment variables
- ✅ Optimized with connection pooling
- ✅ Production-ready logging
- ✅ CORS configured for production
- ✅ Health monitoring enabled
