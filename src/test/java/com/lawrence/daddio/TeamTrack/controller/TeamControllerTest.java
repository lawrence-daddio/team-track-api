package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.config.SecurityConfig;
import com.lawrence.daddio.TeamTrack.dto.TeamDto;
import com.lawrence.daddio.TeamTrack.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamController.class)
@Import(SecurityConfig.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamService service;

    private static TeamDto team(Long id, String name) {
        TeamDto dto = new TeamDto();
        dto.setId(id);
        dto.setName(name);
        dto.setProjectIds(List.of(10L, 11L));
        dto.setTeamMembershipIds(List.of(20L));
        return dto;
    }

    @Test
    void getTeam() throws Exception {
        when(service.getTeam("Alpha")).thenReturn(team(1L, "Alpha"));

        mockMvc.perform(get("/teams/Alpha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alpha"))
                .andExpect(jsonPath("$.projectIds[0]").value(10))
                .andExpect(jsonPath("$.teamMembershipIds[0]").value(20));
    }

    @Test
    void getTeam_notFound() throws Exception {
        when(service.getTeam("Missing"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found: Missing"));

        mockMvc.perform(get("/teams/Missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTeams() throws Exception {
        when(service.getTeams()).thenReturn(List.of(team(1L, "Alpha"), team(2L, "Beta")));

        mockMvc.perform(get("/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alpha"))
                .andExpect(jsonPath("$[1].name").value("Beta"));
    }

    @Test
    void createTeam() throws Exception {
        when(service.createTeam(any(TeamDto.class))).thenReturn(team(5L, "Gamma"));

        mockMvc.perform(post("/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gamma\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Gamma"));
    }

    @Test
    void createTeam_blankNameIsRejected() throws Exception {
        mockMvc.perform(post("/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"  \"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void createTeam_nameTooLongIsRejected() throws Exception {
        String longName = "x".repeat(256);

        mockMvc.perform(post("/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + longName + "\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void createTeam_duplicateNameReturnsConflict() throws Exception {
        when(service.createTeam(any(TeamDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Team already exists: Alpha"));

        mockMvc.perform(post("/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alpha\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void updateTeam() throws Exception {
        when(service.updateTeam(any(TeamDto.class), eq(3L))).thenReturn(team(3L, "Renamed"));

        mockMvc.perform(put("/teams/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Renamed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Renamed"));
    }

    @Test
    void updateTeam_notFound() throws Exception {
        when(service.updateTeam(any(TeamDto.class), eq(99L)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found for id: 99"));

        mockMvc.perform(put("/teams/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Renamed\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTeam() throws Exception {
        mockMvc.perform(delete("/teams/4"))
                .andExpect(status().isNoContent());

        verify(service).deleteTeam(4L);
    }

    @Test
    void deleteTeam_notFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found for id: 99"))
                .when(service).deleteTeam(99L);

        mockMvc.perform(delete("/teams/99"))
                .andExpect(status().isNotFound());
    }
}
