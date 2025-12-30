# Dockerfile for Symfony (production-ready)
FROM php:8.4-apache

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
RUN a2enmod rewrite

# Install Composer from official image
# Install Composer from official image
COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

# Set working directory
WORKDIR /var/www/html

# Copy composer files first to leverage layer cache
COPY composer.json composer.lock ./

RUN composer install \
  --no-dev \
  --optimize-autoloader \
  --no-interaction \
  --no-scripts

# Copy application source
COPY . .

RUN mkdir -p var/cache var/log public/assets assets/vendor \
 && chown -R www-data:www-data var public assets \
 && chmod -R 775 var public assets

# Run importmap install at build if possible (non-fatal)
RUN su -s /bin/sh www-data -c "php bin/console importmap:install --no-interaction --env=prod || true"

# Write Apache vhost forcing DocumentRoot to the Symfony `public` directory
RUN cat > /etc/apache2/sites-available/000-default.conf <<'EOF'
<VirtualHost *:80>
  ServerName localhost
  DocumentRoot /var/www/html/public

  <Directory /var/www/html/public>
    AllowOverride All
    Require all granted
    DirectoryIndex index.php index.html
    FallbackResource /index.php
  </Directory>

  ErrorLog /var/log/apache2/error.log
  CustomLog /var/log/apache2/access.log combined
</VirtualHost>
EOF

COPY docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 80
ENTRYPOINT ["/entrypoint.sh"]
