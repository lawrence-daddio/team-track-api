package com.lawrence.daddio.TeamTrack.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeDto {

    private Long id;

    private String email;

    private String displayName;

    private List<Long> taskIds;

    private List<Long> teamMembershipIds;

    private List<Long> commentIds;

}
