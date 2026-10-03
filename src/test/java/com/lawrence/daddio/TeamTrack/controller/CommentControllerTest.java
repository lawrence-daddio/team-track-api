package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.config.SecurityConfig;
import com.lawrence.daddio.TeamTrack.dto.CommentDto;
import com.lawrence.daddio.TeamTrack.service.CommentService;
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

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService service;

    private static CommentDto comment(Long id, String body) {
        CommentDto dto = new CommentDto();
        dto.setId(id);
        dto.setTaskId(7L);
        dto.setEmployeeId(3L);
        dto.setBody(body);
        return dto;
    }

    @Test
    void getComment() throws Exception {
        when(service.getComment(1L)).thenReturn(comment(1L, "hello"));

        mockMvc.perform(get("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taskId").value(7))
                .andExpect(jsonPath("$.employeeId").value(3))
                .andExpect(jsonPath("$.body").value("hello"));
    }

    @Test
    void getComment_notFound() throws Exception {
        when(service.getComment(99L)).thenReturn(null);

        mockMvc.perform(get("/comments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getComments() throws Exception {
        when(service.getComments()).thenReturn(List.of(comment(1L, "a"), comment(2L, "b")));

        mockMvc.perform(get("/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].body").value("b"));
    }

    @Test
    void getComments_emptyReturnsNotFound() throws Exception {
        when(service.getComments()).thenReturn(List.of());

        mockMvc.perform(get("/comments"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommentsByTask() throws Exception {
        when(service.getCommentsByTask(7L)).thenReturn(List.of(comment(1L, "a")));

        mockMvc.perform(get("/comments/task/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskId").value(7));
    }


    @Test
    void getCommentsByTask_noneReturnsNotFound() throws Exception {
        when(service.getCommentsByTask(8L)).thenReturn(List.of());

        mockMvc.perform(get("/comments/task/8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment() throws Exception {
        when(service.createComment(any(CommentDto.class))).thenReturn(comment(5L, "new"));

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\":7,\"employeeId\":3,\"body\":\"new\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.body").value("new"));
    }

    @Test
    void createComment_missingTaskIdIsRejected() throws Exception {
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":3,\"body\":\"new\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void createComment_missingEmployeeIdIsRejected() throws Exception {
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\":7,\"body\":\"new\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void deleteComment() throws Exception {
        when(service.deleteComment(4L)).thenReturn(true);

        mockMvc.perform(delete("/comments/4"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteComment_notFound() throws Exception {
        when(service.deleteComment(99L)).thenReturn(false);

        mockMvc.perform(delete("/comments/99"))
                .andExpect(status().isNotFound());
    }
}
