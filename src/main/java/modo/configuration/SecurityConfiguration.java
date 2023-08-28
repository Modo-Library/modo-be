package modo.configuration;


import lombok.RequiredArgsConstructor;
import modo.auth.ExceptionHandlerFilter;
import modo.auth.JwtAuthenticationFilter;
import modo.auth.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/api/v2/**").permitAll()
                                .requestMatchers("/oauth/**").permitAll()
                                .requestMatchers("/docs/**").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/profile").permitAll()
                                .requestMatchers("/api/v1/**").authenticated()
                                .requestMatchers("/websocketTest/**").permitAll()
                                .requestMatchers("/modo-websocket/**").permitAll()
                                .requestMatchers("/modo-websocket").permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring()
                    .requestMatchers("/api/v2/**")
                    .requestMatchers("/oauth/**")
                    .requestMatchers("/docs/**")
                    .requestMatchers("/favicon.ico")
                    .requestMatchers("/error")
                    .requestMatchers("/profile")
                    .requestMatchers("/websocketTest/**")
                    .requestMatchers("/modo-websocket/**")
                    .requestMatchers("/modo-websocket");
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("https://host.modolib.site", "https://auth.modolib.site", "https://books.modolib.site", "http://localhost:5000", "http://localhost:5001", "http://localhost:5002"));
        configuration.setAllowedMethods(List.of("PUT", "POST", "GET", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}