using BrasilBurger.Data;
using BrasilBurger.Models.Enums;
using BrasilBurger.Services;
using BrasilBurger.Services.Impl;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);


// ========== CONFIGURATION NPGSQL POUR LES ENUMS ==========
var dataSourceBuilder = new Npgsql.NpgsqlDataSourceBuilder(
    builder.Configuration.GetConnectionString("DefaultConnection"));
dataSourceBuilder.MapEnum<TypeProduit>("type_produit_enum");
dataSourceBuilder.MapEnum<TypeComplement>("type_complement_enum");
dataSourceBuilder.MapEnum<StatutCommande>("statut_commande_enum");
dataSourceBuilder.MapEnum<TypeLivraison>("type_livraison_enum");
dataSourceBuilder.MapEnum<ModePaiement>("mode_paiement_enum");
var dataSource = dataSourceBuilder.Build();

// ========== DB CONTEXT ==========
builder.Services.AddDbContext<BrasilBurgerDbContext>(options =>
    options.UseNpgsql(dataSource, o => o.MapEnum<TypeProduit>("type_produit_enum")
                                       .MapEnum<TypeComplement>("type_complement_enum")
                                       .MapEnum<StatutCommande>("statut_commande_enum")
                                       .MapEnum<TypeLivraison>("type_livraison_enum")
                                       .MapEnum<ModePaiement>("mode_paiement_enum")));

// ========== SERVICES (DI) ==========
builder.Services.AddScoped<IProduitService, ProduitService>();
builder.Services.AddScoped<IClientService, ClientService>();
builder.Services.AddScoped<ICommandeService, CommandeService>();
builder.Services.AddScoped<IPaiementService, PaiementService>();
builder.Services.AddScoped<IZoneService, ZoneService>();
builder.Services.AddScoped<IPanierService, PanierService>();

// ========== SESSION ==========
builder.Services.AddDistributedMemoryCache();
builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromMinutes(30);
    options.Cookie.HttpOnly = true;
    options.Cookie.IsEssential = true;
});

// ========== HTTP CONTEXT ACCESSOR ==========
builder.Services.AddHttpContextAccessor();

// ========== MVC ==========
builder.Services.AddControllersWithViews();
builder.Services.AddHttpClient<PayDunyaService>();

var app = builder.Build();

// ========== PIPELINE HTTP ==========
if (app.Environment.IsDevelopment())
{
    app.UseDeveloperExceptionPage(); // 🔴 ESSENTIEL
}
else
{
    app.UseExceptionHandler("/Home/Error");
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseStaticFiles();

app.UseRouting();

// Session doit être avant Authorization
app.UseSession();

app.UseAuthorization();

// Route par défaut
app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Home}/{action=Index}/{id?}");

app.Run();
