package gerenciamento.biblioteca.api.auth2.Controllers;

import gerenciamento.biblioteca.api.auth2.DTO.CreationTokenDTO;
import gerenciamento.biblioteca.api.auth2.DTO.TokenCreatedDTO;
import gerenciamento.biblioteca.api.auth2.DTO.UserInfoDTO;
import gerenciamento.biblioteca.api.auth2.Entities.Situacao;
import gerenciamento.biblioteca.api.auth2.Services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class TokenController {
    @Autowired
    private TokenService tokenService;

    @PostMapping("/create")
    public ResponseEntity<TokenCreatedDTO> createToken(@RequestBody CreationTokenDTO tokenDTO) {
        return ResponseEntity.ok(tokenService.criarToken(tokenDTO));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization")  String accessToken) {
        return ResponseEntity.ok(tokenService.validarToken(accessToken));
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateToken(@RequestBody TokenCreatedDTO tokenCreatedDTO) {
        return ResponseEntity.ok(tokenService.atualizarToken(tokenCreatedDTO));
    }

    @GetMapping("/situation/{idRequerinte}/{idAlvo}")
    public ResponseEntity<Situacao> getSituacao(@PathVariable int idRequerinte, @PathVariable int idAlvo) {
        if (idRequerinte <= 0 || idAlvo <= 0) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(tokenService.atualizarSituacao(idRequerinte, idAlvo));
    }

    @GetMapping("/info_user")
    public ResponseEntity<UserInfoDTO> getInfo(@RequestHeader("Authorization") String accessToken) {
        return ResponseEntity.ok(tokenService.getInfo(accessToken));
    }

    @PostMapping("/google")
    public ResponseEntity<TokenCreatedDTO> autenticarComGoogle(@RequestBody String idToken) {
        return ResponseEntity.ok(tokenService.autenticarComGoogle(idToken));
    }
}
