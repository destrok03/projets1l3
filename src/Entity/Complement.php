<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class Complement extends Produit
{
    public const TYPE_BOISSON = 'BOISSON';
    public const TYPE_FRITES = 'FRITES';
    public const TYPE_SAUCE = 'SAUCE';
    public const TYPE_DESSERT = 'DESSERT';

    #[ORM\Column(name: 'type_complement', length: 50, nullable: true)]
    private ?string $typeComplement = null;

    public function calculerPrix(): float
    {
        return (float) $this->prix;
    }

    public function getTypeComplement(): ?string
    {
        return $this->typeComplement;
    }

    public function setTypeComplement(?string $typeComplement): static
    {
        $this->typeComplement = $typeComplement;
        return $this;
    }

    public function getTypeComplementLabel(): string
    {
        return match($this->typeComplement) {
            self::TYPE_BOISSON => 'Boisson',
            self::TYPE_FRITES => 'Frites',
            self::TYPE_SAUCE => 'Sauce',
            self::TYPE_DESSERT => 'Dessert',
            default => $this->typeComplement ?? 'Autre'
        };
    }

    public static function getTypesComplement(): array
    {
        return [
            'Boisson' => self::TYPE_BOISSON,
            'Frites' => self::TYPE_FRITES,
            'Sauce' => self::TYPE_SAUCE,
            'Dessert' => self::TYPE_DESSERT,
        ];
    }
}
