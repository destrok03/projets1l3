<?php

namespace App\Controller;

use App\Service\StatistiqueService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class DashboardController extends AbstractController
{
    #[Route('/', name: 'app_dashboard')]
    public function index(StatistiqueService $statistiqueService): Response
    {
        $stats = $statistiqueService->getStatistiquesJournalieres();

        return $this->render('dashboard/index.html.twig', [
            'stats' => $stats
        ]);
    }
}
