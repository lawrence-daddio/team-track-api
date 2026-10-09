package com.lawrence.daddio.TeamTrack.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskUpdateDto {

    private Long projectId;

    private Long employeeId;

    @NotBlank
    private String title;

    @NotBlank
    private String status;

    @NotNull
    private LocalDateTime dueDate;
}
