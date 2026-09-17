using Azure.Core;
using Microsoft.AspNetCore.Http;
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
                "DOWNLOAD_FILE ip={IP} user={User}",
                HttpContext.Connection.RemoteIpAddress,
                "felipe"
            );
            return Ok();
        }
        [HttpGet]
        [Route("teste")]
        public IActionResult Privileged()
        {
            Log.Warning(
                "PRIVILEGED_ACCESS ip={IP} user={User}",
                HttpContext.Connection.RemoteIpAddress,
                "felipe"
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
                "LOGIN_SUCCESS ip={IP} user={User}",
                HttpContext.Connection.RemoteIpAddress,
                request.Username
                );

                return Ok("Usuário logado");          
            }else
            {
                Log.Warning(
                "LOGIN_FAILED ip={IP} user={User}",
                HttpContext.Connection.RemoteIpAddress,
                request.Username
            ); 
                return Unauthorized("Senha errada");
            }
        }
    }
}
