using NpgsqlTypes;

namespace BrasilBurger.Models.Enums
{
    public enum TypeProduit
    {
        [PgName("BURGER")]
        BURGER,
        [PgName("COMPLEMENT")]
        COMPLEMENT,
        [PgName("MENU")]
        MENU
    }
}
