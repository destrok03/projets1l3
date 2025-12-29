# Statuts et transitions

Finalité:
- Définir et tracer l'état d'une commande (EN_PREPARATION, PRETE, EN_LIVRAISON, TERMINEE, ANNULEE).

Données affichées:
- Statut actuel, historique de statuts (si existant), badges dans la liste.

Actions / Endpoints associés:
- Endpoint de mise à jour de statut depuis l'interface commande (controller `CommandeController`).

Fichiers référents:
- `src/Entity/Commande.php`, `templates/commande/list.html.twig`, `templates/commande/_details_modal.html.twig`
