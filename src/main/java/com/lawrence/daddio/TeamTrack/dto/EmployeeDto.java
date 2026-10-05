package com.lawrence.daddio.TeamTrack.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmployeeDto extends AuditableDto {

    private Long id;

    private String email;

    private String displayName;

    private List<Long> taskIds;

    private List<Long> teamMembershipIds;

    private List<Long> commentIds;

}
