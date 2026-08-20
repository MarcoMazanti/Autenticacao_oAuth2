package gerenciamento.biblioteca.api.auth2.Services;

import gerenciamento.biblioteca.api.auth2.DTO.CreationTokenDTO;
import gerenciamento.biblioteca.api.auth2.DTO.TokenCreatedDTO;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import static gerenciamento.biblioteca.api.auth2.Application.SECRET_KEY;

public class ManagementJWT {
    private long EXPIRATION_TIME_MILLIS = 1000 * 60 * 30; // 30 minutos
    private SecretKey secretKey = null;

    public ManagementJWT(long EXPIRATION_TIME_MILLIS) {
        Dotenv dotenv = Dotenv.load();
        this.EXPIRATION_TIME_MILLIS = EXPIRATION_TIME_MILLIS;
        this.secretKey = Keys.hmacShaKeyFor(dotenv.get("SECRET_KEY").getBytes(StandardCharsets.UTF_8));
    }

    public ManagementJWT() {
        this.EXPIRATION_TIME_MILLIS = 1000 * 60 * 30;
    }

    public TokenCreatedDTO criarTokens(CreationTokenDTO tokenDTO) {
        return new TokenCreatedDTO(criarAccessToken(tokenDTO), criarRefreshToken());
    }

    // Possível alteração futura, haverá a possibilidade de o refresh token ser renovado ou não dependendo do seu vencimento
    public TokenCreatedDTO atualizarTokens(CreationTokenDTO tokenDTO) {
        return new TokenCreatedDTO(criarAccessToken(tokenDTO), criarRefreshToken());
    }

    public String criarAccessToken(CreationTokenDTO tokenDTO) {
        SecretKey key = (secretKey == null) ? SECRET_KEY : secretKey;

        long nowMillis = System.currentTimeMillis();
        Date dataCriacao = new Date(nowMillis);
        Date dataExpiracao = new Date(nowMillis + EXPIRATION_TIME_MILLIS);

        return Jwts.builder()
                .setSubject(String.valueOf(tokenDTO.id()))
                .claim("name", tokenDTO.name())
                .claim("email", tokenDTO.email())
                .claim("roles", tokenDTO.roles())
                .setIssuedAt(dataCriacao)
                .setExpiration(dataExpiracao)
                .signWith(key, SignatureAlgorithm.HS256)
                .setIssuer("auth-service")
                .compact();
    }

    public String criarRefreshToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
