# Regroupement et gestion par zone

Finalité:
- Regrouper les commandes par zone pour optimiser les tournées et appliquer des tarifs par zone.

Données affichées:
- Liste des commandes par zone, tarifs, statut de la zone (active/inactive).

Actions / Endpoints associés:
- Toggle zone: `/livraison/zone/{id}/toggle` (fetch JS).

Fichiers référents:
- `src/Service/LivraisonService.php`, `src/Entity/Zone.php`, `templates/livraison/zones.html.twig`
