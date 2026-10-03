package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.dto.TeamMembershipDto;
import com.lawrence.daddio.TeamTrack.service.TeamMembershipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team-memberships")
public class TeamMembershipController {

    private TeamMembershipService service;

    public TeamMembershipController(TeamMembershipService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamMembershipDto> getTeamMembership(@PathVariable("id") long id) {

        TeamMembershipDto teamMembership = service.getTeamMembership(id);

        if (teamMembership == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(teamMembership, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TeamMembershipDto>> getTeamMemberships() {

        List<TeamMembershipDto> teamMemberships = service.getTeamMemberships();

        return new ResponseEntity<>(teamMemberships, HttpStatus.OK);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamMembershipDto>> getTeamMembershipsByTeam(@PathVariable("teamId") long teamId) {

        List<TeamMembershipDto> teamMemberships = service.getTeamMembershipsByTeam(teamId);

        return new ResponseEntity<>(teamMemberships, HttpStatus.OK);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TeamMembershipDto>> getTeamMembershipsByEmployee(@PathVariable("employeeId") long employeeId) {

        List<TeamMembershipDto> teamMemberships = service.getTeamMembershipsByEmployee(employeeId);

        return new ResponseEntity<>(teamMemberships, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TeamMembershipDto> createTeamMembership(@Valid @RequestBody TeamMembershipDto teamMembershipDto) {

        TeamMembershipDto createdTeamMembership = service.createTeamMembership(teamMembershipDto);

        return new ResponseEntity<>(createdTeamMembership, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamMembership(@PathVariable("id") long id) {

        boolean deleted = service.deleteTeamMembership(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }


}
