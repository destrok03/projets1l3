<?php

namespace App\Repository;

use App\Entity\Client;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Client>
 */
class ClientRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Client::class);
    }

    /**
     * Recherche de clients par nom, prénom ou téléphone
     */
    public function search(string $term): array
    {
        return $this->createQueryBuilder('c')
            ->where('c.nom LIKE :term')
            ->orWhere('c.prenom LIKE :term')
            ->orWhere('c.telephone LIKE :term')
            ->setParameter('term', '%' . $term . '%')
            ->orderBy('c.nom', 'ASC')
            ->setMaxResults(20)
            ->getQuery()
            ->getResult();
    }

    /**
     * Compte le nombre total de clients actifs
     */
    public function countActifs(): int
    {
        return (int) $this->createQueryBuilder('c')
            ->select('COUNT(c.id)')
            ->where('c.actif = true')
            ->getQuery()
            ->getSingleScalarResult();
    }
}
