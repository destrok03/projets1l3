using NpgsqlTypes;

namespace BrasilBurger.Models.Enums
{
    public enum StatutCommande
    {
        [PgName("EN_ATTENTE")]
        EN_ATTENTE,
        [PgName("VALIDEE")]
        VALIDEE,
        [PgName("EN_PREPARATION")]
        EN_PREPARATION,
        [PgName("PRETE")]
        PRETE,
        [PgName("EN_LIVRAISON")]
        EN_LIVRAISON,
        [PgName("TERMINEE")]
        TERMINEE,
        [PgName("ANNULEE")]
        ANNULEE
    }
}
