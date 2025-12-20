namespace BrasilBurger.ViewModels
{
    public class PaginationViewModel
    {
        public int NbrePage { get; set; }
        public int PageEncours { get; set; }
        public string ActionName { get; set; } = "Index";
        public string ControllerName { get; set; } = "Home";
        public Dictionary<string, string?> QueryParams { get; set; } = new Dictionary<string, string?>();
    }
}
