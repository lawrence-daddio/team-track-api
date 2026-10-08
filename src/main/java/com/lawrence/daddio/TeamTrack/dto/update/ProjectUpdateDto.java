package com.lawrence.daddio.TeamTrack.dto.update;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectUpdateDto {

    private Long teamId;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

}
