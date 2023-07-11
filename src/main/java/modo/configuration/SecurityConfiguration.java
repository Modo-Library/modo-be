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


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .cors().disable()
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/api/v2/**").permitAll()
                                .requestMatchers("/oauth/**").permitAll()
                                .requestMatchers("/docs/**").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/profile").permitAll()
                                .requestMatchers("/api/v1/**").authenticated()
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
                    .requestMatchers("/profile");
        };
    }
}