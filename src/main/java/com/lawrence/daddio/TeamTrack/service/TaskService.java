package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.TaskDto;
import com.lawrence.daddio.TeamTrack.entity.Task;
import com.lawrence.daddio.TeamTrack.mapper.TaskMapper;
import com.lawrence.daddio.TeamTrack.repo.EmployeeRepository;
import com.lawrence.daddio.TeamTrack.repo.ProjectRepository;
import com.lawrence.daddio.TeamTrack.repo.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository repository, ProjectRepository projectRepository,
                       EmployeeRepository employeeRepository, TaskMapper mapper) {
        this.repository = repository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public TaskDto getTask(long id) {
        return repository.findById(id)
                .map(mapper::TaskToTaskDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasks() {
        return mapper.TaskToTaskDtoList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByProject(long projectId) {
        return mapper.TaskToTaskDtoList(repository.findByProjectId(projectId));
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByEmployee(long employeeId) {
        return mapper.TaskToTaskDtoList(repository.findByEmployeeId(employeeId));
    }

    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        Task task = mapper.TaskDtoToTask(taskDto);
        task.setId(null);

        // project and employee are optional, but if given they must exist
        Long projectId = taskDto.getProjectId();
        if (projectId != null) {
            task.setProject(projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Project not found: " + projectId)));
        }

        Long employeeId = taskDto.getEmployeeId();
        if (employeeId != null) {
            task.setEmployee(employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Employee not found: " + employeeId)));
        }

        return mapper.TaskToTaskDto(repository.save(task));
    }

    public boolean deleteTask(long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
