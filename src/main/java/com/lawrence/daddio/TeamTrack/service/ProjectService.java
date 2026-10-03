package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.ProjectDto;
import com.lawrence.daddio.TeamTrack.entity.Project;
import com.lawrence.daddio.TeamTrack.entity.Task;
import com.lawrence.daddio.TeamTrack.mapper.ProjectMapper;
import com.lawrence.daddio.TeamTrack.repo.ProjectRepository;
import com.lawrence.daddio.TeamTrack.repo.TaskRepository;
import com.lawrence.daddio.TeamTrack.repo.TeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final ProjectMapper mapper;

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository,
                          TeamRepository teamRepository, ProjectMapper mapper) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.teamRepository = teamRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public ProjectDto getProject(long id) {
        return projectRepository.findById(id)
                .map(mapper::projectToProjectDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjects() {
        return mapper.projectsToProjectDtos(projectRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByTeam(long teamId) {
        return mapper.projectsToProjectDtos(projectRepository.findByTeamId(teamId));
    }

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {

        Project newProject = mapper.projectDtoToProject(projectDto);
        newProject.setId(null);

        // team is optional, but if one is given it must exist
        Long teamId = projectDto.getTeamId();
        if (teamId != null) {
            newProject.setTeam(teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Team not found: " + teamId)));
        }

        List<Long> taskIds = projectDto.getTaskIds();
        if (taskIds != null && !taskIds.isEmpty()) {
            List<Task> tasks = taskRepository.findAllById(taskIds);
            if (tasks.size() != new HashSet<>(taskIds).size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "One or more tasks not found: " + taskIds);
            }
            tasks.forEach(task -> task.setProject(newProject));
            newProject.setTasks(tasks);
        }

        return mapper.projectToProjectDto(projectRepository.save(newProject));
    }

    public boolean deleteProject(long id) {
        if (!projectRepository.existsById(id)) {
            return false;
        }
        projectRepository.deleteById(id);
        return true;
    }
}
