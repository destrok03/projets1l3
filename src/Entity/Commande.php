<?php

namespace App\Entity;

use App\Repository\CommandeRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: CommandeRepository::class)]
#[ORM\Table(name: 'commandes')]
class Commande
{
    // Statuts possibles
    public const STATUT_EN_ATTENTE = 'EN_ATTENTE';
    public const STATUT_VALIDEE = 'VALIDEE';
    public const STATUT_EN_PREPARATION = 'EN_PREPARATION';
    public const STATUT_PRETE = 'PRETE';
    public const STATUT_EN_LIVRAISON = 'EN_LIVRAISON';
    public const STATUT_TERMINEE = 'TERMINEE';
    public const STATUT_ANNULEE = 'ANNULEE';

    // Types de livraison
    public const TYPE_SUR_PLACE = 'SUR_PLACE';
    public const TYPE_A_EMPORTER = 'A_EMPORTER';
    public const TYPE_LIVRAISON = 'LIVRAISON';

    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\ManyToOne(targetEntity: Client::class, inversedBy: 'commandes')]
    #[ORM\JoinColumn(name: 'client_id', nullable: false)]
    private ?Client $client = null;

    #[ORM\Column(name: 'date_commande', type: Types::DATETIME_MUTABLE)]
    private ?\DateTimeInterface $dateCommande = null;

    #[ORM\Column(length: 50)]
    private ?string $statut = self::STATUT_EN_ATTENTE;

    #[ORM\Column(name: 'type_livraison', length: 50)]
    private ?string $typeLivraison = null;

    #[ORM\ManyToOne(targetEntity: Quartier::class)]
    #[ORM\JoinColumn(name: 'quartier_id', nullable: true)]
    private ?Quartier $quartier = null;

    #[ORM\ManyToOne(targetEntity: Zone::class, inversedBy: 'commandes')]
    #[ORM\JoinColumn(name: 'zone_id', nullable: true)]
    private ?Zone $zone = null;

    #[ORM\ManyToOne(targetEntity: Livreur::class, inversedBy: 'commandes')]
    #[ORM\JoinColumn(name: 'livreur_id', nullable: true)]
    private ?Livreur $livreur = null;

    #[ORM\Column(name: 'montant_total', type: Types::DECIMAL, precision: 10, scale: 2)]
    private ?string $montantTotal = '0.00';

    #[ORM\Column(name: 'frais_livraison', type: Types::DECIMAL, precision: 10, scale: 2)]
    private ?string $fraisLivraison = '0.00';

    #[ORM\Column(type: Types::TEXT, nullable: true)]
    private ?string $notes = null;

    #[ORM\Column(name: 'created_at', type: Types::DATETIME_MUTABLE, nullable: true)]
    private ?\DateTimeInterface $createdAt = null;

    #[ORM\Column(name: 'updated_at', type: Types::DATETIME_MUTABLE, nullable: true)]
    private ?\DateTimeInterface $updatedAt = null;

    #[ORM\OneToMany(mappedBy: 'commande', targetEntity: CommandeLigne::class, cascade: ['persist', 'remove'])]
    private Collection $lignes;

    #[ORM\OneToOne(mappedBy: 'commande', targetEntity: Paiement::class, cascade: ['persist'])]
    private ?Paiement $paiement = null;

    public function __construct()
    {
        $this->lignes = new ArrayCollection();
        $this->dateCommande = new \DateTime();
        $this->createdAt = new \DateTime();
        $this->updatedAt = new \DateTime();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNumero(): string
    {
        return 'CMD-' . str_pad($this->id, 3, '0', STR_PAD_LEFT);
    }

    public function getClient(): ?Client
    {
        return $this->client;
    }

    public function setClient(?Client $client): static
    {
        $this->client = $client;
        return $this;
    }

    public function getDateCommande(): ?\DateTimeInterface
    {
        return $this->dateCommande;
    }

    public function setDateCommande(\DateTimeInterface $dateCommande): static
    {
        $this->dateCommande = $dateCommande;
        return $this;
    }

    public function getStatut(): ?string
    {
        return $this->statut;
    }

    public function setStatut(string $statut): static
    {
        $this->statut = $statut;
        $this->updatedAt = new \DateTime();
        return $this;
    }

    public function getTypeLivraison(): ?string
    {
        return $this->typeLivraison;
    }

    public function setTypeLivraison(string $typeLivraison): static
    {
        $this->typeLivraison = $typeLivraison;
        return $this;
    }

    public function getQuartier(): ?Quartier
    {
        return $this->quartier;
    }

    public function setQuartier(?Quartier $quartier): static
    {
        $this->quartier = $quartier;
        return $this;
    }

    public function getZone(): ?Zone
    {
        return $this->zone;
    }

    public function setZone(?Zone $zone): static
    {
        $this->zone = $zone;
        return $this;
    }

    public function getLivreur(): ?Livreur
    {
        return $this->livreur;
    }

    public function setLivreur(?Livreur $livreur): static
    {
        $this->livreur = $livreur;
        return $this;
    }

    public function getMontantTotal(): ?string
    {
        return $this->montantTotal;
    }

    public function setMontantTotal(string $montantTotal): static
    {
        $this->montantTotal = $montantTotal;
        return $this;
    }

    public function getMontantTotalFloat(): float
    {
        return (float) $this->montantTotal;
    }

    public function getFraisLivraison(): ?string
    {
        return $this->fraisLivraison;
    }

    public function setFraisLivraison(string $fraisLivraison): static
    {
        $this->fraisLivraison = $fraisLivraison;
        return $this;
    }

    public function getNotes(): ?string
    {
        return $this->notes;
    }

    public function setNotes(?string $notes): static
    {
        $this->notes = $notes;
        return $this;
    }

    public function getCreatedAt(): ?\DateTimeInterface
    {
        return $this->createdAt;
    }

    public function getUpdatedAt(): ?\DateTimeInterface
    {
        return $this->updatedAt;
    }

    /**
     * @return Collection<int, CommandeLigne>
     */
    public function getLignes(): Collection
    {
        return $this->lignes;
    }

    public function addLigne(CommandeLigne $ligne): static
    {
        if (!$this->lignes->contains($ligne)) {
            $this->lignes->add($ligne);
            $ligne->setCommande($this);
        }
        return $this;
    }

    public function removeLigne(CommandeLigne $ligne): static
    {
        if ($this->lignes->removeElement($ligne)) {
            if ($ligne->getCommande() === $this) {
                $ligne->setCommande(null);
            }
        }
        return $this;
    }

    public function getPaiement(): ?Paiement
    {
        return $this->paiement;
    }

    public function setPaiement(?Paiement $paiement): static
    {
        if ($paiement !== null && $paiement->getCommande() !== $this) {
            $paiement->setCommande($this);
        }
        $this->paiement = $paiement;
        return $this;
    }

    public function isPaye(): bool
    {
        return $this->paiement !== null;
    }

    public function getStatutLabel(): string
    {
        return match($this->statut) {
            self::STATUT_EN_ATTENTE => 'En attente',
            self::STATUT_VALIDEE => 'Validée',
            self::STATUT_EN_PREPARATION => 'En préparation',
            self::STATUT_PRETE => 'Prête',
            self::STATUT_EN_LIVRAISON => 'En livraison',
            self::STATUT_TERMINEE => 'Terminée',
            self::STATUT_ANNULEE => 'Annulée',
            default => $this->statut
        };
    }

    public function getStatutBadgeClass(): string
    {
        // Return semantic classes used by the frontend `.badge-status` styles
        return match($this->statut) {
            self::STATUT_EN_ATTENTE => 'muted',
            self::STATUT_VALIDEE => 'success',
            self::STATUT_EN_PREPARATION => 'info',
            self::STATUT_PRETE => 'primary',
            self::STATUT_EN_LIVRAISON => 'info',
            self::STATUT_TERMINEE => 'success',
            self::STATUT_ANNULEE => 'danger',
            default => 'muted'
        };
    }

    public function getTypeLivraisonLabel(): string
    {
        return match($this->typeLivraison) {
            self::TYPE_SUR_PLACE => 'Sur place',
            self::TYPE_A_EMPORTER => 'À emporter',
            self::TYPE_LIVRAISON => 'Livraison',
            default => $this->typeLivraison ?? ''
        };
    }

    public function getTypeLivraisonBadgeClass(): string
    {
        return match($this->typeLivraison) {
            self::TYPE_SUR_PLACE => 'bg-success',
            self::TYPE_A_EMPORTER => 'bg-warning text-dark',
            self::TYPE_LIVRAISON => 'bg-info',
            default => 'bg-secondary'
        };
    }

    public static function getStatuts(): array
    {
        return [
            'En attente' => self::STATUT_EN_ATTENTE,
            'Validée' => self::STATUT_VALIDEE,
            'En préparation' => self::STATUT_EN_PREPARATION,
            'Prête' => self::STATUT_PRETE,
            'En livraison' => self::STATUT_EN_LIVRAISON,
            'Terminée' => self::STATUT_TERMINEE,
            'Annulée' => self::STATUT_ANNULEE,
        ];
    }

    public static function getTypesLivraison(): array
    {
        return [
            'Sur place' => self::TYPE_SUR_PLACE,
            'À emporter' => self::TYPE_A_EMPORTER,
            'Livraison' => self::TYPE_LIVRAISON,
        ];
    }

    public function canBeModified(): bool
    {
        return !in_array($this->statut, [self::STATUT_TERMINEE, self::STATUT_ANNULEE]);
    }

    public function canBeCancelled(): bool
    {
        return !in_array($this->statut, [self::STATUT_TERMINEE, self::STATUT_ANNULEE, self::STATUT_EN_LIVRAISON]);
    }

    public function needsDelivery(): bool
    {
        return $this->typeLivraison === self::TYPE_LIVRAISON;
    }
}
