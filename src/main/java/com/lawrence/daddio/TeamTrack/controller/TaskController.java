package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.dto.TaskDto;
import com.lawrence.daddio.TeamTrack.dto.update.TaskUpdateDto;
import com.lawrence.daddio.TeamTrack.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@Slf4j
public class TaskController {

    private TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {

        log.info("Getting task by id {}", id);
        TaskDto taskDto = service.getTaskById(id);

        if (taskDto == null) {
            log.debug("No task found with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks() {

        log.info("Getting all tasks");
        List<TaskDto> taskDtos = service.getTasks();

        if (taskDtos.isEmpty()) {
            log.debug("No tasks found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(taskDtos, HttpStatus.OK);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDto>> getTasksByProjectId(@PathVariable("projectId") Long projectId) {

        log.info("Getting all tasks for project {}", projectId);
        List<TaskDto> taskDtos = service.getTasksByProjectId(projectId);

        if (taskDtos.isEmpty()) {
            log.debug("No tasks found for project {}", projectId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(taskDtos, HttpStatus.OK);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TaskDto>> getTasksByEmployeeId(@PathVariable("employeeId") Long employeeId) {

        log.info("Getting all tasks for employee {}", employeeId);
        List<TaskDto> taskDtos = service.getTasksByEmployeeId(employeeId);

        if (taskDtos.isEmpty()) {
            log.debug("No tasks found for employee {}", employeeId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(taskDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {

        log.info("Creating task {}", taskDto);
        TaskDto createdTaskDto = service.createTask(taskDto);
        return new ResponseEntity<>(createdTaskDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDto taskUpdateDto) {

        log.info("Updating task {} with id {}", taskUpdateDto, id);
        TaskDto updatedDto = service.updateTask(id, taskUpdateDto);
        return new ResponseEntity<>(updatedDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("Deleting task with id {}", id);
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }


}
