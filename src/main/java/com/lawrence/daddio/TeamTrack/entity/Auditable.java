package com.lawrence.daddio.TeamTrack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class Auditable {

    // columnDefinition defaults cover rows inserted by team_track.sql, which bypasses Hibernate
    @CreationTimestamp
    @Column(name = "created_ts", updatable = false,
            columnDefinition = "timestamp(6) with time zone default current_timestamp")
    private Instant createdTs;

    @UpdateTimestamp
    @Column(name = "update_ts",
            columnDefinition = "timestamp(6) with time zone default current_timestamp")
    private Instant updateTs;
}
