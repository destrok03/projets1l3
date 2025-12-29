<?php

namespace App\Repository;

use App\Entity\Commande;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Commande>
 */
class CommandeRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Commande::class);
    }

    public function save(Commande $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    /**
     * Compte les commandes du jour par statut(s)
     */
    public function countByStatutToday(array $statuts): int
    {
        $today = new \DateTime('today');
        $tomorrow = new \DateTime('tomorrow');

        return (int) $this->createQueryBuilder('c')
            ->select('COUNT(c.id)')
            ->where('c.statut IN (:statuts)')
            ->andWhere('c.dateCommande >= :today')
            ->andWhere('c.dateCommande < :tomorrow')
            ->setParameter('statuts', $statuts)
            ->setParameter('today', $today)
            ->setParameter('tomorrow', $tomorrow)
            ->getQuery()
            ->getSingleScalarResult();
    }

    /**
     * Commandes en cours du jour (EN_PREPARATION, PRETE, EN_LIVRAISON)
     */
    public function countCommandesEnCoursToday(): int
    {
        return $this->countByStatutToday([
            Commande::STATUT_EN_PREPARATION,
            Commande::STATUT_PRETE,
            Commande::STATUT_EN_LIVRAISON
        ]);
    }

    /**
     * Commandes validées du jour
     */
    public function countCommandesValideesToday(): int
    {
        return $this->countByStatutToday([Commande::STATUT_VALIDEE]);
    }

    /**
     * Commandes annulées du jour
     */
    public function countCommandesAnnuleesToday(): int
    {
        return $this->countByStatutToday([Commande::STATUT_ANNULEE]);
    }

    /**
     * Commandes terminées du jour
     */
    public function countCommandesTermineesToday(): int
    {
        return $this->countByStatutToday([Commande::STATUT_TERMINEE]);
    }

    /**
     * Récupère les commandes récentes
     */
    public function findRecentCommandes(int $limit = 5): array
    {
        return $this->createQueryBuilder('c')
            ->leftJoin('c.client', 'cl')
            ->addSelect('cl')
            ->orderBy('c.dateCommande', 'DESC')
            ->setMaxResults($limit)
            ->getQuery()
            ->getResult();
    }

    /**
     * Recherche avec filtres et pagination
     */
    public function findWithFilters(array $filters, int $limit, int $offset): array
    {
        $qb = $this->createQueryBuilder('c')
            ->leftJoin('c.client', 'cl')
            ->addSelect('cl')
            ->leftJoin('c.zone', 'z')
            ->addSelect('z')
            ->leftJoin('c.livreur', 'l')
            ->addSelect('l');

        $this->applyFilters($qb, $filters);

        return $qb->orderBy('c.dateCommande', 'DESC')
            ->setMaxResults($limit)
            ->setFirstResult($offset)
            ->getQuery()
            ->getResult();
    }

    /**
     * Compte avec filtres
     */
    public function countWithFilters(array $filters): int
    {
        $qb = $this->createQueryBuilder('c')
            ->select('COUNT(c.id)')
            ->leftJoin('c.client', 'cl');

        $this->applyFilters($qb, $filters);

        return (int) $qb->getQuery()->getSingleScalarResult();
    }

    /**
     * Applique les filtres au QueryBuilder
     */
    private function applyFilters($qb, array $filters): void
    {
        // Filtre par type de livraison
        if (!empty($filters['typeLivraison'])) {
            $qb->andWhere('c.typeLivraison = :typeLivraison')
                ->setParameter('typeLivraison', $filters['typeLivraison']);
        }

        // Filtre par statut
        if (!empty($filters['statut'])) {
            $qb->andWhere('c.statut = :statut')
                ->setParameter('statut', $filters['statut']);
        }

        // Filtre par client (recherche nom, prénom, téléphone)
        if (!empty($filters['client'])) {
            $qb->andWhere('cl.nom LIKE :client OR cl.prenom LIKE :client OR cl.telephone LIKE :client')
                ->setParameter('client', '%' . $filters['client'] . '%');
        }

        // Filtre par date début
        if (!empty($filters['dateDebut'])) {
            $qb->andWhere('c.dateCommande >= :dateDebut')
                ->setParameter('dateDebut', new \DateTime($filters['dateDebut']));
        }

        // Filtre par date fin
        if (!empty($filters['dateFin'])) {
            $qb->andWhere('c.dateCommande <= :dateFin')
                ->setParameter('dateFin', new \DateTime($filters['dateFin'] . ' 23:59:59'));
        }

        // Filtre par livreur
        if (!empty($filters['livreur'])) {
            $qb->andWhere('c.livreur = :livreur')
                ->setParameter('livreur', (int) $filters['livreur']);
        }
    }

    /**
     * Commandes à livrer regroupées par zone
     */
    public function findCommandesALivrerByZone(): array
    {
        return $this->createQueryBuilder('c')
            ->leftJoin('c.zone', 'z')
            ->addSelect('z')
            ->leftJoin('c.client', 'cl')
            ->addSelect('cl')
            ->where('c.typeLivraison = :type')
            ->andWhere('c.statut IN (:statuts)')
            ->andWhere('c.livreur IS NULL')
            ->setParameter('type', Commande::TYPE_LIVRAISON)
            ->setParameter('statuts', [
                Commande::STATUT_EN_PREPARATION,
                Commande::STATUT_PRETE,
                Commande::STATUT_VALIDEE
            ])
            ->orderBy('z.nom', 'ASC')
            ->addOrderBy('c.dateCommande', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Commandes d'un livreur
     */
    public function findByLivreur(int $livreurId, ?string $statut = null): array
    {
        $qb = $this->createQueryBuilder('c')
            ->where('c.livreur = :livreurId')
            ->setParameter('livreurId', $livreurId);

        if ($statut) {
            $qb->andWhere('c.statut = :statut')
                ->setParameter('statut', $statut);
        }

        return $qb->orderBy('c.dateCommande', 'DESC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Statistiques des commandes par statut du jour
     */
    public function getStatistiquesParStatutToday(): array
    {
        $today = new \DateTime('today');
        $tomorrow = new \DateTime('tomorrow');

        $result = $this->createQueryBuilder('c')
            ->select('c.statut, COUNT(c.id) as total')
            ->where('c.dateCommande >= :today')
            ->andWhere('c.dateCommande < :tomorrow')
            ->setParameter('today', $today)
            ->setParameter('tomorrow', $tomorrow)
            ->groupBy('c.statut')
            ->getQuery()
            ->getResult();

        $stats = [];
        foreach ($result as $row) {
            $stats[$row['statut']] = (int) $row['total'];
        }

        return $stats;
    }
}
