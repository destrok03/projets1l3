<?php

namespace App\Service;

use App\DTO\StatistiqueDTO;
use App\Repository\CommandeRepository;
use App\Repository\PaiementRepository;
use App\Repository\ProduitRepository;

class StatistiqueService
{
    public function __construct(
        private CommandeRepository $commandeRepo,
        private PaiementRepository $paiementRepo,
        private ProduitRepository $produitRepo
    ) {}

    /**
     * Génère les statistiques journalières pour le dashboard
     */
    public function getStatistiquesJournalieres(): StatistiqueDTO
    {
        $dto = new StatistiqueDTO();
        
        // Compteurs de commandes
        $dto->commandesEnCours = $this->commandeRepo->countCommandesEnCoursToday();
        $dto->commandesValidees = $this->commandeRepo->countCommandesValideesToday();
        $dto->commandesAnnulees = $this->commandeRepo->countCommandesAnnuleesToday();
        $dto->commandesTerminees = $this->commandeRepo->countCommandesTermineesToday();
        
        // Recettes du jour
        $dto->recettesJournalieres = $this->paiementRepo->calculerRecettesToday();
        
        // Top burgers vendus
        $dto->topBurgers = $this->produitRepo->findTopBurgersToday(5);
        
        // Commandes récentes
        $dto->commandesRecentes = $this->commandeRepo->findRecentCommandes(5);
        
        // Stats par statut
        $dto->statistiquesParStatut = $this->commandeRepo->getStatistiquesParStatutToday();
        
        // Stats par mode de paiement
        $dto->statistiquesParModePaiement = $this->paiementRepo->getStatistiquesParModeToday();

        return $dto;
    }

    /**
     * Génère les statistiques détaillées pour la page statistiques
     */
    public function getStatistiquesDetaillees(): StatistiqueDTO
    {
        $dto = $this->getStatistiquesJournalieres();
        
        // Ajouter des données supplémentaires pour la page stats
        $dto->topBurgers = $this->produitRepo->findTopBurgersToday(10);
        
        return $dto;
    }
}
