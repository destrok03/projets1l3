<?php

namespace App\Entity;

use App\Repository\LivreurRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: LivreurRepository::class)]
#[ORM\Table(name: 'livreurs')]
class Livreur
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 100)]
    private ?string $nom = null;

    #[ORM\Column(length: 100)]
    private ?string $prenom = null;

    #[ORM\Column(length: 20, unique: true)]
    private ?string $telephone = null;

    #[ORM\Column]
    private bool $disponible = true;

    #[ORM\Column]
    private bool $actif = true;

    #[ORM\Column(name: 'created_at', type: Types::DATETIME_MUTABLE, nullable: true)]
    private ?\DateTimeInterface $createdAt = null;

    #[ORM\ManyToMany(targetEntity: Zone::class, inversedBy: 'livreurs')]
    #[ORM\JoinTable(name: 'livreur_zones')]
    private Collection $zones;

    #[ORM\OneToMany(mappedBy: 'livreur', targetEntity: Commande::class)]
    private Collection $commandes;

    public function __construct()
    {
        $this->zones = new ArrayCollection();
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

    public function getPrenom(): ?string
    {
        return $this->prenom;
    }

    public function setPrenom(string $prenom): static
    {
        $this->prenom = $prenom;
        return $this;
    }

    public function getNomComplet(): string
    {
        return $this->prenom . ' ' . $this->nom;
    }

    public function getTelephone(): ?string
    {
        return $this->telephone;
    }

    public function setTelephone(string $telephone): static
    {
        $this->telephone = $telephone;
        return $this;
    }

    public function getTelephoneFormate(): string
    {
        return '+221 ' . $this->telephone;
    }

    public function isDisponible(): bool
    {
        return $this->disponible;
    }

    public function setDisponible(bool $disponible): static
    {
        $this->disponible = $disponible;
        return $this;
    }

    public function isActif(): bool
    {
        return $this->actif;
    }

    public function setActif(bool $actif): static
    {
        $this->actif = $actif;
        return $this;
    }

    public function getCreatedAt(): ?\DateTimeInterface
    {
        return $this->createdAt;
    }

    /**
     * @return Collection<int, Zone>
     */
    public function getZones(): Collection
    {
        return $this->zones;
    }

    public function addZone(Zone $zone): static
    {
        if (!$this->zones->contains($zone)) {
            $this->zones->add($zone);
        }
        return $this;
    }

    public function removeZone(Zone $zone): static
    {
        $this->zones->removeElement($zone);
        return $this;
    }

    /**
     * @return Collection<int, Commande>
     */
    public function getCommandes(): Collection
    {
        return $this->commandes;
    }

    public function getZonesNoms(): string
    {
        return implode(', ', $this->zones->map(fn($z) => $z->getNom())->toArray());
    }

    public function getCommandesEnCours(): Collection
    {
        return $this->commandes->filter(fn($c) => $c->getStatut() === Commande::STATUT_EN_LIVRAISON);
    }

    public function __toString(): string
    {
        return $this->getNomComplet();
    }
}
