using NpgsqlTypes;

namespace BrasilBurger.Models.Enums
{
    public enum ModePaiement
    {
        [PgName("WAVE")]
        WAVE,
        [PgName("ORANGE_MONEY")]
        ORANGE_MONEY,
        [PgName("ESPECES")]
        ESPECES
    }
}
