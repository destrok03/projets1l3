#!/usr/bin/env bash
set -e

# Docker entrypoint: perform runtime tasks that require env vars, then start Apache
# - Runs composer dump-env if APP_ENV=prod and APP_SECRET is set
# - Clears and warms up Symfony cache in prod
# - Ensures permissions for var and public

mkdir -p var var/cache var/log public
touch var/log/prod.log || true
chown -R www-data:www-data var public || true
chmod -R 0755 var || true

if [ "${APP_ENV:-prod}" = "prod" ]; then
  if [ -n "${APP_SECRET:-}" ]; then
    composer dump-env prod --no-interaction || true
  fi
  php bin/console cache:clear --no-warmup --no-interaction || true
  php bin/console cache:warmup --no-interaction || true
fi

# Finally exec Apache in foreground using the base image entrypoint
exec docker-php-entrypoint apache2-foreground
