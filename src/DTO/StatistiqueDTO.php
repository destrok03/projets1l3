<?php

namespace App\DTO;

/**
 * DTO pour les statistiques du dashboard et page statistiques
 */
class StatistiqueDTO
{
    public int $commandesEnCours = 0;
    public int $commandesValidees = 0;
    public int $commandesAnnulees = 0;
    public int $commandesTerminees = 0;
    public float $recettesJournalieres = 0.0;
    public array $topBurgers = [];
    public array $commandesRecentes = [];
    public array $statistiquesParStatut = [];
    public array $statistiquesParModePaiement = [];

    public function getRecettesFormatees(): string
    {
        return number_format($this->recettesJournalieres, 0, ',', ' ') . ' FCFA';
    }

    public function getTotalCommandesJour(): int
    {
        return $this->commandesEnCours + $this->commandesValidees + 
               $this->commandesTerminees + $this->commandesAnnulees;
    }
}
