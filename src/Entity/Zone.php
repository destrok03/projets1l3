<?php

namespace App\Entity;

use App\Repository\ZoneRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: ZoneRepository::class)]
#[ORM\Table(name: 'zones')]
class Zone
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 100, unique: true)]
    private ?string $nom = null;

    #[ORM\Column(name: 'prix_livraison', type: Types::DECIMAL, precision: 10, scale: 2)]
    private ?string $prixLivraison = '0.00';

    #[ORM\Column(name: 'created_at', type: Types::DATETIME_MUTABLE, nullable: true)]
    private ?\DateTimeInterface $createdAt = null;

    #[ORM\OneToMany(mappedBy: 'zone', targetEntity: Quartier::class, cascade: ['persist'])]
    private Collection $quartiers;

    #[ORM\ManyToMany(targetEntity: Livreur::class, mappedBy: 'zones')]
    private Collection $livreurs;

    #[ORM\OneToMany(mappedBy: 'zone', targetEntity: Commande::class)]
    private Collection $commandes;

    public function __construct()
    {
        $this->quartiers = new ArrayCollection();
        $this->livreurs = new ArrayCollection();
        $this->commandes = new ArrayCollection();
        $this->createdAt = new \DateTime();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNom(): ?string
    {
        return $this->nom;
    }

    public function setNom(string $nom): static
    {
        $this->nom = $nom;
        return $this;
    }

    public function getPrixLivraison(): ?string
    {
        return $this->prixLivraison;
    }

    public function setPrixLivraison(string $prixLivraison): static
    {
        $this->prixLivraison = $prixLivraison;
        return $this;
    }

    public function getPrixLivraisonFloat(): float
    {
        return (float) $this->prixLivraison;
    }

    public function getCreatedAt(): ?\DateTimeInterface
    {
        return $this->createdAt;
    }

    /**
     * @return Collection<int, Quartier>
     */
    public function getQuartiers(): Collection
    {
        return $this->quartiers;
    }

    public function addQuartier(Quartier $quartier): static
    {
        if (!$this->quartiers->contains($quartier)) {
            $this->quartiers->add($quartier);
            $quartier->setZone($this);
        }
        return $this;
    }

    public function removeQuartier(Quartier $quartier): static
    {
        if ($this->quartiers->removeElement($quartier)) {
            if ($quartier->getZone() === $this) {
                $quartier->setZone(null);
            }
        }
        return $this;
    }

    /**
     * @return Collection<int, Livreur>
     */
    public function getLivreurs(): Collection
    {
        return $this->livreurs;
    }

    /**
     * @return Collection<int, Commande>
     */
    public function getCommandes(): Collection
    {
        return $this->commandes;
    }

    public function getQuartiersNoms(): string
    {
        return implode(', ', $this->quartiers->map(fn($q) => $q->getNom())->toArray());
    }

    public function getLivreursDisponibles(): Collection
    {
        return $this->livreurs->filter(fn($l) => $l->isDisponible() && $l->isActif());
    }

    public function __toString(): string
    {
        return $this->nom ?? '';
    }
}
