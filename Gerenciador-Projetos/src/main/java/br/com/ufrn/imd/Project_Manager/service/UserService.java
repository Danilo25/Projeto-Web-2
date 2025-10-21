package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.UserRequest;
import br.com.ufrn.imd.Project_Manager.dtos.UserResponse;
import br.com.ufrn.imd.Project_Manager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<UserResponse> listAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return toUserResponse(user);
    }

    @Transactional
    public List<UserResponse> searchUserByName(String name) {
        List<User> users;
        if (StringUtils.hasText(name)) {
            users = userRepository.findByNameContainingIgnoreCase(name);
        } else {
            users = userRepository.findAll();
        }
        return users.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
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

        if (StringUtils.hasText(userRequest.name())) {
            existingUser.setName(userRequest.name());
        }
        if (StringUtils.hasText(userRequest.email())) {
            existingUser.setEmail(userRequest.email());
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
