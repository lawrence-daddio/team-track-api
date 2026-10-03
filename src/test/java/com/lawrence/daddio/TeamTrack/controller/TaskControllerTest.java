package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.config.SecurityConfig;
import com.lawrence.daddio.TeamTrack.dto.TaskDto;
import com.lawrence.daddio.TeamTrack.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService service;

    private static TaskDto task(Long id, String title) {
        TaskDto dto = new TaskDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setStatus("OPEN");
        dto.setProjectId(1L);
        dto.setEmployeeId(3L);
        dto.setCommentIds(List.of(8L));
        return dto;
    }

    @Test
    void getTask() throws Exception {
        when(service.getTask(1L)).thenReturn(task(1L, "Write docs"));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Write docs"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.employeeId").value(3))
                .andExpect(jsonPath("$.commentIds[0]").value(8));
    }

    @Test
    void getTask_notFound() throws Exception {
        when(service.getTask(99L)).thenReturn(null);

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasks() throws Exception {
        when(service.getTasks()).thenReturn(List.of(task(1L, "a"), task(2L, "b")));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].title").value("b"));
    }

    @Test
    void getTasks_emptyReturnsEmptyList() throws Exception {
        when(service.getTasks()).thenReturn(List.of());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getTasksByProject() throws Exception {
        when(service.getTasksByProject(1L)).thenReturn(List.of(task(1L, "a")));

        mockMvc.perform(get("/tasks/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("a"));
    }

    @Test
    void getTasksByEmployee() throws Exception {
        when(service.getTasksByEmployee(3L)).thenReturn(List.of(task(1L, "a")));

        mockMvc.perform(get("/tasks/employee/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("a"));
    }

    @Test
    void createTask() throws Exception {
        when(service.createTask(any(TaskDto.class))).thenReturn(task(5L, "New"));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New\",\"status\":\"OPEN\",\"projectId\":1,\"employeeId\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.projectId").value(1));
    }

    @Test
    void deleteTask() throws Exception {
        when(service.deleteTask(4L)).thenReturn(true);

        mockMvc.perform(delete("/tasks/4"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_notFound() throws Exception {
        when(service.deleteTask(99L)).thenReturn(false);

        mockMvc.perform(delete("/tasks/99"))
                .andExpect(status().isNotFound());
    }
}
