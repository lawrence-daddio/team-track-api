package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.TeamMembershipDto;
import com.lawrence.daddio.TeamTrack.entity.TeamMembership;
import com.lawrence.daddio.TeamTrack.mapper.TeamMembershipMapper;
import com.lawrence.daddio.TeamTrack.repo.EmployeeRepository;
import com.lawrence.daddio.TeamTrack.repo.TeamMembershipRepository;
import com.lawrence.daddio.TeamTrack.repo.TeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TeamMembershipService {

    private final TeamMembershipRepository repository;
    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;
    private final TeamMembershipMapper mapper;

    public TeamMembershipService(TeamMembershipRepository repository, EmployeeRepository employeeRepository,
                                 TeamRepository teamRepository, TeamMembershipMapper mapper) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.teamRepository = teamRepository;
        this.mapper = mapper;
    }

    public TeamMembershipDto getTeamMembershipById(Long id) {
        return repository.findById(id)
                .map(mapper::TeamMembershipToTeamMembershipDto)
                .orElse(null);
    }

    public List<TeamMembershipDto> getTeamMemberships() {
        return mapper.TeamMembershipToTeamMembershipDtoList(repository.findAll());
    }

    public List<TeamMembershipDto> getTeamMembershipsByTeamId(Long teamId) {
        return mapper.TeamMembershipToTeamMembershipDtoList(repository.findByTeamId(teamId));
    }

    public List<TeamMembershipDto> getTeamMembershipsByEmployeeId(Long employeeId) {
        return mapper.TeamMembershipToTeamMembershipDtoList(repository.findByEmployeeId(employeeId));
    }

    @Transactional
    public TeamMembershipDto createTeamMembership(TeamMembershipDto teamMembershipDto) {
        TeamMembership teamMembership = mapper.TeamMembershipDtoToTeamMembership(teamMembershipDto);
        teamMembership.setId(null);

        Long employeeId = teamMembershipDto.getEmployeeId();
        teamMembership.setEmployee(employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Employee not found: " + employeeId)));

        Long teamId = teamMembershipDto.getTeamId();
        teamMembership.setTeam(teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Team not found: " + teamId)));

        return mapper.TeamMembershipToTeamMembershipDto(repository.save(teamMembership));
    }

    public void deleteTeamMembership(Long id) {
        repository.deleteById(id);
    }
}
