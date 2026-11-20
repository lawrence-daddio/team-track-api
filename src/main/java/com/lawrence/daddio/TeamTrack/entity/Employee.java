package com.lawrence.daddio.TeamTrack.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee")
public class Employee extends Auditable implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    @Email
    @Column(name = "email",  nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    @Column(name = "password_hash")
    private String passwordHash;

    @NotBlank
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @ToString.Exclude
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "employee",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMembership> teamMemberships = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "employee",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Employee.class.hashCode();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
