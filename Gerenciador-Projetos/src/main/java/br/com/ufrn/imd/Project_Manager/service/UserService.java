package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.UserRequest;
import br.com.ufrn.imd.Project_Manager.dtos.UserResponse;
import br.com.ufrn.imd.Project_Manager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPosition()
        );
    }

    @Transactional
    public Page<UserResponse> listAllUsers(Pageable pageable) {
        return searchUsers(null, null, pageable);
    }

    @Transactional
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return toUserResponse(user);
    }

    @Transactional
    public Page<UserResponse> searchUsers(String name, String position, Pageable pageable) {
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
                ));
            }
            if (StringUtils.hasText(position)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("position")),
                    "%" + position.toLowerCase() + "%"
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Page<User> usersPage = userRepository.findAll(spec, pageable);
        return usersPage.map(this::toUserResponse);
    }

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.existsByEmailIgnoreCase(userRequest.email())) {
            throw new RuntimeException("Conflito: O e-mail '" + userRequest.email() + "' já está em uso.");
        }
        if (userRepository.existsByNameIgnoreCase(userRequest.name())) {
            throw new RuntimeException("Conflito: O nome '" + userRequest.name() + "' já está em uso.");
        }
        User newUser = new User();
        newUser.setName(userRequest.name());
        newUser.setEmail(userRequest.email());
        newUser.setPassword(userRequest.password());
        newUser.setPosition(userRequest.position());

        User savedUser = userRepository.save(newUser);
        return toUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (StringUtils.hasText(userRequest.email()) && !userRequest.email().equalsIgnoreCase(existingUser.getEmail())) {
            Optional<User> userWithSameEmail = userRepository.findByEmailIgnoreCase(userRequest.email());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(userId)) {
                throw new RuntimeException("Conflito: O e-mail '" + userRequest.email() + "' já está em uso por outro usuário.");
            }
            existingUser.setEmail(userRequest.email());
        }
        if (StringUtils.hasText(userRequest.name()) && !userRequest.name().equalsIgnoreCase(existingUser.getName())) {
            Optional<User> userWithSameName = userRepository.findByNameIgnoreCase(userRequest.name());
            if (userWithSameName.isPresent() && !userWithSameName.get().getId().equals(userId)) {
                 throw new RuntimeException("Conflito: O nome '" + userRequest.name() + "' já está em uso por outro usuário.");
            }
            existingUser.setName(userRequest.name());
        }
        
        if (StringUtils.hasText(userRequest.password())) {
            existingUser.setPassword(userRequest.password());
        }
        if (StringUtils.hasText(userRequest.position())) {
            existingUser.setPosition(userRequest.position());
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
