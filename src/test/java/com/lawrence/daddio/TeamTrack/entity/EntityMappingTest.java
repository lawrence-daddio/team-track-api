package com.lawrence.daddio.TeamTrack.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class EntityMappingTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Autowired
    private TestEntityManager em;

    private Team persistTeamWithProject() {
        Team team = new Team();
        team.setName("Alpha");
        Project project = new Project();
        project.setName("Apollo");
        project.setDescription("Moon landing");
        project.setTeam(team);
        team.getProjects().add(project);
        em.persist(team);
        em.flush();
        em.clear();
        return team;
    }

    // ---- serialization ----

    // Entities are exposed through DTOs, so only the password-hash guard is kept here.
    @Test
    void employeePasswordHashIsNeverSerialized() {
        Employee employee = new Employee();
        employee.setEmail("a@b.com");
        employee.setDisplayName("Ann");
        employee.setPasswordHash("secret-hash");
        em.persistAndFlush(employee);
        em.clear();

        Employee proxy = em.getEntityManager().getReference(Employee.class, employee.getId());
        String json = jsonMapper.writeValueAsString(proxy);

        assertThat(json)
                .contains("a@b.com")
                .doesNotContain("passwordHash")
                .doesNotContain("secret-hash");
    }

    // ---- equals / hashCode ----

    @Test
    void unsavedEntitiesAreOnlyEqualToThemselves() {
        Team a = new Team();
        Team b = new Team();

        assertThat(a).isEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void entitiesWithSameIdAreEqual() {
        Team a = new Team();
        a.setId(1L);
        a.setName("One");
        Team b = new Team();
        b.setId(1L);
        b.setName("Different name");
        Team c = new Team();
        c.setId(2L);

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(c);
    }

    @Test
    void proxyIsEqualToRealEntityWithSameId() {
        Team saved = persistTeamWithProject();

        Team proxy = em.getEntityManager().getReference(Team.class, saved.getId());
        Team real = new Team();
        real.setId(saved.getId());

        assertThat(proxy).isEqualTo(real);
        assertThat(real).isEqualTo(proxy);
    }

    @Test
    void hashCodeIsStableAcrossPersist() {
        Team team = new Team();
        team.setName("Stable");
        Set<Team> set = new HashSet<>();
        set.add(team);
        int before = team.hashCode();

        em.persistAndFlush(team);

        assertThat(team.hashCode()).isEqualTo(before);
        assertThat(set).contains(team);
    }

    // ---- toString ----

    @Test
    void toStringDoesNotRecurseOrTouchLazyRelations() {
        Team saved = persistTeamWithProject();
        Team loaded = em.find(Team.class, saved.getId());
        Project project = loaded.getProjects().get(0);

        assertThat(loaded.toString()).contains("Alpha").doesNotContain("projects");
        assertThat(project.toString()).contains("Apollo").doesNotContain("team=");
    }

    @Test
    void employeeToStringOmitsPasswordHash() {
        Employee employee = new Employee();
        employee.setEmail("a@b.com");
        employee.setPasswordHash("secret-hash");

        assertThat(employee.toString()).contains("a@b.com").doesNotContain("secret-hash");
    }
}
