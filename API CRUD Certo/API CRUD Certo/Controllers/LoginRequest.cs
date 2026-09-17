using Microsoft.Identity.Client;

namespace API_CRUD_Certo.Controllers 
{
    public class LoginRequest
    {
        public string Username { get; set; } = string.Empty;

        public string Password { get; set; } = string.Empty;

    }
}