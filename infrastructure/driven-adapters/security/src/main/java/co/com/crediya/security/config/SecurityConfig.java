package co.com.crediya.security.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoanApplicationPath path;
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveJwtAuthenticationConverter jwtAuthConverter,
                                                         ReactiveJwtDecoder jwtDecoder) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange((authorize) -> authorize
                        .pathMatchers( "/doc/**",  "/v3/api-docs/**").permitAll()
                        .pathMatchers(path.getLoanApp() + "/**").hasAnyAuthority("USER")
                        .anyExchange().authenticated()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> {
                    jwt.jwtDecoder(jwtDecoder);
                    jwt.jwtAuthenticationConverter(jwtAuthConverter);
                }));
        return http.build();
    }

    @Bean
    SecretKey hmacKey(JwtProperties props) {
        byte[] keyBytes = Base64.getUrlDecoder().decode(props.secret());
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder(SecretKey hmacKey, JwtProperties props) {
        var decoder = NimbusReactiveJwtDecoder.withSecretKey(hmacKey).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(props.issuer()));
        return decoder;
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object raw = jwt.getClaims().get("roles");
            if (!(raw instanceof Iterable<?> it)) {
                return Flux.empty(); 
            }

            return Flux.fromIterable(it)
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .map(m -> String.valueOf(m.get("authority")))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(SimpleGrantedAuthority::new);

        });
        return converter;
    }
}
