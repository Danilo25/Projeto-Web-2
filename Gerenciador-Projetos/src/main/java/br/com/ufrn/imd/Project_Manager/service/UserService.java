package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.api.UserRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.UserResponse;
import br.com.ufrn.imd.Project_Manager.model.Position;
import br.com.ufrn.imd.Project_Manager.model.Role;
import br.com.ufrn.imd.Project_Manager.model.User;
import br.com.ufrn.imd.Project_Manager.repository.PositionRepository;
import br.com.ufrn.imd.Project_Manager.repository.RoleRepository;
import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PositionService positionService;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPosition() != null ?
                        positionService.toPositionResponse(user.getPosition())
                        : null
        );
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("--- [DEBUG] Tentativa de login com email: " + email); // <--- PRINT NOVO

        return userRepository.findByEmailIgnoreCase(email)
                .map(user -> {
                    System.out.println("--- [DEBUG] Usuário encontrado: " + user.getName()); // <--- PRINT NOVO
                    System.out.println("--- [DEBUG] Hash da senha no banco: " + user.getPassword()); // <--- PRINT NOVO
                    System.out.println("--- [DEBUG] Role: " + (user.getRole() != null ? user.getRole().getName() : "NULL")); // <--- PRINT NOVO
                    return user;
                })
                .orElseThrow(() -> {
                    System.out.println("--- [DEBUG] Usuário NÃO encontrado!"); // <--- PRINT NOVO
                    return new UsernameNotFoundException("Usuário não encontrado com email: " + email);
                });
    }

    public Page<UserResponse> getUsers(String name, String position, Pageable pageable) {
        Page<User> usersPage = userRepository.searchUsers(name, position, pageable);
        return usersPage.map(this::toUserResponse);
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.existsByEmailIgnoreCase(userRequest.email())) {
            throw new RuntimeException("Conflito: O e-mail '" + userRequest.email() + "' já está em uso.");
        }
        if (userRepository.existsByNameIgnoreCase(userRequest.name())) {
            throw new RuntimeException("Conflito: O nome '" + userRequest.name() + "' já está em uso.");
        }

        String encodedPassword = passwordEncoder.encode(userRequest.password());

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Erro: Role ROLE_USER não encontrada."));

        User newUser;
        if (userRequest.positionId() != null) {
            Position position = this.positionRepository.findById(userRequest.positionId())
                    .orElseThrow(() -> new RuntimeException("Position not found!"));

            newUser = new User(userRequest.name(), userRequest.email(), encodedPassword, position);
        } else {
            newUser = new User(userRequest.name(), userRequest.email(), encodedPassword);
        }

        newUser.setRole(defaultRole);

        User savedUser = userRepository.save(newUser);
        return toUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (userRequest.email() != null){
            Optional<User> user = userRepository.findByEmailIgnoreCase(userRequest.email());
            if (user.isPresent() && !user.get().getId().equals(userId)) {
                throw new RuntimeException("Conflito: O e-mail '" + userRequest.email() + "' já está em uso por outro usuário.");
            }
            existingUser.setEmail(userRequest.email());
        }
        if (userRequest.name() != null){
            Optional<User> user = userRepository.findByNameIgnoreCase(userRequest.name());
            if (user.isPresent() && !user.get().getId().equals(userId)) {
                throw new RuntimeException("Conflito: O nome '" + userRequest.name() + "' já está em uso por outro usuário.");
            }
            existingUser.setName(userRequest.name());
        }

        if (userRequest.password() != null && !userRequest.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userRequest.password()));
        }

        if (userRequest.positionId() != null) {
            Position position = this.positionRepository.findById(userRequest.positionId())
                    .orElseThrow(() -> new RuntimeException("Position not found!"));

            existingUser.setPosition(position);
        }

        User updatedUser = userRepository.save(existingUser);
        return toUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        userRepository.delete(user);
    }
}