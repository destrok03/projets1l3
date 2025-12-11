package entity;

public class Complement extends Produit {
    private TypeComplement typeComplement;

    public Complement() {
        super();
        this.typeProduit = TypeProduit.COMPLEMENT;
    }

    public Complement(int id, String nom, double prix, TypeComplement typeComplement) {
        super(id, nom, prix, TypeProduit.COMPLEMENT);
        this.typeComplement = typeComplement;
    }

    public TypeComplement getTypeComplement() {
        return typeComplement;
    }

    public void setTypeComplement(TypeComplement typeComplement) {
        this.typeComplement = typeComplement;
    }

    @Override
    public String toString() {
        return id + " | " + nom + " | " + prix + " FCFA | " + typeComplement;
    }
}
