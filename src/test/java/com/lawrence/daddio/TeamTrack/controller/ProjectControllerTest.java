package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.config.SecurityConfig;
import com.lawrence.daddio.TeamTrack.dto.ProjectDto;
import com.lawrence.daddio.TeamTrack.service.ProjectService;
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

@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService service;

    private static ProjectDto project(Long id, String name) {
        ProjectDto dto = new ProjectDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription("desc");
        dto.setTeamId(1L);
        dto.setTaskIds(List.of(50L));
        return dto;
    }

    @Test
    void getProject() throws Exception {
        when(service.getProject(1L)).thenReturn(project(1L, "Apollo"));

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Apollo"))
                .andExpect(jsonPath("$.teamId").value(1))
                .andExpect(jsonPath("$.taskIds[0]").value(50));
    }

    @Test
    void getProject_notFound() throws Exception {
        when(service.getProject(99L)).thenReturn(null);

        mockMvc.perform(get("/projects/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProjects() throws Exception {
        when(service.getProjects()).thenReturn(List.of(project(1L, "Apollo"), project(2L, "Gemini")));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].name").value("Gemini"));
    }

    @Test
    void getProjects_emptyReturnsEmptyList() throws Exception {
        when(service.getProjects()).thenReturn(List.of());

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getProjectsByTeam() throws Exception {
        when(service.getProjectsByTeam(1L)).thenReturn(List.of(project(1L, "Apollo")));

        mockMvc.perform(get("/projects/team/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Apollo"));
    }

    @Test
    void createProject() throws Exception {
        when(service.createProject(any(ProjectDto.class))).thenReturn(project(5L, "New"));

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New\",\"description\":\"desc\",\"teamId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.teamId").value(1));
    }

    @Test
    void deleteProject() throws Exception {
        when(service.deleteProject(4L)).thenReturn(true);

        mockMvc.perform(delete("/projects/4"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProject_notFound() throws Exception {
        when(service.deleteProject(99L)).thenReturn(false);

        mockMvc.perform(delete("/projects/99"))
                .andExpect(status().isNotFound());
    }
}
