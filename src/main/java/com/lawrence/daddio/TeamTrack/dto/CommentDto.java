package com.lawrence.daddio.TeamTrack.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CommentDto extends AuditableDto {

    private Long id;

    @NotNull
    private Long taskId;

    @NotNull
    private Long employeeId;

    @NotNull
    private String body;

}
