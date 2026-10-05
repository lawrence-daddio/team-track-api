package com.lawrence.daddio.TeamTrack.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class CommentDto {

    private Long id;

    @NotNull
    private Long taskId;

    @NotNull
    private Long employeeId;

    @NotNull
    private String body;

    private Instant createdAt;
}
