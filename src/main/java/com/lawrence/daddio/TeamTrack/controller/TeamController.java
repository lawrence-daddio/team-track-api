package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.dto.TeamDto;
import com.lawrence.daddio.TeamTrack.service.TeamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@Slf4j
public class TeamController {


    private final TeamService service;

    public TeamController(TeamService service) {
        this.service = service;
    }

    @GetMapping("/{name}")
    public ResponseEntity<TeamDto> getTeamByName(@PathVariable("name") @NotBlank @Size(max = 255) String name) {
        log.info("Getting team by name {}", name);
        TeamDto teamDto = service.getTeamName(name);

        if (teamDto == null) {
            log.debug("No team found with name {}", name);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(teamDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TeamDto>> getTeams() {
        log.info("Getting all teams");
        List<TeamDto> teamDtos = service.getTeams();

        if (teamDtos.isEmpty()) {
            log.debug("No teams found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(teamDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody TeamDto teamDto) {

        log.info("Creating team {}", teamDto);
        TeamDto createdTeamDto = service.createTeam(teamDto);
        return new ResponseEntity<>(createdTeamDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDto> updateTeam(@PathVariable("id") Long id, @Valid @RequestBody TeamDto teamDto) {
        log.info("Updating team for id {}", id);
        TeamDto updatedTeam = service.updateTeam(teamDto, id);
        return new ResponseEntity<>(updatedTeam, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable("id") Long id) {
        log.info("Deleting team with id {}", id);
        service.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

}
