package core.factory;

import repository.db.*;
import repository.list.*;

public class RepositoryFactory {
    private static Persistence persistence = Persistence.DATABASE;

    public static void setPersistence(Persistence p) {
        persistence = p;
    }

    public static Object getInstance(EntityName entityName) {
        switch (persistence) {
            case LIST:
                return getInstanceList(entityName);
            case DATABASE:
                return getInstanceDb(entityName);
            default:
                return null;
        }
    }

    private static Object getInstanceList(EntityName entityName) {
        switch (entityName) {
            case ZONE:
                return new ZoneRepositoryListImpl();
            case QUARTIER:
                return new QuartierRepositoryListImpl();
            case LIVREUR:
                return new LivreurRepositoryListImpl();
            case PRODUIT:
                return new ProduitRepositoryListImpl();
            default:
                return null;
        }
    }

    private static Object getInstanceDb(EntityName entityName) {
        switch (entityName) {
            case ZONE:
                return new ZoneRepositoryDbImpl();
            case QUARTIER:
                return new QuartierRepositoryDbImpl();
            case LIVREUR:
                return new LivreurRepositoryDbImpl();
            case PRODUIT:
                return new ProduitRepositoryDbImpl();
            default:
                return null;
        }
    }
}
