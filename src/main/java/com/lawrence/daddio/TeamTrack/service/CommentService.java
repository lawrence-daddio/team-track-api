package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.CommentDto;
import com.lawrence.daddio.TeamTrack.entity.Comment;
import com.lawrence.daddio.TeamTrack.mapper.CommentMapper;
import com.lawrence.daddio.TeamTrack.repo.CommentRepository;
import com.lawrence.daddio.TeamTrack.repo.EmployeeRepository;
import com.lawrence.daddio.TeamTrack.repo.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
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

    public CommentDto getComment(long id) {
        Optional<Comment> comment = repository.findById(id);

        if (comment.isPresent()) {
            return mapper.commentToCommentDto(comment.get());
        }
        return null;
    }

    public List<CommentDto> getComments() {

        List<Comment> comments  = repository.findAll();

        if (comments != null && !comments.isEmpty()){
            return mapper.commentsToCommentDtos(comments);
        }

        return null;
    }

    public List<CommentDto> getCommentsByTask(long taskId) {
        Optional<List<Comment>> comments = repository.findByTaskId(taskId);

        if (comments.isPresent()) {
            List<CommentDto> commentDtos = mapper.commentsToCommentDtos(comments.get());
            return commentDtos;
        }
        return null;
    }

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

    public boolean deleteComment(long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
