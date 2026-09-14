package cl.translog.bff.mobile.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService users(
            PasswordEncoder encoder,
            @Value("${security.users.web.password}") String webPassword,
            @Value("${security.users.mobile.password}") String mobilePassword,
            @Value("${security.users.cajero.password}") String cajeroPassword) {

        UserDetails web = User.builder()
                .username("webuser")
                .password(encoder.encode(webPassword))
                .roles("WEB")
                .build();

        UserDetails mobile = User.builder()
                .username("mobileuser")
                .password(encoder.encode(mobilePassword))
                .roles("MOBILE")
                .build();

        UserDetails cajero = User.builder()
                .username("cajerouser")
                .password(encoder.encode(cajeroPassword))
                .roles("CAJERO")
                .build();

        return new InMemoryUserDetailsManager(web, mobile, cajero);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/mobile/**").hasRole("MOBILE")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, ex) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"status\":401,\"error\":\"UNAUTHORIZED\",\"message\":\"Credenciales requeridas o inválidas\"}");
                        })
                        .accessDeniedHandler((request, response, ex) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"status\":403,\"error\":\"FORBIDDEN\",\"message\":\"Usuario autenticado sin permisos para este canal\"}");
                        }));

        return http.build();
    }
}
