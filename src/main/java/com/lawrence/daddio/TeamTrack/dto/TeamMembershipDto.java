package com.lawrence.daddio.TeamTrack.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamMembershipDto {

    private Long id;

    private String role;

    @NotNull
    private Long employeeId;

    @NotNull
    private Long teamId;
}
