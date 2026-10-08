package com.lawrence.daddio.TeamTrack.dto.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
public class EmployeeUpdateDto {

    @NotBlank
    @Email
    private String email;

    @ToString.Exclude
    private String password;

    @NotBlank
    private String displayName;

}
