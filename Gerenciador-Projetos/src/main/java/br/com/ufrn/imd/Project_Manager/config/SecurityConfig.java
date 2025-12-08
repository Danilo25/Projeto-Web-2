package br.com.ufrn.imd.Project_Manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/login", "/web/register", "/api/users", "/api/positions/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/clients/**", "/roles/**").hasRole("ADMIN")
                .requestMatchers("/web/positions/**", "/web/clients/**").hasRole("ADMIN")
                .requestMatchers("/api/positions/**", "/api/clients/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder() {
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                System.out.println("--- [DEBUG AUTH] Iniciando Comparação de Senhas ---");
                System.out.println("Senha digitada (raw): " + rawPassword);
                System.out.println("Hash armazenado no banco: " + encodedPassword);
                
                boolean matches = super.matches(rawPassword, encodedPassword);
                
                System.out.println("Resultado da comparação: " + (matches ? "SUCESSO (Iguais)" : "FALHA (Diferentes)"));
                System.out.println("---------------------------------------------------");
                return matches;
            }
        };
    }
}