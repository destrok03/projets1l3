#!/usr/bin/env bash
set -euo pipefail

# Script post-deploy pour Render (exécuter après que le container soit démarré)
# Usage: ./scripts/post_deploy.sh

RETRY=0
MAX_RETRIES=12
SLEEP=5

# Attendre la disponibilité de la base si nécessaire (DATABASE_URL doit être configurée dans les env vars)
until php bin/console doctrine:query:sql "SELECT 1" > /dev/null 2>&1; do
  RETRY=$((RETRY+1))
  if [ "$RETRY" -ge "$MAX_RETRIES" ]; then
    echo "La base de données est inaccessible après $((MAX_RETRIES * SLEEP)) secondes. Abandon."
    exit 1
  fi
  echo "Attente DB... tentative $RETRY/$MAX_RETRIES"
  sleep $SLEEP
done

# Appliquer les migrations
php bin/console doctrine:migrations:migrate --no-interaction --allow-no-migration

# Vider et préchauffer le cache en prod
php bin/console cache:clear --env=prod --no-debug
php bin/console cache:warmup --env=prod --no-debug

# (Optionnel) charger fixtures uniquement si souhaité (décommenter si besoin)
# php bin/console doctrine:fixtures:load --no-interaction --append

echo "Post-deploy terminé."
