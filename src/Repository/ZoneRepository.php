<?php

namespace App\Repository;

use App\Entity\Zone;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Zone>
 */
class ZoneRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Zone::class);
    }

    public function save(Zone $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(Zone $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    /**
     * Toutes les zones avec leurs quartiers
     */
    public function findAllWithQuartiers(): array
    {
        return $this->createQueryBuilder('z')
            ->leftJoin('z.quartiers', 'q')
            ->addSelect('q')
            ->orderBy('z.nom', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Zones avec livreurs disponibles
     */
    public function findWithAvailableLivreurs(): array
    {
        return $this->createQueryBuilder('z')
            ->leftJoin('z.livreurs', 'l')
            ->addSelect('l')
            ->where('l.disponible = true')
            ->andWhere('l.actif = true')
            ->orderBy('z.nom', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Zones ayant des commandes en attente de livraison
     */
    public function findZonesWithPendingDeliveries(): array
    {
        return $this->createQueryBuilder('z')
            ->innerJoin('z.commandes', 'c')
            ->where('c.typeLivraison = :type')
            ->andWhere('c.statut IN (:statuts)')
            ->andWhere('c.livreur IS NULL')
            ->setParameter('type', 'LIVRAISON')
            ->setParameter('statuts', ['EN_PREPARATION', 'PRETE', 'VALIDEE'])
            ->groupBy('z.id')
            ->orderBy('z.nom', 'ASC')
            ->getQuery()
            ->getResult();
    }
}
