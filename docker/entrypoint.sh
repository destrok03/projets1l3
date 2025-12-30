#!/bin/sh
set -e

mkdir -p /var/www/html/var/cache /var/www/html/var/log
chown -R www-data:www-data /var/www/html/var
chmod -R 775 /var/www/html/var
rm -rf /var/www/html/var/cache/*

mkdir -p /var/www/html/public/assets /var/www/html/assets/vendor
chown -R www-data:www-data /var/www/html/public /var/www/html/assets
chmod -R 775 /var/www/html/public /var/www/html/assets

su -s /bin/sh www-data -c "php /var/www/html/bin/console importmap:install --no-interaction --env=prod || true"

# Use assets:install instead of non-existing asset-map:compile
su -s /bin/sh www-data -c "php /var/www/html/bin/console assets:install --symlink --relative /var/www/html/public || true"

su -s /bin/sh www-data -c "php /var/www/html/bin/console cache:clear --env=prod --no-warmup || true"
su -s /bin/sh www-data -c "php /var/www/html/bin/console cache:warmup --env=prod || true"

exec apache2-foreground
