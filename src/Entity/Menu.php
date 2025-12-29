<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class Menu extends Produit
{
    #[ORM\ManyToOne(targetEntity: Burger::class)]
    #[ORM\JoinColumn(name: 'burger_id', referencedColumnName: 'id', nullable: true)]
    private ?Burger $burger = null;

    #[ORM\ManyToOne(targetEntity: Complement::class)]
    #[ORM\JoinColumn(name: 'boisson_id', referencedColumnName: 'id', nullable: true)]
    private ?Complement $boisson = null;

    #[ORM\ManyToOne(targetEntity: Complement::class)]
    #[ORM\JoinColumn(name: 'frites_id', referencedColumnName: 'id', nullable: true)]
    private ?Complement $frites = null;

    public function calculerPrix(): float
    {
        $total = 0.0;
        
        if ($this->burger) {
            $total += (float) $this->burger->getPrix();
        }
        if ($this->boisson) {
            $total += (float) $this->boisson->getPrix();
        }
        if ($this->frites) {
            $total += (float) $this->frites->getPrix();
        }
        
        return $total;
    }

    public function getBurger(): ?Burger
    {
        return $this->burger;
    }

    public function setBurger(?Burger $burger): static
    {
        $this->burger = $burger;
        return $this;
    }

    public function getBoisson(): ?Complement
    {
        return $this->boisson;
    }

    public function setBoisson(?Complement $boisson): static
    {
        $this->boisson = $boisson;
        return $this;
    }

    public function getFrites(): ?Complement
    {
        return $this->frites;
    }

    public function setFrites(?Complement $frites): static
    {
        $this->frites = $frites;
        return $this;
    }

    public function getComposition(): string
    {
        $parts = [];
        if ($this->burger) {
            $parts[] = $this->burger->getNom();
        }
        if ($this->boisson) {
            $parts[] = $this->boisson->getNom();
        }
        if ($this->frites) {
            $parts[] = $this->frites->getNom();
        }
        return implode(' + ', $parts);
    }
}
