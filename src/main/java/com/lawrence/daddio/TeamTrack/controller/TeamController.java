package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.dto.TeamDto;
import com.lawrence.daddio.TeamTrack.service.TeamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {


    private final TeamService service;

    public TeamController(TeamService service) {
        this.service = service;
    }

    @GetMapping("/{name}")
    public ResponseEntity<TeamDto> getTeam(@PathVariable("name") @NotBlank @Size(max = 255) String name) {

        return new ResponseEntity<>(service.getTeam(name), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TeamDto>> getTeams() {

        return new ResponseEntity<>(service.getTeams(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody TeamDto teamDto) {

        TeamDto createdTeam = service.createTeam(teamDto);

        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDto> updateTeam(@PathVariable("id") long id, @Valid @RequestBody TeamDto teamDto) {

        TeamDto updatedTeam = service.updateTeam(teamDto, id);

        return new ResponseEntity<>(updatedTeam, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable("id") long id) {

        service.deleteTeam(id);

        return ResponseEntity.noContent().build();
    }

}
