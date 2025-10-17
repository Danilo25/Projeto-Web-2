package br.com.ufrn.imd.Project_Manager.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ufrn.imd.Project_Manager.model.Team;
import br.com.ufrn.imd.Project_Manager.model.User;
import br.com.ufrn.imd.Project_Manager.repository.TeamRepository;
import br.com.ufrn.imd.Project_Manager.repository.UserRepository;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    public Team saveTeam(Team team, List<Long> usersID) {
        if (usersID != null && !usersID.isEmpty()) {
            List<User> users = userRepository.findAllById(usersID);
            team.setUsers(new HashSet<>(users));
        }
        return teamRepository.save(team);
    }
    
}
