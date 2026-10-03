package com.lawrence.daddio.TeamTrack.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectDto {

    private Long id;

    private Long teamId;

    private String name;

    private String description;

    private List<Long> taskIds;

}
