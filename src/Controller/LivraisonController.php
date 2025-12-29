<?php

namespace App\Controller;

use App\Entity\Commande;
use App\Entity\Livreur;
use App\Repository\CommandeRepository;
use App\Repository\LivreurRepository;
use App\Repository\ZoneRepository;
use App\Service\LivraisonService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/livraison')]
class LivraisonController extends AbstractController
{
    public function __construct(
        private ZoneRepository $zoneRepo,
        private LivreurRepository $livreurRepo,
        private LivraisonService $livraisonService,
        private EntityManagerInterface $em
    ) {}

    #[Route('', name: 'app_livraison')]
    public function index(): Response
    {
        return $this->redirectToRoute('app_livraison_zones');
    }

    #[Route('/zones', name: 'app_livraison_zones', methods: ['GET'])]
    public function zones(): Response
    {
        $zones = $this->zoneRepo->findAllWithQuartiers();
        $commandesParZone = $this->livraisonService->getCommandesParZone();
        
        return $this->render('livraison/zones.html.twig', [
            'zones' => $zones,
            'commandesParZone' => $commandesParZone
        ]);
    }

    #[Route('/livreurs', name: 'app_livraison_livreurs', methods: ['GET'])]
    public function livreurs(): Response
    {
        $livreurs = $this->livreurRepo->findAllWithZones();
        
        return $this->render('livraison/livreurs.html.twig', [
            'livreurs' => $livreurs
        ]);
    }

    #[Route('/gerer', name: 'app_livraison_gerer', methods: ['GET'])]
    public function gererLivraisons(): Response
    {
        $commandesParZone = $this->livraisonService->getCommandesParZone();
        $livreurs = $this->livreurRepo->findDisponibles();

        return $this->render('livraison/gerer.html.twig', [
            'commandesParZone' => $commandesParZone,
            'livreurs' => $livreurs
        ]);
    }

    #[Route('/commandes-par-zone', name: 'app_livraison_par_zone', methods: ['GET'])]
    public function commandesParZone(): Response
    {
        $commandesParZone = $this->livraisonService->getCommandesParZone();
        $livreurs = $this->livreurRepo->findDisponibles();

        return $this->render('livraison/_commandes_par_zone_modal.html.twig', [
            'commandesParZone' => $commandesParZone,
            'livreurs' => $livreurs
        ]);
    }

    #[Route('/affecter', name: 'app_livraison_affecter', methods: ['POST'])]
    public function affecterLivreur(Request $request, CommandeRepository $commandeRepo): JsonResponse
    {
        $commandeId = $request->request->getInt('commande_id');
        $livreurId = $request->request->getInt('livreur_id');

        $commande = $commandeRepo->find($commandeId);
        $livreur = $this->livreurRepo->find($livreurId);

        if (!$commande || !$livreur) {
            return new JsonResponse(['error' => 'Données invalides'], Response::HTTP_BAD_REQUEST);
        }

        if (!$livreur->isDisponible() || !$livreur->isActif()) {
            return new JsonResponse(['error' => 'Ce livreur n\'est pas disponible'], Response::HTTP_BAD_REQUEST);
        }

        $success = $this->livraisonService->affecterLivreur($commande, $livreur);

        if (!$success) {
            return new JsonResponse(['error' => 'Impossible d\'affecter le livreur'], Response::HTTP_BAD_REQUEST);
        }

        return new JsonResponse([
            'success' => true,
            'message' => "Livreur {$livreur->getNomComplet()} affecté à la commande {$commande->getNumero()}"
        ]);
    }

    #[Route('/livreur/{id}/toggle-disponibilite', name: 'app_livreur_toggle', methods: ['POST'])]
    public function toggleDisponibilite(Livreur $livreur): JsonResponse
    {
        $nouvelleDisponibilite = $this->livraisonService->toggleDisponibilite($livreur);

        return new JsonResponse([
            'success' => true,
            'disponible' => $nouvelleDisponibilite,
            'message' => $nouvelleDisponibilite ? 'Livreur maintenant disponible' : 'Livreur maintenant indisponible'
        ]);
    }

    #[Route('/livreurs-zone/{zoneId}', name: 'app_livraison_livreurs_zone', methods: ['GET'])]
    public function livreursParZone(int $zoneId): JsonResponse
    {
        $livreurs = $this->livreurRepo->findDisponiblesByZone($zoneId);
        
        $data = array_map(function($livreur) {
            return [
                'id' => $livreur->getId(),
                'nom' => $livreur->getNomComplet(),
                'telephone' => $livreur->getTelephone()
            ];
        }, $livreurs);

        return new JsonResponse($data);
    }

    #[Route('/livreur/{id}/delete', name: 'app_livreur_delete', methods: ['POST'])]
    public function deleteLivreur(Request $request, Livreur $livreur): JsonResponse
    {
        $token = $request->request->get('_token');
        if (!$this->isCsrfTokenValid('delete-livreur' . $livreur->getId(), $token)) {
            return new JsonResponse(['success' => false, 'error' => 'Token CSRF invalide'], Response::HTTP_FORBIDDEN);
        }

        try {
            $this->livreurRepo->remove($livreur, true);
        } catch (\Exception $e) {
            return new JsonResponse(['success' => false, 'error' => 'Erreur lors de la suppression'], Response::HTTP_INTERNAL_SERVER_ERROR);
        }

        return new JsonResponse(['success' => true, 'message' => 'Livreur supprimé']);
    }
}
