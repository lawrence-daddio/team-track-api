package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.TeamDto;
import com.lawrence.daddio.TeamTrack.entity.Team;
import com.lawrence.daddio.TeamTrack.mapper.TeamMapper;
import com.lawrence.daddio.TeamTrack.repo.TeamRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository repository;
    private final TeamMapper mapper;

    public TeamService(TeamRepository repository, TeamMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public TeamDto getTeam(String name) {
        Team team = repository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Team not found: " + name));

        return mapper.teamToTeamDto(team);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getTeams() {
        return mapper.teamsToTeamDtos(repository.findAll());
    }

    @Transactional
    public TeamDto createTeam(TeamDto teamDto) {
        if (repository.existsByName(teamDto.getName())) {
            throw nameConflict(teamDto.getName());
        }

        Team newTeam = mapper.teamDtoToTeam(teamDto);
        newTeam.setId(null);

        return mapper.teamToTeamDto(saveAndFlush(newTeam));
    }

    @Transactional
    public TeamDto updateTeam(TeamDto teamDto, long id) {
        Team team = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Team not found for id: " + id));

        boolean nameTaken = repository.findByName(teamDto.getName())
                .filter(existing -> !existing.getId().equals(id))
                .isPresent();
        if (nameTaken) {
            throw nameConflict(teamDto.getName());
        }

        // Update the managed entity rather than saving a mapped DTO, so projects and
        // teamMemberships (orphanRemoval = true) are left untouched.
        team.setName(teamDto.getName());

        return mapper.teamToTeamDto(saveAndFlush(team));
    }

    @Transactional
    public void deleteTeam(long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found for id: " + id);
        }
        repository.deleteById(id);
    }

    // Flush immediately so a unique-name violation from a concurrent request surfaces here
    // as a 409 instead of at commit time as a 500.
    private Team saveAndFlush(Team team) {
        try {
            return repository.saveAndFlush(team);
        } catch (DataIntegrityViolationException e) {
            throw nameConflict(team.getName());
        }
    }

    private ResponseStatusException nameConflict(String name) {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Team already exists: " + name);
    }
}
