package com.lawrence.daddio.TeamTrack.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TeamMembershipDto extends AuditableDto {

    private Long id;

    private String role;

    @NotNull
    private Long employeeId;

    @NotNull
    private Long teamId;
}
