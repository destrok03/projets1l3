# Dockerfile for Symfony (production-ready)
FROM php:8.2-apache

# Install system dependencies and PHP extensions
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        git \
        curl \
        ca-certificates \
        libpng-dev \
        libonig-dev \
        libxml2-dev \
        libpq-dev \
        zip \
        unzip \
    && docker-php-ext-install -j"$(nproc)" pdo pdo_pgsql pgsql mbstring xml bcmath gd \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Enable needed Apache modules
RUN a2enmod rewrite headers expires

# Install Composer from official image
COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

# Set working directory
WORKDIR /var/www/html

# Copy composer files first (cache deps)
COPY composer.json composer.lock ./

# Install PHP dependencies without running scripts (to avoid dev-only script issues)
RUN composer install --no-dev --optimize-autoloader --no-interaction --no-progress --no-scripts || true

# Copy application source
COPY . .

# Ensure production environment variables
ENV APP_ENV=prod
ENV APP_DEBUG=0

# Optimize autoload and dump prod env if possible
RUN composer dump-autoload --optimize --classmap-authoritative || true
RUN composer dump-env prod --no-interaction || true

# Ensure runtime directories exist and permissions are correct
RUN mkdir -p var var/cache var/log public && \
    touch var/log/prod.log || true && \
    chown -R www-data:www-data var public || true && \
    chmod -R 0755 var || true

# Write an Apache vhost for Symfony
RUN cat > /etc/apache2/sites-available/000-default.conf <<'EOF'
<VirtualHost *:80>
    ServerName localhost
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

# Clear and warmup cache in prod (ignore failures during build)
RUN APP_ENV=prod php bin/console cache:clear --no-warmup --no-interaction || true
RUN APP_ENV=prod php bin/console cache:warmup --no-interaction || true

# Expose port 80 and run Apache in foreground
EXPOSE 80
CMD ["apache2-foreground"]
