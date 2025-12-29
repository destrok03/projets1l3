<?php

namespace App\Entity;

use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class Burger extends Produit
{
    #[ORM\Column(type: Types::TEXT, nullable: true)]
    private ?string $description = null;

    #[ORM\Column(type: Types::TEXT, nullable: true)]
    private ?string $ingredients = null;

    public function calculerPrix(): float
    {
        return (float) $this->prix;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(?string $description): static
    {
        $this->description = $description;
        return $this;
    }

    public function getIngredients(): ?string
    {
        return $this->ingredients;
    }

    public function setIngredients(?string $ingredients): static
    {
        $this->ingredients = $ingredients;
        return $this;
    }

    public function getIngredientsArray(): array
    {
        if (!$this->ingredients) {
            return [];
        }
        return array_map('trim', explode(',', $this->ingredients));
    }
}
