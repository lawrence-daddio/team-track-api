package com.lawrence.daddio.TeamTrack.dto.update;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeamMembershipUpdateDto {

    @NotBlank
    private String role;
}
