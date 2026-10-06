package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.dto.CommentDto;
import com.lawrence.daddio.TeamTrack.dto.CommentUpdateDto;
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
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {

        log.info("Getting comment for id {}", id);
        CommentDto commentDto = service.getCommentById(id);

        if (commentDto == null) {
            log.debug("Comment not found for id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments() {

        log.info("Getting all comments");
        List<CommentDto> commentDtos = service.getComments();

        if (commentDtos.isEmpty()) {
            log.debug("Comments not found");
            return new ResponseEntity<>(commentDtos, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(commentDtos, HttpStatus.OK);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentDto>> getCommentsByTask(@PathVariable Long taskId) {

        log.info("Getting all comments for task {}", taskId);
        List<CommentDto> comments = service.getCommentsByTask(taskId);

        if (comments.isEmpty()) {
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

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id,
                                                    @Valid @RequestBody CommentUpdateDto updateDto) {
        log.info("Updating comment for id: {}", id);
        CommentDto updatedCommentDto = service.updateComment(id, updateDto);

        return new ResponseEntity<>(updatedCommentDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {

        log.info("Deleting comment for id {}", id);
        service.deleteComment(id);

        return ResponseEntity.noContent().build();
    }

}
