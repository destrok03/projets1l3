<?php

namespace App\Service;

use App\Entity\Commande;
use App\Entity\Livreur;
use App\Repository\CommandeRepository;
use App\Repository\LivreurRepository;
use App\Repository\ZoneRepository;
use Doctrine\ORM\EntityManagerInterface;

class LivraisonService
{
    public function __construct(
        private CommandeRepository $commandeRepo,
        private LivreurRepository $livreurRepo,
        private ZoneRepository $zoneRepo,
        private EntityManagerInterface $em
    ) {}

    /**
     * Regroupe les commandes à livrer par zone
     * @return array Structure: ['zoneName' => ['zone' => Zone, 'commandes' => Commande[]]]
     */
    public function getCommandesParZone(): array
    {
        $commandes = $this->commandeRepo->findCommandesALivrerByZone();
        
        $result = [];
        foreach ($commandes as $commande) {
            $zoneName = $commande->getZone()?->getNom() ?? 'Sans zone';
            $zoneId = $commande->getZone()?->getId() ?? 0;
            
            if (!isset($result[$zoneId])) {
                $result[$zoneId] = [
                    'zone' => $commande->getZone(),
                    'zoneName' => $zoneName,
                    'commandes' => [],
                    'count' => 0
                ];
            }
            $result[$zoneId]['commandes'][] = $commande;
            $result[$zoneId]['count']++;
        }
        
        return $result;
    }

    /**
     * Affecte un livreur à une commande
     */
    public function affecterLivreur(Commande $commande, Livreur $livreur): bool
    {
        if (!$commande->canBeModified()) {
            return false;
        }

        $commande->setLivreur($livreur);
        $commande->setStatut(Commande::STATUT_EN_LIVRAISON);
        
        $this->em->flush();
        
        return true;
    }

    /**
     * Affecte un livreur à plusieurs commandes d'une même zone
     */
    public function affecterLivreurMultiple(array $commandeIds, Livreur $livreur): int
    {
        $count = 0;
        
        foreach ($commandeIds as $commandeId) {
            $commande = $this->commandeRepo->find($commandeId);
            if ($commande && $this->affecterLivreur($commande, $livreur)) {
                $count++;
            }
        }
        
        return $count;
    }

    /**
     * Récupère les livreurs disponibles pour une zone
     */
    public function getLivreursDisponiblesParZone(int $zoneId): array
    {
        return $this->livreurRepo->findDisponiblesByZone($zoneId);
    }

    /**
     * Récupère tous les livreurs disponibles
     */
    public function getLivreursDisponibles(): array
    {
        return $this->livreurRepo->findDisponibles();
    }

    /**
     * Toggle la disponibilité d'un livreur
     */
    public function toggleDisponibilite(Livreur $livreur): bool
    {
        $livreur->setDisponible(!$livreur->isDisponible());
        $this->em->flush();
        
        return $livreur->isDisponible();
    }

    /**
     * Compte le nombre de commandes en attente de livraison
     */
    public function countCommandesEnAttenteLivraison(): int
    {
        $commandesParZone = $this->getCommandesParZone();
        $count = 0;
        
        foreach ($commandesParZone as $data) {
            $count += $data['count'];
        }
        
        return $count;
    }
}
