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
    public Page<TeamResponse> searchTeams(String name, Pageable pageable) {
        Specification<Team> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Team> teamsPage = teamRepository.findAll(spec, pageable);
        return teamsPage.map(this::toTeamResponse);
    }


    @Transactional
    public TeamResponse createTeam(TeamRequest teamRequest, Long creatorId){
        if (!StringUtils.hasText(teamRequest.name())) {
             throw new IllegalArgumentException("O nome da equipe não pode ser vazio.");
        }
        if (teamRepository.existsByNameIgnoreCase(teamRequest.name())) {
            throw new RuntimeException("Conflito: Já existe uma equipe com o nome '" + teamRequest.name() + "'.");
        }
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Não encontrado: Creator User not found with ID: " + creatorId));

        Team newTeam = new Team();
        newTeam.setName(teamRequest.name());
        newTeam.setDescription(teamRequest.description());

        Set<User> teamMembers = new HashSet<>();
        teamMembers.add(creator);
        if (teamRequest.userIds() != null && !teamRequest.userIds().isEmpty()) {
            List<User> selectedUsers = userRepository.findAllById(teamRequest.userIds());
            Set<Long> foundUserIds = selectedUsers.stream().map(User::getId).collect(Collectors.toSet());
            List<Long> notFoundIds = teamRequest.userIds().stream()
                                        .filter(id -> !foundUserIds.contains(id))
                                        .toList();
            if (!notFoundIds.isEmpty()) {
                 throw new RuntimeException("Não encontrado: Usuário(s) com ID(s) " + notFoundIds + " não encontrado(s).");
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
                .orElseThrow(() -> new RuntimeException("Não encontrado: Team not found!"));

        if (StringUtils.hasText(teamRequest.name()) && !teamRequest.name().equalsIgnoreCase(existingTeam.getName())) {
            Optional<Team> teamWithSameName = teamRepository.findByNameIgnoreCase(teamRequest.name());
            if (teamWithSameName.isPresent() && !teamWithSameName.get().getId().equals(teamId)) {
                 throw new RuntimeException("Conflito: Já existe outra equipe com o nome '" + teamRequest.name() + "'.");
            }
            existingTeam.setName(teamRequest.name());
        }
        if (teamRequest.description() != null) {
            existingTeam.setDescription(teamRequest.description());
        }
        if (teamRequest.userIds() != null) {
            Set<User> newMembers = new HashSet<>();
            if (!teamRequest.userIds().isEmpty()) {
                List<User> users = userRepository.findAllById(teamRequest.userIds());
                Set<Long> foundUserIds = users.stream().map(User::getId).collect(Collectors.toSet());
                List<Long> notFoundIds = teamRequest.userIds().stream()
                                            .filter(id -> !foundUserIds.contains(id))
                                            .toList();
                if (!notFoundIds.isEmpty()) {
                     throw new RuntimeException("Não encontrado: Usuário(s) com ID(s) " + notFoundIds + " não encontrado(s).");
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
                .orElseThrow(() -> new RuntimeException("Não encontrado: Team not found!"));
        teamRepository.delete(team);
    }
}
