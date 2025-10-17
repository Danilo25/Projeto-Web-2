package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.ufrn.imd.Project_Manager.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUserByName(String name) {
        if (StringUtils.hasText(name)) {
            return userRepository.findByNameContainingIgnoreCase(name);
        }
        return listAllUsers();
    }
}
