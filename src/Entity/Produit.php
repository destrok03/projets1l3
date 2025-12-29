<?php

namespace App\Entity;

use App\Repository\ProduitRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: ProduitRepository::class)]
#[ORM\Table(name: 'produits')]
#[ORM\InheritanceType('SINGLE_TABLE')]
#[ORM\DiscriminatorColumn(name: 'type_produit', type: 'string')]
#[ORM\DiscriminatorMap([
    'BURGER' => Burger::class,
    'MENU' => Menu::class,
    'COMPLEMENT' => Complement::class
])]
abstract class Produit
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    protected ?int $id = null;

    #[ORM\Column(length: 150)]
    protected ?string $nom = null;

    #[ORM\Column(type: Types::DECIMAL, precision: 10, scale: 2)]
    protected ?string $prix = '0.00';

    #[ORM\Column(length: 500, nullable: true)]
    protected ?string $image = null;

    #[ORM\Column]
    protected bool $archived = false;

    #[ORM\Column]
    protected bool $disponible = true;

    #[ORM\Column(name: 'created_at', type: Types::DATETIME_MUTABLE, nullable: true)]
    protected ?\DateTimeInterface $createdAt = null;

    #[ORM\Column(name: 'updated_at', type: Types::DATETIME_MUTABLE, nullable: true)]
    protected ?\DateTimeInterface $updatedAt = null;

    public function __construct()
    {
        $this->createdAt = new \DateTime();
        $this->updatedAt = new \DateTime();
    }

    abstract public function calculerPrix(): float;

    public function getTypeProduit(): string
    {
        return match(true) {
            $this instanceof Burger => 'BURGER',
            $this instanceof Menu => 'MENU',
            $this instanceof Complement => 'COMPLEMENT',
            default => 'UNKNOWN'
        };
    }

    public function getTypeProduitLabel(): string
    {
        return match(true) {
            $this instanceof Burger => 'Burger',
            $this instanceof Menu => 'Menu',
            $this instanceof Complement => 'Complément',
            default => 'Inconnu'
        };
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

    public function getPrix(): ?string
    {
        return $this->prix;
    }

    public function setPrix(string $prix): static
    {
        $this->prix = $prix;
        return $this;
    }

    public function getPrixFloat(): float
    {
        return (float) $this->prix;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(?string $image): static
    {
        $this->image = $image;
        return $this;
    }

    public function isArchived(): bool
    {
        return $this->archived;
    }

    public function setArchived(bool $archived): static
    {
        $this->archived = $archived;
        return $this;
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

    public function getCreatedAt(): ?\DateTimeInterface
    {
        return $this->createdAt;
    }

    public function setCreatedAt(?\DateTimeInterface $createdAt): static
    {
        $this->createdAt = $createdAt;
        return $this;
    }

    public function getUpdatedAt(): ?\DateTimeInterface
    {
        return $this->updatedAt;
    }

    public function setUpdatedAt(?\DateTimeInterface $updatedAt): static
    {
        $this->updatedAt = $updatedAt;
        return $this;
    }
}
