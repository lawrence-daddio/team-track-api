package com.lawrence.daddio.TeamTrack.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskDto extends AuditableDto {

    private Long id;

    private Long projectId;

    private Long employeeId;

    private String title;

    private String status;

    private LocalDateTime dueDate;

    private List<Long> commentIds;
}
