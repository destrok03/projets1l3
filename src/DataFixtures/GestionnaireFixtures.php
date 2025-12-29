<?php

namespace App\DataFixtures;

use App\Entity\Gestionnaire;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Persistence\ObjectManager;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;

class GestionnaireFixtures extends Fixture
{
    public function __construct(private UserPasswordHasherInterface $passwordHasher)
    {
    }

    public function load(ObjectManager $manager): void
    {
        $gestionnaire = new Gestionnaire();
        $gestionnaire->setNomComplet('Admin Brasil');
        $gestionnaire->setEmail('admin@brasil.com');
        $gestionnaire->setTelephone('771234567');
        $gestionnaire->setRoles(['ROLE_ADMIN', 'ROLE_GESTIONNAIRE']);
        $gestionnaire->setActif(true);
        
        $hashedPassword = $this->passwordHasher->hashPassword($gestionnaire, 'admin123');
        $gestionnaire->setPassword($hashedPassword);

        $manager->persist($gestionnaire);
        $manager->flush();
    }
}
