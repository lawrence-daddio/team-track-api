package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.TeamMembershipDto;
import com.lawrence.daddio.TeamTrack.dto.update.TeamMembershipUpdateDto;
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

    private final TeamMembershipRepository teamMembershipRepository;
    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;
    private final TeamMembershipMapper mapper;

    public TeamMembershipService(TeamMembershipRepository teamMembershipRepository, EmployeeRepository employeeRepository,
                                 TeamRepository teamRepository, TeamMembershipMapper mapper) {
        this.teamMembershipRepository = teamMembershipRepository;
        this.employeeRepository = employeeRepository;
        this.teamRepository = teamRepository;
        this.mapper = mapper;
    }

    public TeamMembershipDto getTeamMembershipById(Long id) {
        return teamMembershipRepository.findById(id)
                .map(mapper::TeamMembershipToTeamMembershipDto)
                .orElse(null);
    }

    public List<TeamMembershipDto> getTeamMemberships() {
        return mapper.TeamMembershipToTeamMembershipDtoList(teamMembershipRepository.findAll());
    }

    public List<TeamMembershipDto> getTeamMembershipsByTeamId(Long teamId) {
        return mapper.TeamMembershipToTeamMembershipDtoList(teamMembershipRepository.findByTeamId(teamId));
    }

    public List<TeamMembershipDto> getTeamMembershipsByEmployeeId(Long employeeId) {
        return mapper.TeamMembershipToTeamMembershipDtoList(teamMembershipRepository.findByEmployeeId(employeeId));
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

        return mapper.TeamMembershipToTeamMembershipDto(teamMembershipRepository.save(teamMembership));
    }

    @Transactional
    public TeamMembershipDto updateTeamMembership(Long id, TeamMembershipUpdateDto teamMembershipUpdateDto) {
        TeamMembership teamMembership = teamMembershipRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team Membership not found for id: " + id));
        teamMembership.setRole(teamMembershipUpdateDto.getRole());
        return mapper.TeamMembershipToTeamMembershipDto(teamMembershipRepository.save(teamMembership));
    }

    @Transactional
    public void deleteTeamMembership(Long id) {
        teamMembershipRepository.deleteById(id);
    }
}
