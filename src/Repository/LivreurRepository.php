<?php

namespace App\Repository;

use App\Entity\Livreur;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Livreur>
 */
class LivreurRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Livreur::class);
    }

    public function save(Livreur $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(Livreur $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    /**
     * Trouve tous les livreurs actifs
     */
    public function findAllActifs(): array
    {
        return $this->createQueryBuilder('l')
            ->where('l.actif = true')
            ->orderBy('l.nom', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Trouve les livreurs disponibles
     */
    public function findDisponibles(): array
    {
        return $this->createQueryBuilder('l')
            ->where('l.actif = true')
            ->andWhere('l.disponible = true')
            ->orderBy('l.nom', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Trouve les livreurs disponibles pour une zone spécifique
     */
    public function findDisponiblesByZone(int $zoneId): array
    {
        return $this->createQueryBuilder('l')
            ->innerJoin('l.zones', 'z')
            ->where('z.id = :zoneId')
            ->andWhere('l.actif = true')
            ->andWhere('l.disponible = true')
            ->setParameter('zoneId', $zoneId)
            ->orderBy('l.nom', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Livreurs avec leurs zones
     */
    public function findAllWithZones(): array
    {
        return $this->createQueryBuilder('l')
            ->leftJoin('l.zones', 'z')
            ->addSelect('z')
            ->where('l.actif = true')
            ->orderBy('l.nom', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Compte les livraisons en cours par livreur
     */
    public function countLivraisonsEnCours(int $livreurId): int
    {
        return (int) $this->getEntityManager()->createQuery('
            SELECT COUNT(c.id)
            FROM App\Entity\Commande c
            WHERE c.livreur = :livreurId
            AND c.statut = :statut
        ')
            ->setParameter('livreurId', $livreurId)
            ->setParameter('statut', 'EN_LIVRAISON')
            ->getSingleScalarResult();
    }
}
