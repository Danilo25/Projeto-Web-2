package br.com.ufrn.imd.Project_Manager.config;

import br.com.ufrn.imd.Project_Manager.model.Role;
import br.com.ufrn.imd.Project_Manager.model.User;
import br.com.ufrn.imd.Project_Manager.repository.RoleRepository;
import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            Role roleAdmin = createRoleIfNotFound("ROLE_ADMIN");
            Role roleUser = createRoleIfNotFound("ROLE_USER");

            if (userRepository.findByEmailIgnoreCase("admin@sistema.com").isEmpty()) {
                User admin = new User();
                admin.setName("Administrador");
                admin.setEmail("admin@sistema.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(roleAdmin);
                userRepository.save(admin);
                System.out.println(">> ADMIN criado: admin@sistema.com / admin123");
            }
        };
    }

    private Role createRoleIfNotFound(String name) {
        return roleRepository.findAll().stream()
                .filter(r -> r.getName().equals(name))
                .findFirst()
                .orElseGet(() -> roleRepository.save(new Role(name)));
    }
}