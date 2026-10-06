package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.config.SecurityConfig;
import com.lawrence.daddio.TeamTrack.dto.CommentDto;
import com.lawrence.daddio.TeamTrack.dto.CommentUpdateDto;
import com.lawrence.daddio.TeamTrack.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    void getCommentById() throws Exception {
        when(service.getCommentById(1L)).thenReturn(comment(1L, "hello"));

        mockMvc.perform(get("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taskId").value(7))
                .andExpect(jsonPath("$.employeeId").value(3))
                .andExpect(jsonPath("$.body").value("hello"));
    }

    @Test
    void getComment_ById_notFound() throws Exception {
        when(service.getCommentById(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found: 99"));

        mockMvc.perform(get("/comments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getComment_ById_nullReturnsNotFound() throws Exception {
        when(service.getCommentById(99L)).thenReturn(null);

        mockMvc.perform(get("/comments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommentsById() throws Exception {
        when(service.getComments()).thenReturn(List.of(comment(1L, "a"), comment(2L, "b")));

        mockMvc.perform(get("/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].body").value("b"));
    }

    @Test
    void getComments_emptyReturnsNotFoundById() throws Exception {
        when(service.getComments()).thenReturn(List.of());

        mockMvc.perform(get("/comments"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommentsByTaskById() throws Exception {
        when(service.getCommentsByTask(7L)).thenReturn(List.of(comment(1L, "a")));

        mockMvc.perform(get("/comments/task/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskId").value(7));
    }


    @Test
    void getCommentsByTask_noneReturnsNotFoundById() throws Exception {
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
    void updateComment() throws Exception {
        when(service.updateComment(eq(5L), any(CommentUpdateDto.class))).thenReturn(comment(5L, "edited"));

        mockMvc.perform(put("/comments/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"edited\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.body").value("edited"));
    }

    @Test
    void updateComment_notFound() throws Exception {
        when(service.updateComment(eq(99L), any(CommentUpdateDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found: 99"));

        mockMvc.perform(put("/comments/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"edited\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_missingBodyIsRejected() throws Exception {
        mockMvc.perform(put("/comments/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void deleteComment() throws Exception {
        doNothing().when(service).deleteComment(anyLong());

        mockMvc.perform(delete("/comments/4"))
                .andExpect(status().isNoContent());
    }
}
