<?php

namespace App\Controller;

use App\Entity\Commande;
use App\Repository\CommandeRepository;
use App\Repository\LivreurRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/commandes')]
class CommandeController extends AbstractController
{
    public function __construct(
        private CommandeRepository $commandeRepo,
        private EntityManagerInterface $em
    ) {}

    #[Route('', name: 'app_commande_list', methods: ['GET'])]
    public function list(Request $request): Response
    {
        $page = $request->query->getInt('page', 1);
        $limit = $this->getParameter('limite_par_page');
        $offset = ($page - 1) * $limit;

        // Récupération des filtres
        $filters = [
            'typeLivraison' => $request->query->get('type'),
            'statut' => $request->query->get('statut'),
            'client' => $request->query->get('client'),
            'livreur' => $request->query->getInt('livreur'),
            'dateDebut' => $request->query->get('dateDebut'),
            'dateFin' => $request->query->get('dateFin'),
        ];

        // Récupération des commandes avec filtres
        $commandes = $this->commandeRepo->findWithFilters($filters, $limit, $offset);
        $total = $this->commandeRepo->countWithFilters($filters);
        $nbrePages = ceil($total / $limit);

        return $this->render('commande/list.html.twig', [
            'commandes' => $commandes,
            'filters' => $filters,
            'nbrePages' => $nbrePages,
            'pageEncours' => $page,
            'total' => $total
        ]);
    }

    #[Route('/{id}/details', name: 'app_commande_details', methods: ['GET'])]
    public function details(Commande $commande, LivreurRepository $livreurRepo): Response
    {
        $livreurs = [];
        if ($commande->needsDelivery() && $commande->getZone()) {
            $livreurs = $livreurRepo->findDisponiblesByZone($commande->getZone()->getId());
        }

        return $this->render('commande/_details_modal.html.twig', [
            'commande' => $commande,
            'livreurs' => $livreurs
        ]);
    }

    #[Route('/{id}/statut', name: 'app_commande_change_statut', methods: ['POST'])]
    public function changeStatut(Request $request, Commande $commande): JsonResponse
    {
        $nouveauStatut = $request->request->get('statut');
        
        // Validation du statut
        $statutsValides = array_values(Commande::getStatuts());
        if (!in_array($nouveauStatut, $statutsValides)) {
            return new JsonResponse(['error' => 'Statut invalide'], Response::HTTP_BAD_REQUEST);
        }

        // Vérification que la commande peut être modifiée
        if (!$commande->canBeModified() && $nouveauStatut !== Commande::STATUT_ANNULEE) {
            return new JsonResponse(['error' => 'Cette commande ne peut plus être modifiée'], Response::HTTP_BAD_REQUEST);
        }

        $commande->setStatut($nouveauStatut);
        $this->em->flush();

        return new JsonResponse([
            'success' => true,
            'statut' => $commande->getStatut(),
            'label' => $commande->getStatutLabel(),
            'badgeClass' => $commande->getStatutBadgeClass()
        ]);
    }

    #[Route('/{id}/annuler', name: 'app_commande_annuler', methods: ['POST'])]
    public function annuler(Commande $commande): JsonResponse
    {
        if (!$commande->canBeCancelled()) {
            return new JsonResponse([
                'success' => false,
                'error' => 'Cette commande ne peut pas être annulée'
            ], Response::HTTP_BAD_REQUEST);
        }

        $commande->setStatut(Commande::STATUT_ANNULEE);
        $this->em->flush();

        $this->addFlash('success', 'Commande ' . $commande->getNumero() . ' annulée avec succès.');

        return new JsonResponse([
            'success' => true,
            'message' => 'Commande annulée'
        ]);
    }

    #[Route('/{id}/affecter-livreur', name: 'app_commande_affecter_livreur', methods: ['POST'])]
    public function affecterLivreur(Request $request, Commande $commande, LivreurRepository $livreurRepo): JsonResponse
    {
        $livreurId = $request->request->getInt('livreur_id');
        $livreur = $livreurRepo->find($livreurId);

        if (!$livreur) {
            return new JsonResponse(['error' => 'Livreur non trouvé'], Response::HTTP_BAD_REQUEST);
        }

        if (!$livreur->isDisponible() || !$livreur->isActif()) {
            return new JsonResponse(['error' => 'Ce livreur n\'est pas disponible'], Response::HTTP_BAD_REQUEST);
        }

        $commande->setLivreur($livreur);
        $commande->setStatut(Commande::STATUT_EN_LIVRAISON);
        $this->em->flush();

        return new JsonResponse([
            'success' => true,
            'message' => 'Livreur ' . $livreur->getNomComplet() . ' affecté à la commande ' . $commande->getNumero()
        ]);
    }

    #[Route('/{id}/terminer', name: 'app_commande_terminer', methods: ['POST'])]
    public function terminer(Commande $commande): JsonResponse
    {
        $commande->setStatut(Commande::STATUT_TERMINEE);
        $this->em->flush();

        return new JsonResponse([
            'success' => true,
            'message' => 'Commande terminée'
        ]);
    }

    #[Route('/{id}/facture', name: 'app_commande_facture', methods: ['GET'])]
    public function facture(Commande $commande): Response
    {
        // Render invoice HTML
        $html = $this->renderView('commande/facture.html.twig', [
            'commande' => $commande
        ]);

        $baseFilename = sprintf('facture-%s', $commande->getNumero() ?? $commande->getId());

        // If Dompdf is available, generate PDF, otherwise return HTML attachment
        if (class_exists('Dompdf\\Dompdf')) {
            $options = new \Dompdf\Options();
            $options->set('isRemoteEnabled', true);
            $options->set('defaultFont', 'DejaVu Sans');

            $dompdf = new \Dompdf\Dompdf($options);
            $dompdf->loadHtml($html);
            $dompdf->setPaper('A4', 'portrait');
            $dompdf->render();

            $pdf = $dompdf->output();
            $filename = $baseFilename . '.pdf';

            return new Response($pdf, 200, [
                'Content-Type' => 'application/pdf',
                'Content-Disposition' => 'attachment; filename="' . $filename . '"'
            ]);
        }

        // Fallback: return HTML attachment
        $filename = $baseFilename . '.html';
        return new Response($html, 200, [
            'Content-Type' => 'text/html; charset=utf-8',
            'Content-Disposition' => 'attachment; filename="' . $filename . '"'
        ]);
    }
}
