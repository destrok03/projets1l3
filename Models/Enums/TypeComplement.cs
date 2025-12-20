using NpgsqlTypes;

namespace BrasilBurger.Models.Enums
{
    public enum TypeComplement
    {
        [PgName("BOISSON")]
        BOISSON,
        [PgName("FRITES")]
        FRITES,
        
    }
}
