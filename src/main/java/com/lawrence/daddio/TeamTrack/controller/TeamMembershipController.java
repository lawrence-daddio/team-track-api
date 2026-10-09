package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.dto.TeamMembershipDto;
import com.lawrence.daddio.TeamTrack.dto.update.TeamMembershipUpdateDto;
import com.lawrence.daddio.TeamTrack.service.TeamMembershipService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team-memberships")
@Slf4j
public class TeamMembershipController {

    private TeamMembershipService service;

    public TeamMembershipController(TeamMembershipService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamMembershipDto> getTeamMembershipById(@PathVariable("id") Long id) {

        log.info("Getting team membership by id {}", id);
        TeamMembershipDto teamMembershipDto = service.getTeamMembershipById(id);

        if (teamMembershipDto == null) {
            log.debug("No team membership found with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(teamMembershipDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TeamMembershipDto>> getTeamMemberships() {

        log.info("Getting all team memberships");
        List<TeamMembershipDto> teamMembershipDtos = service.getTeamMemberships();

        if (teamMembershipDtos.isEmpty()) {
            log.debug("No team memberships found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(teamMembershipDtos, HttpStatus.OK);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamMembershipDto>> getTeamMembershipsByTeamId(@PathVariable("teamId") Long teamId) {

        log.info("Getting all team memberships for team {}", teamId);
        List<TeamMembershipDto> teamMembershipDtos = service.getTeamMembershipsByTeamId(teamId);

        if (teamMembershipDtos.isEmpty()) {
            log.debug("No team memberships found for team {}", teamId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(teamMembershipDtos, HttpStatus.OK);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TeamMembershipDto>> getTeamMembershipsByEmployeeId(@PathVariable("employeeId") Long employeeId) {

        log.info("Getting all team memberships for employee {}", employeeId);
        List<TeamMembershipDto> teamMembershipDtos = service.getTeamMembershipsByEmployeeId(employeeId);

        if (teamMembershipDtos.isEmpty()) {
            log.debug("No team memberships found for employee {}", employeeId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(teamMembershipDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TeamMembershipDto> createTeamMembership(@Valid @RequestBody TeamMembershipDto teamMembershipDto) {

        log.info("Creating team membership {}", teamMembershipDto);
        TeamMembershipDto createdTeamMembership = service.createTeamMembership(teamMembershipDto);

        return new ResponseEntity<>(createdTeamMembership, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamMembershipDto> updateTeamMembership(@PathVariable("id") Long id, @Valid @RequestBody TeamMembershipUpdateDto teamMembershipUpdateDto) {

        log.info("Updating team membership {} with id {}", teamMembershipUpdateDto, id);
        TeamMembershipDto updatedTeamMembership = service.updateTeamMembership(id, teamMembershipUpdateDto);
        return new ResponseEntity<>(updatedTeamMembership, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamMembership(@PathVariable("id") Long id) {
        log.info("Deleting team membership with id {}", id);
        service.deleteTeamMembership(id);
        return ResponseEntity.noContent().build();
    }


}
