using NpgsqlTypes;

namespace BrasilBurger.Models.Enums
{
    public enum TypeLivraison
    {
        [PgName("SUR_PLACE")]
        SUR_PLACE,
        [PgName("A_EMPORTER")]
        A_EMPORTER,
        [PgName("LIVRAISON")]
        LIVRAISON
    }
}
