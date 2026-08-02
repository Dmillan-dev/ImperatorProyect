package imperator.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JwtResourceServerConfiguration {
    private static final String REQUIRED_AUDIENCE = "imperator-api";
    private static final String REQUIRED_ALGORITHM = "RS256";

    @Bean
    SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            JwtAuthenticationEntryPoint authenticationEntryPoint
    ) throws Exception {
        return http
                .securityMatcher("/api/v1/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .bearerTokenResolver(new StrictBearerTokenResolver())
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(authenticationConverter())))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityContext(context -> context.requireExplicitSave(true))
                .csrf(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    OAuth2TokenValidator<Jwt> jwtTokenValidator(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer,
            @Value("${spring.security.oauth2.resourceserver.jwt.audiences}") String audience
    ) {
        requireText(issuer, "JWT issuer URI is required");
        if (!REQUIRED_AUDIENCE.equals(audience)) {
            throw new IllegalStateException("JWT audience must be imperator-api");
        }

        JwtTimestampValidator timestampValidator = new JwtTimestampValidator(Duration.ofSeconds(60));
        return new DelegatingOAuth2TokenValidator<>(
                timestampValidator,
                new JwtIssuerValidator(issuer),
                new JwtContractValidator(audience)
        );
    }

    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    JwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.jws-algorithms}") String algorithm,
            OAuth2TokenValidator<Jwt> jwtTokenValidator
    ) {
        URI uri = URI.create(requireText(jwkSetUri, "JWT JWKS URI is required"));
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalStateException("JWT JWKS URI must use HTTPS");
        }
        if (!REQUIRED_ALGORITHM.equals(algorithm)) {
            throw new IllegalStateException("JWT algorithm must be RS256");
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(uri.toString())
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .build();
        decoder.setJwtValidator(jwtTokenValidator);
        return decoder;
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> authenticationConverter() {
        return jwt -> new JwtAuthenticationToken(jwt, List.of(), jwt.getSubject());
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(message);
        }
        return value.trim();
    }
}
