<?php

namespace App\Entity;

use App\Repository\PaiementRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: PaiementRepository::class)]
#[ORM\Table(name: 'paiements')]
class Paiement
{
    public const MODE_WAVE = 'WAVE';
    public const MODE_ORANGE_MONEY = 'ORANGE_MONEY';
    public const MODE_ESPECES = 'ESPECES';

    public const STATUT_VALIDE = 'VALIDE';
    public const STATUT_EN_ATTENTE = 'EN_ATTENTE';
    public const STATUT_ECHOUE = 'ECHOUE';

    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\OneToOne(inversedBy: 'paiement', targetEntity: Commande::class)]
    #[ORM\JoinColumn(name: 'commande_id', nullable: false, unique: true)]
    private ?Commande $commande = null;

    #[ORM\Column(name: 'date_paiement', type: Types::DATETIME_MUTABLE)]
    private ?\DateTimeInterface $datePaiement = null;

    #[ORM\Column(type: Types::DECIMAL, precision: 10, scale: 2)]
    private ?string $montant = '0.00';

    #[ORM\Column(name: 'mode_paiement', length: 50)]
    private ?string $modePaiement = null;

    #[ORM\Column(name: 'reference_transaction', length: 100, nullable: true, unique: true)]
    private ?string $referenceTransaction = null;

    #[ORM\Column(length: 50)]
    private string $statut = self::STATUT_VALIDE;

    #[ORM\Column(name: 'created_at', type: Types::DATETIME_MUTABLE, nullable: true)]
    private ?\DateTimeInterface $createdAt = null;

    public function __construct()
    {
        $this->datePaiement = new \DateTime();
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

    public function getDatePaiement(): ?\DateTimeInterface
    {
        return $this->datePaiement;
    }

    public function setDatePaiement(\DateTimeInterface $datePaiement): static
    {
        $this->datePaiement = $datePaiement;
        return $this;
    }

    public function getMontant(): ?string
    {
        return $this->montant;
    }

    public function setMontant(string $montant): static
    {
        $this->montant = $montant;
        return $this;
    }

    public function getMontantFloat(): float
    {
        return (float) $this->montant;
    }

    public function getModePaiement(): ?string
    {
        return $this->modePaiement;
    }

    public function setModePaiement(string $modePaiement): static
    {
        $this->modePaiement = $modePaiement;
        return $this;
    }

    public function getModePaiementLabel(): string
    {
        return match($this->modePaiement) {
            self::MODE_WAVE => 'Wave',
            self::MODE_ORANGE_MONEY => 'Orange Money',
            self::MODE_ESPECES => 'Espèces',
            default => $this->modePaiement ?? ''
        };
    }

    public function getReferenceTransaction(): ?string
    {
        return $this->referenceTransaction;
    }

    public function setReferenceTransaction(?string $referenceTransaction): static
    {
        $this->referenceTransaction = $referenceTransaction;
        return $this;
    }

    public function getStatut(): string
    {
        return $this->statut;
    }

    public function setStatut(string $statut): static
    {
        $this->statut = $statut;
        return $this;
    }

    public function getCreatedAt(): ?\DateTimeInterface
    {
        return $this->createdAt;
    }

    public function isValide(): bool
    {
        return $this->statut === self::STATUT_VALIDE;
    }

    public static function getModesPaiement(): array
    {
        return [
            'Wave' => self::MODE_WAVE,
            'Orange Money' => self::MODE_ORANGE_MONEY,
            'Espèces' => self::MODE_ESPECES,
        ];
    }
}
