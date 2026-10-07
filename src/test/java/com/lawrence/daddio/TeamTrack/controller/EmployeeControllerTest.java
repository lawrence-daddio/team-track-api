package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.config.SecurityConfig;
import com.lawrence.daddio.TeamTrack.dto.EmployeeDto;
import com.lawrence.daddio.TeamTrack.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@Import(SecurityConfig.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService service;

    private static EmployeeDto employee(Long id, String email) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(id);
        dto.setEmail(email);
        dto.setDisplayName("Ann");
        dto.setTaskIds(List.of(1L));
        dto.setTeamMembershipIds(List.of(2L));
        dto.setCommentIds(List.of(3L));
        return dto;
    }

    @Test
    void getEmployee() throws Exception {
        when(service.getEmployees("ann@x.com")).thenReturn(employee(1L, "ann@x.com"));

        mockMvc.perform(get("/employees/ann@x.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ann@x.com"))
                .andExpect(jsonPath("$.displayName").value("Ann"))
                .andExpect(jsonPath("$.taskIds[0]").value(1));
    }

    @Test
    void getEmployee_notFound() throws Exception {
        when(service.getEmployees("nobody@x.com")).thenReturn(null);

        mockMvc.perform(get("/employees/nobody@x.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEmployees() throws Exception {
        when(service.getEmployees()).thenReturn(List.of(employee(1L, "a@x.com"), employee(2L, "b@x.com")));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].email").value("b@x.com"));
    }

    @Test
    void getEmployees_nullReturnsNotFound() throws Exception {
        when(service.getEmployees()).thenReturn(null);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEmployee() throws Exception {
        when(service.createEmployee(any(EmployeeDto.class))).thenReturn(employee(9L, "new@x.com"));

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@x.com\",\"displayName\":\"Ann\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.email").value("new@x.com"));
    }

    @Test
    void deleteEmployee() throws Exception {
        doNothing().when(service).deleteEmployee(4L);
        mockMvc.perform(delete("/employees/4"))
                .andExpect(status().isNoContent());
    }
}
