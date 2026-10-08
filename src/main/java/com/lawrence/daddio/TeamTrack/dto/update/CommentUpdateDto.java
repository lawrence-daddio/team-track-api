package com.lawrence.daddio.TeamTrack.dto.update;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentUpdateDto {

    @NotBlank
    private String body;
}
