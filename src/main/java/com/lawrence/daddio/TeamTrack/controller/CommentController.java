package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.dto.CommentDto;
import com.lawrence.daddio.TeamTrack.service.CommentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@Slf4j
public class CommentController {

    private CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable("id") long id) {

        log.info("Getting comment for id {}", id);
        CommentDto commentDto = service.getComment(id);

        if (commentDto == null) {
            log.debug("comment not found for id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments() {

        log.info("Getting all comments");
        List<CommentDto> commentDtos = service.getComments();

        if (commentDtos == null || commentDtos.isEmpty()) {
            log.debug("comment not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(commentDtos, HttpStatus.OK);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentDto>> getCommentsByTask(@PathVariable("taskId") long taskId) {

        log.info("Getting all comments for task {}", taskId);
        List<CommentDto> comments = service.getCommentsByTask(taskId);

        if (comments == null || comments.isEmpty()) {
            log.debug("comments not found for task {}", taskId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto) {

        log.info("Creating comment {}", commentDto);
        CommentDto createdCommentDto = service.createComment(commentDto);

        return new ResponseEntity<>(createdCommentDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") long id) {

        log.info("Deleting comment for id {}", id);
        boolean deleted = service.deleteComment(id);

        if (!deleted) {
            log.debug("Comment not found for id {}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }


}
