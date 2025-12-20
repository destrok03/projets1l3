using System.Collections.Generic;
using BrasilBurger.Models.DTO;

namespace BrasilBurger.ViewModels
{
	public class ProduitDetailViewModel
	{
		public ProduitListDto Produit { get; set; } = new ProduitListDto();
		public List<ProduitListDto> Frites { get; set; } = new List<ProduitListDto>();
		public List<ProduitListDto> Boissons { get; set; } = new List<ProduitListDto>();
		public List<ProduitListDto> ProduitsSimilaires { get; set; } = new List<ProduitListDto>();
		public int Quantite { get; set; } = 1;
		public decimal Total { get; set; } = 0;
		public List<ProduitListDto> MenuComposants { get; set; } = new List<ProduitListDto>();
	}
}
