# Dockerfile for Brasil Burger Symfony Application
FROM php:8.2-apache

# Install system dependencies
RUN apt-get update && apt-get install -y \
    git \
    curl \
    libpng-dev \
    libonig-dev \
    libxml2-dev \
    libpq-dev \
    zip \
    unzip \
    && docker-php-ext-install pdo pdo_pgsql pgsql mbstring exif pcntl bcmath gd \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Enable Apache mod_rewrite
RUN a2enmod rewrite

# Install Composer
COPY --from=composer:latest /usr/bin/composer /usr/bin/composer

# Set working directory
WORKDIR /var/www/html

# Copy application files
COPY . .

# Install dependencies (don't run composer scripts during build)
RUN composer install --no-dev --optimize-autoloader --no-interaction --no-progress --no-scripts

# Ensure runtime directories exist and set permissions (avoid failure if missing)
RUN mkdir -p /var/www/html/var /var/www/html/var/cache /var/www/html/var/log /var/www/html/public \
    && chown -R www-data:www-data /var/www/html/var /var/www/html/public || true \
    && chmod -R 755 /var/www/html/var || true

# Apache configuration
# Write a proper virtual host file using a heredoc and explicit log paths
RUN cat > /etc/apache2/sites-available/000-default.conf <<'EOF'
<VirtualHost *:80>
    DocumentRoot /var/www/html/public
    <Directory /var/www/html/public>
        AllowOverride All
        Require all granted
        FallbackResource /index.php
    </Directory>
    ErrorLog /var/log/apache2/error.log
    CustomLog /var/log/apache2/access.log combined
</VirtualHost>
EOF

# Clear cache and warmup (force production env so dev bundles are not required)
RUN APP_ENV=prod php bin/console cache:clear --no-debug --env=prod || true
RUN APP_ENV=prod php bin/console cache:warmup --env=prod || true

# Expose port 80
EXPOSE 80

# Start Apache
CMD ["apache2-foreground"]
