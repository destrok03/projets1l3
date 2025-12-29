# Affectation livreur

Finalité:
- Assigner un livreur à une commande (automatique ou manuelle), vérifier disponibilité et persister l'affectation.

Données affichées:
- Nom du livreur, disponibilité, nombre de livraisons en cours.

Actions / Endpoints associés:
- `/livraison/affecter` via `LivraisonService::affecterLivreur()` et `LivraisonController`.

Fichiers référents:
- `src/Service/LivraisonService.php`, `src/Controller/LivraisonController.php`, `src/Entity/Livreur.php`
