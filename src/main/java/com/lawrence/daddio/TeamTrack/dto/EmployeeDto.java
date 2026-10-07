package com.lawrence.daddio.TeamTrack.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmployeeDto extends AuditableDto {

    private Long id;

    @NotBlank
    @Email
    private String email;

    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank
    private String displayName;

    private List<Long> taskIds;

    private List<Long> teamMembershipIds;

    private List<Long> commentIds;

}
