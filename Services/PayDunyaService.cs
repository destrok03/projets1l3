using Microsoft.Extensions.Options;
using System.Net.Http.Headers;
using System.Text.Json;

public class PayDunyaService
{
    private readonly PayDunyaOptions _options;
    private readonly HttpClient _httpClient;

    public PayDunyaService(IOptions<PayDunyaOptions> options, HttpClient httpClient)
    {
        _options = options.Value;
        _httpClient = httpClient;
    }

    public async Task<string> CreateInvoice(decimal amount, string callbackUrl)
    {
        var payload = new
        {
            total_amount = amount,
            currency = "XOF",
            items = new[]
            {
                new { name = "Commande", quantity = 1, price = amount }
            },
            callback_url = callbackUrl,
            cancel_url = callbackUrl,
            return_url = callbackUrl
        };

        var request = new HttpRequestMessage(HttpMethod.Post,
            "https://app.paydunya.com/sandbox/checkout-invoice/create")
        {
            Content = new StringContent(JsonSerializer.Serialize(payload), System.Text.Encoding.UTF8, "application/json")
        };

        // Authentification avec Token
        request.Headers.Authorization = new AuthenticationHeaderValue("Token", _options.Token);

        var response = await _httpClient.SendAsync(request);
        response.EnsureSuccessStatusCode();

        var json = await response.Content.ReadAsStringAsync();
        return json; // contient le lien pour payer
    }
}