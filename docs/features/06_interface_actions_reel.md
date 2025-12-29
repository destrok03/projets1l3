# Interface & actions temps réel

Finalité:
- Permettre les actions rapides en UI: marquer prêt, passer en livraison, affecter livreur, consulter facture via modales et boutons AJAX.

Données affichées:
- Boutons d'action, modale détails, état de paiement, frais.

Actions / Endpoints associés:
- Endpoints AJAX pour affecter et changer le statut (ex: `/livraison/affecter`).

Fichiers référents:
- `templates/commande/_details_modal.html.twig`, `templates/livraison/gerer.html.twig`, assets JS.
