package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.TeamRequest;
import br.com.ufrn.imd.Project_Manager.dtos.TeamResponse;
import br.com.ufrn.imd.Project_Manager.model.Team;
import br.com.ufrn.imd.Project_Manager.model.User;
import br.com.ufrn.imd.Project_Manager.repository.TeamRepository;
import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    private TeamResponse toTeamResponse(Team team) {
        Set<Long> userIds = team.getUsers().stream()
                             .map(User::getId)
                             .collect(Collectors.toSet());
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                userIds
        );
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> listAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::toTeamResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeamById(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found!"));
        return toTeamResponse(team);
    }

    @Transactional(readOnly = true)
    public Set<TeamResponse> findTeamsByUserId(Long userId) {
        Set<Team> teams = teamRepository.findByUsers_Id(userId);
        return teams.stream()
                .map(this::toTeamResponse)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
     public List<TeamResponse> searchTeamByName(String name) {
        List<Team> teams;
        if (StringUtils.hasText(name)) {
            teams = teamRepository.findByNameContainingIgnoreCase(name);
        } else {
            teams = teamRepository.findAll();
        }
        return teams.stream()
                .map(this::toTeamResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public TeamResponse createTeam(TeamRequest teamRequest, Long creatorId){
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator User not found with ID: " + creatorId));
        Team newTeam = new Team();
        newTeam.setName(teamRequest.name());
        newTeam.setDescription(teamRequest.description());

        Set<User> teamMembers = new HashSet<>();
        teamMembers.add(creator);
        if (teamRequest.userIds() != null && !teamRequest.userIds().isEmpty()) { //
            List<User> selectedUsers = userRepository.findAllById(teamRequest.userIds()); //
            if (selectedUsers.size() != teamRequest.userIds().size()) { 
                 throw new RuntimeException("One or more selected users not found!"); //
            }
            teamMembers.addAll(selectedUsers);
        }
        newTeam.setUsers(teamMembers);
        Team savedTeam = teamRepository.save(newTeam); 
        return toTeamResponse(savedTeam);
    }

    @Transactional
    public TeamResponse updateTeam(Long teamId, TeamRequest teamRequest) {
        Team existingTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found!"));

        if (StringUtils.hasText(teamRequest.name())) {
            existingTeam.setName(teamRequest.name());
        }
        if (teamRequest.description() != null) {
            existingTeam.setDescription(teamRequest.description());
        }

        if (teamRequest.userIds() != null) {
            if (teamRequest.userIds().isEmpty()) {
                existingTeam.getUsers().clear();
            } else {
                List<User> users = userRepository.findAllById(teamRequest.userIds());
                 if (users.size() != teamRequest.userIds().size()) {
                    throw new RuntimeException("One or more users not found!");
                 }
                existingTeam.setUsers(new HashSet<>(users));
            }
        }

        Team updatedTeam = teamRepository.save(existingTeam);
        return toTeamResponse(updatedTeam);
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found!"));
        teamRepository.delete(team);
    }
}
