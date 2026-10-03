package com.lawrence.daddio.TeamTrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TeamDto {

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    private List<Long> projectIds;

    private List<Long> teamMembershipIds;
}
