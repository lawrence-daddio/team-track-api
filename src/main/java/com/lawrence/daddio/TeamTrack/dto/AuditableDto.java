package com.lawrence.daddio.TeamTrack.dto;

import lombok.Data;

import java.time.Instant;

@Data
public abstract class AuditableDto {

    private Instant createdTs;

    private Instant updateTs;
}
