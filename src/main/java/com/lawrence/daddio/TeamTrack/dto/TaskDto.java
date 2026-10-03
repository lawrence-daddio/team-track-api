package com.lawrence.daddio.TeamTrack.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDto {

    private Long id;

    private Long projectId;

    private Long employeeId;

    private String title;

    private String status;

    private LocalDateTime dueDate;

    private List<Long> commentIds;
}
