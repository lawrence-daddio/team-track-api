package com.lawrence.daddio.TeamTrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team_membership")
public class TeamMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "role")
    private String role;

    @ToString.Exclude
    @JoinColumn(name = "employee_id",  nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    @ToString.Exclude
    @JoinColumn(name = "team_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamMembership other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return TeamMembership.class.hashCode();
    }
}
