package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.config.SecurityConfig;
import com.lawrence.daddio.TeamTrack.dto.TeamMembershipDto;
import com.lawrence.daddio.TeamTrack.service.TeamMembershipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamMembershipController.class)
@Import(SecurityConfig.class)
class TeamMembershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamMembershipService service;

    private static TeamMembershipDto membership(Long id, String role) {
        TeamMembershipDto dto = new TeamMembershipDto();
        dto.setId(id);
        dto.setRole(role);
        dto.setEmployeeId(3L);
        dto.setTeamId(1L);
        return dto;
    }

    @Test
    void getTeamMembership() throws Exception {
        when(service.getTeamMembership(1L)).thenReturn(membership(1L, "LEAD"));

        mockMvc.perform(get("/team-memberships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("LEAD"))
                .andExpect(jsonPath("$.employeeId").value(3))
                .andExpect(jsonPath("$.teamId").value(1));
    }

    @Test
    void getTeamMembership_notFound() throws Exception {
        when(service.getTeamMembership(99L)).thenReturn(null);

        mockMvc.perform(get("/team-memberships/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTeamMemberships() throws Exception {
        when(service.getTeamMemberships()).thenReturn(List.of(membership(1L, "LEAD"), membership(2L, "DEV")));

        mockMvc.perform(get("/team-memberships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].role").value("DEV"));
    }

    @Test
    void getTeamMemberships_emptyReturnsEmptyList() throws Exception {
        when(service.getTeamMemberships()).thenReturn(List.of());

        mockMvc.perform(get("/team-memberships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getTeamMembershipsByTeam() throws Exception {
        when(service.getTeamMembershipsByTeam(1L)).thenReturn(List.of(membership(1L, "LEAD")));

        mockMvc.perform(get("/team-memberships/team/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("LEAD"));
    }

    @Test
    void getTeamMembershipsByEmployee() throws Exception {
        when(service.getTeamMembershipsByEmployee(3L)).thenReturn(List.of(membership(1L, "LEAD")));

        mockMvc.perform(get("/team-memberships/employee/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("LEAD"));
    }

    @Test
    void createTeamMembership() throws Exception {
        when(service.createTeamMembership(any(TeamMembershipDto.class))).thenReturn(membership(5L, "DEV"));

        mockMvc.perform(post("/team-memberships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"DEV\",\"employeeId\":3,\"teamId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.role").value("DEV"));
    }

    @Test
    void createTeamMembership_missingIdsAreRejected() throws Exception {
        mockMvc.perform(post("/team-memberships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"DEV\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void deleteTeamMembership() throws Exception {
        when(service.deleteTeamMembership(4L)).thenReturn(true);

        mockMvc.perform(delete("/team-memberships/4"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTeamMembership_notFound() throws Exception {
        when(service.deleteTeamMembership(99L)).thenReturn(false);

        mockMvc.perform(delete("/team-memberships/99"))
                .andExpect(status().isNotFound());
    }
}
