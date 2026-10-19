package pe.edu.upeu.jdrefrigeracion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class PerfilController {

    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> perfil(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("sub", jwt.getSubject());
        respuesta.put("email", jwt.getClaimAsString("email"));
        respuesta.put("preferred_username", jwt.getClaimAsString("preferred_username"));
        
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        Object roles = realmAccess != null ? realmAccess.get("roles") : List.of();
        respuesta.put("roles", roles);

        return ResponseEntity.ok(respuesta);
    }
}
