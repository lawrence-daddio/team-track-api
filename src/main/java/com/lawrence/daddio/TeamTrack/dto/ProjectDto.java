package com.lawrence.daddio.TeamTrack.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectDto extends AuditableDto {

    private Long id;

    private Long teamId;

    private String name;

    private String description;

    private List<Long> taskIds;

}
