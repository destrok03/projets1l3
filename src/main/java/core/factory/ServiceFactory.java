package core.factory;

import repository.*;
import services.impl.*;

public class ServiceFactory {

    public static Object getInstance(EntityName entityName) {
        switch (entityName) {
            case ZONE:
                ZoneRepository zoneRepository = (ZoneRepository) RepositoryFactory.getInstance(EntityName.ZONE);
                return new ZoneServiceImpl(zoneRepository);
            case QUARTIER:
                QuartierRepository quartierRepository = (QuartierRepository) RepositoryFactory.getInstance(EntityName.QUARTIER);
                return new QuartierServiceImpl(quartierRepository);
            case LIVREUR:
                LivreurRepository livreurRepository = (LivreurRepository) RepositoryFactory.getInstance(EntityName.LIVREUR);
                return new LivreurServiceImpl(livreurRepository);
            case PRODUIT:
                ProduitRepository produitRepository = (ProduitRepository) RepositoryFactory.getInstance(EntityName.PRODUIT);
                return new ProduitServiceImpl(produitRepository);
            default:
                return null;
        }
    }
}
