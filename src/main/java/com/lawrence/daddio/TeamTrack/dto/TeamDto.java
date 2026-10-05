package com.lawrence.daddio.TeamTrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TeamDto extends AuditableDto {

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    private List<Long> projectIds;

    private List<Long> teamMembershipIds;
}
