package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.CommentDto;
import com.lawrence.daddio.TeamTrack.dto.CommentUpdateDto;
import com.lawrence.daddio.TeamTrack.entity.Comment;
import com.lawrence.daddio.TeamTrack.mapper.CommentMapper;
import com.lawrence.daddio.TeamTrack.repo.CommentRepository;
import com.lawrence.daddio.TeamTrack.repo.EmployeeRepository;
import com.lawrence.daddio.TeamTrack.repo.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private CommentRepository repository;
    private CommentMapper mapper;
    private TaskRepository taskRepository;
    private EmployeeRepository employeeRepository;

    public CommentService(CommentRepository repository, CommentMapper mapper,
                          TaskRepository taskRepository, EmployeeRepository employeeRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
    }

    public CommentDto getComment(Long id) {
        Comment comment = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Comment not found: " + id));

        return mapper.commentToCommentDto(comment);
    }

    public List<CommentDto> getComments() {

        List<Comment> comments  = repository.findAll();

        if (!comments.isEmpty()){
            return mapper.commentsToCommentDtos(comments);
        }

        return Collections.emptyList();
    }

    public List<CommentDto> getCommentsByTask(Long taskId) {
        Optional<List<Comment>> comments = repository.findByTaskId(taskId);

        if (comments.isPresent()) {
            List<CommentDto> commentDtos = mapper.commentsToCommentDtos(comments.get());
            return commentDtos;
        }
        return Collections.emptyList();
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {

        Comment comment = mapper.commentDtoToComment(commentDto);
        comment.setTask(taskRepository.findById(commentDto.getTaskId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task not found: " + commentDto.getTaskId())));
        comment.setEmployee(employeeRepository.findById(commentDto.getEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Employee not found: " + commentDto.getEmployeeId())));
        comment.setCreatedAt(Instant.now());
        Comment createdComment = repository.save(comment);

        return mapper.commentToCommentDto(createdComment);
    }

    @Transactional
    public CommentDto updateComment(Long id, CommentUpdateDto updateDto) {
        Comment comment = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Comment not found: " + id));

        comment.setBody(updateDto.getBody());

        return mapper.commentToCommentDto(repository.save(comment));
    }

    @Transactional
    public void deleteComment(Long id) {
        repository.deleteById(id);
    }


}
