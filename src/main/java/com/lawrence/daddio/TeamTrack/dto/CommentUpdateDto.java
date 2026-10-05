package com.lawrence.daddio.TeamTrack.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentUpdateDto {

    @NotNull
    private String body;
}
