using Microsoft.AspNetCore.Mvc;
using Serilog;

namespace API_CRUD_Certo.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class BonecoController : ControllerBase
    {
        [HttpGet]
        [Route("download")]
        public IActionResult DownloadFile([FromRoute] int id)
        {
            Log.Information(
                "{@Event}",
                CreateSecurityEvent(
                    eventType: "FILE_DOWNLOAD",
                    user: "felipe"
                )
            );

            return Ok();
        }

        [HttpGet]
        [Route("teste")]
        public IActionResult Privileged()
        {
            Log.Warning(
                "{@Event}",
                CreateSecurityEvent(
                    eventType: "ADMIN_ACCESS",
                    user: "felipe"
                )
            );

            return Ok();
        }

        [HttpPost]
        public IActionResult Login([FromBody] LoginRequest request)
        {
            if (request.Username == "felipe" &&
                request.Password == "1234")
            {
                Log.Information(
                    "{@Event}",
                    CreateSecurityEvent(
                        eventType: "LOGIN_SUCCESS",
                        user: request.Username
                    )
                );

                return Ok("Usuário logado");
            }

            Log.Warning(
                "{@Event}",
                CreateSecurityEvent(
                    eventType: "LOGIN_FAILED",
                    user: request.Username
                )
            );

            return Unauthorized("Senha errada");
        }

        private object CreateSecurityEvent(string eventType, string user)
        {
            return new
            {
                id = Guid.NewGuid(),
                timestamp = DateTime.UtcNow,
                event_type = eventType,
                source = "test-api",
                user = user,
                ip = HttpContext.Connection.RemoteIpAddress?.ToString(),
                asset = "test-api"
            };
        }
    }
}
