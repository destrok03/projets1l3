<?php

namespace App\Repository;

use App\Entity\Produit;
use App\Entity\Burger;
use App\Entity\Commande;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Produit>
 */
class ProduitRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Produit::class);
    }

    /**
     * Top burgers vendus du jour
     */
    public function findTopBurgersToday(int $limit = 5): array
    {
        $today = new \DateTime('today');
        $tomorrow = new \DateTime('tomorrow');

        return $this->getEntityManager()->createQuery('
            SELECT p.id, p.nom, p.image, SUM(cl.quantite) as totalVentes
            FROM App\Entity\CommandeLigne cl
            JOIN cl.produit p
            JOIN cl.commande c
            WHERE p INSTANCE OF App\Entity\Burger
            AND c.dateCommande >= :today
            AND c.dateCommande < :tomorrow
            AND c.statut IN (:statuts)
            GROUP BY p.id, p.nom, p.image
            ORDER BY totalVentes DESC
        ')
            ->setParameter('today', $today)
            ->setParameter('tomorrow', $tomorrow)
            ->setParameter('statuts', [Commande::STATUT_TERMINEE, Commande::STATUT_VALIDEE, Commande::STATUT_EN_LIVRAISON])
            ->setMaxResults($limit)
            ->getResult();
    }

    /**
     * Produits les plus vendus (tous types)
     */
    public function findTopProduitsToday(int $limit = 10): array
    {
        $today = new \DateTime('today');
        $tomorrow = new \DateTime('tomorrow');

        return $this->getEntityManager()->createQuery('
            SELECT p.id, p.nom, p.image, SUM(cl.quantite) as totalVentes
            FROM App\Entity\CommandeLigne cl
            JOIN cl.produit p
            JOIN cl.commande c
            WHERE c.dateCommande >= :today
            AND c.dateCommande < :tomorrow
            AND c.statut IN (:statuts)
            GROUP BY p.id, p.nom, p.image
            ORDER BY totalVentes DESC
        ')
            ->setParameter('today', $today)
            ->setParameter('tomorrow', $tomorrow)
            ->setParameter('statuts', [Commande::STATUT_TERMINEE, Commande::STATUT_VALIDEE])
            ->setMaxResults($limit)
            ->getResult();
    }

    /**
     * Trouve tous les burgers disponibles
     */
    public function findAvailableBurgers(): array
    {
        return $this->createQueryBuilder('p')
            ->where('p INSTANCE OF App\Entity\Burger')
            ->andWhere('p.archived = false')
            ->andWhere('p.disponible = true')
            ->orderBy('p.nom', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Trouve tous les compléments disponibles par type
     */
    public function findAvailableComplements(?string $type = null): array
    {
        $qb = $this->createQueryBuilder('p')
            ->where('p INSTANCE OF App\Entity\Complement')
            ->andWhere('p.archived = false')
            ->andWhere('p.disponible = true');

        if ($type) {
            $qb->andWhere('p.typeComplement = :type')
                ->setParameter('type', $type);
        }

        return $qb->orderBy('p.nom', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Trouve tous les menus disponibles
     */
    public function findAvailableMenus(): array
    {
        return $this->createQueryBuilder('p')
            ->where('p INSTANCE OF App\Entity\Menu')
            ->andWhere('p.archived = false')
            ->andWhere('p.disponible = true')
            ->orderBy('p.nom', 'ASC')
            ->getQuery()
            ->getResult();
    }
}
