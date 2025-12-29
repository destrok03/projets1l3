<?php

namespace App\Controller;

use App\Entity\Gestionnaire;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/parametres')]
class ParametresController extends AbstractController
{
    #[Route('', name: 'app_parametres')]
    public function index(): Response
    {
        return $this->render('parametres/index.html.twig');
    }

    #[Route('/profil', name: 'app_parametres_profil', methods: ['POST'])]
    public function updateProfil(Request $request, EntityManagerInterface $em): Response
    {
        /** @var Gestionnaire $user */
        $user = $this->getUser();
        
        $nomComplet = $request->request->get('nom_complet');
        $email = $request->request->get('email');
        $telephone = $request->request->get('telephone');

        if ($nomComplet) {
            $user->setNomComplet($nomComplet);
        }
        if ($email) {
            $user->setEmail($email);
        }
        if ($telephone !== null) {
            $user->setTelephone($telephone);
        }

        $user->setUpdatedAt(new \DateTime());
        $em->flush();

        $this->addFlash('success', 'Profil mis à jour avec succès.');

        return $this->redirectToRoute('app_parametres');
    }

    #[Route('/password', name: 'app_parametres_password', methods: ['POST'])]
    public function updatePassword(
        Request $request, 
        EntityManagerInterface $em,
        UserPasswordHasherInterface $passwordHasher
    ): Response
    {
        /** @var Gestionnaire $user */
        $user = $this->getUser();
        
        $currentPassword = $request->request->get('current_password');
        $newPassword = $request->request->get('new_password');
        $confirmPassword = $request->request->get('confirm_password');

        // Vérification du mot de passe actuel
        if (!$passwordHasher->isPasswordValid($user, $currentPassword)) {
            $this->addFlash('error', 'Le mot de passe actuel est incorrect.');
            return $this->redirectToRoute('app_parametres');
        }

        // Vérification de la confirmation
        if ($newPassword !== $confirmPassword) {
            $this->addFlash('error', 'Les mots de passe ne correspondent pas.');
            return $this->redirectToRoute('app_parametres');
        }

        // Validation longueur minimum
        if (strlen($newPassword) < 6) {
            $this->addFlash('error', 'Le mot de passe doit contenir au moins 6 caractères.');
            return $this->redirectToRoute('app_parametres');
        }

        // Mise à jour du mot de passe
        $hashedPassword = $passwordHasher->hashPassword($user, $newPassword);
        $user->setPassword($hashedPassword);
        $user->setUpdatedAt(new \DateTime());
        
        $em->flush();

        $this->addFlash('success', 'Mot de passe mis à jour avec succès.');

        return $this->redirectToRoute('app_parametres');
    }
}
