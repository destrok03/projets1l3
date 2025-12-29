<?php

namespace App\Entity;

use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
#[ORM\Table(name: 'commande_lignes')]
class CommandeLigne
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\ManyToOne(targetEntity: Commande::class, inversedBy: 'lignes')]
    #[ORM\JoinColumn(name: 'commande_id', nullable: false)]
    private ?Commande $commande = null;

    #[ORM\ManyToOne(targetEntity: Produit::class)]
    #[ORM\JoinColumn(name: 'produit_id', nullable: false)]
    private ?Produit $produit = null;

    #[ORM\Column]
    private int $quantite = 1;

    #[ORM\Column(name: 'prix_unitaire', type: Types::DECIMAL, precision: 10, scale: 2)]
    private ?string $prixUnitaire = '0.00';

    #[ORM\Column(name: 'sous_total', type: Types::DECIMAL, precision: 10, scale: 2)]
    private ?string $sousTotal = '0.00';

    #[ORM\Column(name: 'created_at', type: Types::DATETIME_MUTABLE, nullable: true)]
    private ?\DateTimeInterface $createdAt = null;

    public function __construct()
    {
        $this->createdAt = new \DateTime();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getCommande(): ?Commande
    {
        return $this->commande;
    }

    public function setCommande(?Commande $commande): static
    {
        $this->commande = $commande;
        return $this;
    }

    public function getProduit(): ?Produit
    {
        return $this->produit;
    }

    public function setProduit(?Produit $produit): static
    {
        $this->produit = $produit;
        return $this;
    }

    public function getQuantite(): int
    {
        return $this->quantite;
    }

    public function setQuantite(int $quantite): static
    {
        $this->quantite = $quantite;
        return $this;
    }

    public function getPrixUnitaire(): ?string
    {
        return $this->prixUnitaire;
    }

    public function setPrixUnitaire(string $prixUnitaire): static
    {
        $this->prixUnitaire = $prixUnitaire;
        return $this;
    }

    public function getPrixUnitaireFloat(): float
    {
        return (float) $this->prixUnitaire;
    }

    public function getSousTotal(): ?string
    {
        return $this->sousTotal;
    }

    public function setSousTotal(string $sousTotal): static
    {
        $this->sousTotal = $sousTotal;
        return $this;
    }

    public function getSousTotalFloat(): float
    {
        return (float) $this->sousTotal;
    }

    public function getCreatedAt(): ?\DateTimeInterface
    {
        return $this->createdAt;
    }

    public function calculerSousTotal(): float
    {
        return (float) $this->prixUnitaire * $this->quantite;
    }
}
