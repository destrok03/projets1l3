<?php

namespace App\Repository;

use App\Entity\Paiement;
use App\Entity\Commande;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Paiement>
 */
class PaiementRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Paiement::class);
    }

    /**
     * Calcule les recettes du jour (commandes terminées)
     */
    public function calculerRecettesToday(): float
    {
        $today = new \DateTime('today');
        $tomorrow = new \DateTime('tomorrow');

        $result = $this->createQueryBuilder('p')
            ->select('SUM(p.montant)')
            ->join('p.commande', 'c')
            ->where('c.statut = :statut')
            ->andWhere('c.dateCommande >= :today')
            ->andWhere('c.dateCommande < :tomorrow')
            ->andWhere('p.statut = :pStatut')
            ->setParameter('statut', Commande::STATUT_TERMINEE)
            ->setParameter('today', $today)
            ->setParameter('tomorrow', $tomorrow)
            ->setParameter('pStatut', Paiement::STATUT_VALIDE)
            ->getQuery()
            ->getSingleScalarResult();

        return (float) ($result ?? 0);
    }

    /**
     * Calcule les recettes pour une période
     */
    public function calculerRecettesPeriode(\DateTime $debut, \DateTime $fin): float
    {
        $result = $this->createQueryBuilder('p')
            ->select('SUM(p.montant)')
            ->join('p.commande', 'c')
            ->where('c.statut = :statut')
            ->andWhere('c.dateCommande >= :debut')
            ->andWhere('c.dateCommande <= :fin')
            ->andWhere('p.statut = :pStatut')
            ->setParameter('statut', Commande::STATUT_TERMINEE)
            ->setParameter('debut', $debut)
            ->setParameter('fin', $fin)
            ->setParameter('pStatut', Paiement::STATUT_VALIDE)
            ->getQuery()
            ->getSingleScalarResult();

        return (float) ($result ?? 0);
    }

    /**
     * Répartition des paiements par mode du jour
     */
    public function getStatistiquesParModeToday(): array
    {
        $today = new \DateTime('today');
        $tomorrow = new \DateTime('tomorrow');

        $result = $this->createQueryBuilder('p')
            ->select('p.modePaiement, COUNT(p.id) as count, SUM(p.montant) as total')
            ->join('p.commande', 'c')
            ->where('c.dateCommande >= :today')
            ->andWhere('c.dateCommande < :tomorrow')
            ->andWhere('p.statut = :pStatut')
            ->setParameter('today', $today)
            ->setParameter('tomorrow', $tomorrow)
            ->setParameter('pStatut', Paiement::STATUT_VALIDE)
            ->groupBy('p.modePaiement')
            ->getQuery()
            ->getResult();

        $stats = [];
        foreach ($result as $row) {
            $stats[$row['modePaiement']] = [
                'count' => (int) $row['count'],
                'total' => (float) $row['total']
            ];
        }

        return $stats;
    }
}
