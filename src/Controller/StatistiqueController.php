<?php

namespace App\Controller;

use App\Service\StatistiqueService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/statistiques')]
class StatistiqueController extends AbstractController
{
    #[Route('', name: 'app_statistiques')]
    public function index(StatistiqueService $statistiqueService): Response
    {
        $stats = $statistiqueService->getStatistiquesDetaillees();

        return $this->render('statistique/index.html.twig', [
            'stats' => $stats
        ]);
    }
}
