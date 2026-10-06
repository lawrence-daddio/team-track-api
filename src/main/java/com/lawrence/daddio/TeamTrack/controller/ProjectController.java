package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.dto.ProjectDto;
import com.lawrence.daddio.TeamTrack.service.ProjectService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@Slf4j
public class ProjectController {

    private ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {

        log.info("Getting project by id {}", id);
        ProjectDto projectDto = service.getProjectById(id);

        if (projectDto == null) {
            log.debug("No project found with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {

        log.info("Getting all projects");
        List<ProjectDto> projectDtos = service.getAllProjects();

        if (projectDtos.isEmpty()) {
            log.debug("No projects found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(projectDtos, HttpStatus.OK);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByTeam(@PathVariable("teamId") Long teamId) {

        log.info("Getting all projects for team {}", teamId);
        List<ProjectDto> projectDtos = service.getProjectsByTeam(teamId);

        if(projectDtos.isEmpty()) {
            log.debug("No projects found for team {}", teamId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(projectDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto) {

        log.info("Creating project {}", projectDto);
        ProjectDto createdProjectDto = service.createProject(projectDto);

        return new ResponseEntity<>(createdProjectDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") Long id) {
        log.info("Deleting project with id {}", id);
        service.deleteProject(id);

        return ResponseEntity.noContent().build();
    }


}
