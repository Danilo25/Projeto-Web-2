package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.DashboardPageTeamResponse;
import br.com.ufrn.imd.Project_Manager.dtos.api.TeamRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.TeamResponse;
import br.com.ufrn.imd.Project_Manager.model.Team;
import br.com.ufrn.imd.Project_Manager.model.User;
import br.com.ufrn.imd.Project_Manager.repository.TeamRepository;
import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Optional;
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

    public Page<TeamResponse> searchTeams(String name, Long memberId, Pageable pageable) {
        Page<Team> teamsPage = teamRepository.searchTeams(name, memberId, pageable);
        return teamsPage.map(this::toTeamResponse);
    }

    public TeamResponse getTeamById(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found!"));
        return toTeamResponse(team);
    }

    @Transactional
    public TeamResponse createTeam(TeamRequest teamRequest, Long creatorId){
        if (!StringUtils.hasText(teamRequest.name())) {
             throw new RuntimeException("O nome da equipe não pode ser vazio.");
        }
        if (teamRepository.existsByNameIgnoreCase(teamRequest.name())) {
            throw new RuntimeException("Conflito: Já existe uma equipe com o nome '" + teamRequest.name() + "'.");
        }
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Nenhum usuário com o ID " + creatorId + " encontrado."));

        Team newTeam = new Team();
        newTeam.setName(teamRequest.name());
        newTeam.setDescription(teamRequest.description());

        List<User> teamMembers = new ArrayList<>();
        teamMembers.add(creator);
        if (teamRequest.userIds() != null && !teamRequest.userIds().isEmpty()) {
            List<User> selectedUsers = userRepository.findAllById(teamRequest.userIds());
            List<Long> foundUserIds = selectedUsers.stream().map(User::getId).toList();
            List<Long> notFoundIds = teamRequest.userIds().stream()
                                        .filter(id -> !foundUserIds.contains(id))
                                        .toList();
            if (!notFoundIds.isEmpty()) {
                 throw new RuntimeException("Nenhum usuário com o(s) ID(s) " + notFoundIds + " encontrado(s).");
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
                .orElseThrow(() -> new RuntimeException("Equipe não encontrada!"));

        if (StringUtils.hasText(teamRequest.name()) && !teamRequest.name().equals(existingTeam.getName())) {
            Optional<Team> teamWithSameName = teamRepository.findByNameIgnoreCase(teamRequest.name());
            if (teamWithSameName.isPresent() && !teamWithSameName.get().getId().equals(teamId)) {
                 throw new RuntimeException("Conflito: já existe outra equipe com o nome '" + teamRequest.name() + "'.");
            }
            existingTeam.setName(teamRequest.name());
        }
        if (teamRequest.description() != null) {
            existingTeam.setDescription(teamRequest.description());
        }
        if (teamRequest.userIds() != null) {
            List<User> newMembers = new ArrayList<>();
            if (!teamRequest.userIds().isEmpty()) {
                List<User> users = userRepository.findAllById(teamRequest.userIds());
                List<Long> foundUserIds = users.stream().map(User::getId).toList();
                List<Long> notFoundIds = teamRequest.userIds().stream()
                                            .filter(id -> !foundUserIds.contains(id))
                                            .toList();
                if (!notFoundIds.isEmpty()) {
                     throw new RuntimeException("Nenhum usuário com o(s) ID(s) " + notFoundIds + " encontrado(s).");
                 }
                newMembers.addAll(users);
            }
            existingTeam.setUsers(newMembers);
        }

        Team updatedTeam = teamRepository.save(existingTeam);
        return toTeamResponse(updatedTeam);
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Equipe não encontrada!"));
        teamRepository.delete(team);
    }
}
