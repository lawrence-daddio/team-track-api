package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.ProjectDto;
import com.lawrence.daddio.TeamTrack.dto.update.ProjectUpdateDto;
import com.lawrence.daddio.TeamTrack.entity.Project;
import com.lawrence.daddio.TeamTrack.entity.Team;
import com.lawrence.daddio.TeamTrack.mapper.ProjectMapper;
import com.lawrence.daddio.TeamTrack.repo.ProjectRepository;
import com.lawrence.daddio.TeamTrack.repo.TaskRepository;
import com.lawrence.daddio.TeamTrack.repo.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ProjectMapper mapper;

    @InjectMocks
    private ProjectService service;

    private static Team team(Long id) {
        Team team = new Team();
        team.setId(id);
        return team;
    }

    private static Project project(Long id, Team team) {
        Project project = new Project();
        project.setId(id);
        project.setName("Old name");
        project.setDescription("Old description");
        project.setTeam(team);
        return project;
    }

    private static ProjectUpdateDto updateDto(Long teamId) {
        ProjectUpdateDto dto = new ProjectUpdateDto();
        dto.setTeamId(teamId);
        dto.setName("New name");
        dto.setDescription("New description");
        return dto;
    }

    // ---- updateProject ----

    @Test
    void updateProject_projectNotFound_throws404() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.updateProject(1L, updateDto(2L)));

        assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProject_sameTeam_updatesFieldsWithoutTeamLookup() {
        Project existing = project(1L, team(5L));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).then(returnsFirstArg());
        when(mapper.projectToProjectDto(any(Project.class))).thenReturn(new ProjectDto());

        service.updateProject(1L, updateDto(5L));

        assertThat(existing.getName()).isEqualTo("New name");
        assertThat(existing.getDescription()).isEqualTo("New description");
        assertThat(existing.getTeam().getId()).isEqualTo(5L);
        verify(teamRepository, never()).findById(any());
    }

    @Test
    void updateProject_differentTeam_assignsNewTeam() {
        Project existing = project(1L, team(5L));
        Team newTeam = team(6L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(teamRepository.findById(6L)).thenReturn(Optional.of(newTeam));
        when(projectRepository.save(any(Project.class))).then(returnsFirstArg());
        when(mapper.projectToProjectDto(any(Project.class))).thenReturn(new ProjectDto());

        service.updateProject(1L, updateDto(6L));

        assertThat(existing.getTeam()).isSameAs(newTeam);
        // the old team's id must not be mutated
        assertThat(newTeam.getId()).isEqualTo(6L);
    }

    @Test
    void updateProject_differentTeamNotFound_throws404WithRequestedId() {
        Project existing = project(1L, team(5L));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.updateProject(1L, updateDto(99L)));

        assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(e.getReason()).contains("99");
        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProject_projectHasNoTeam_assignsTeamWithoutNpe() {
        Project existing = project(1L, null);
        Team newTeam = team(6L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(teamRepository.findById(6L)).thenReturn(Optional.of(newTeam));
        when(projectRepository.save(any(Project.class))).then(returnsFirstArg());
        when(mapper.projectToProjectDto(any(Project.class))).thenReturn(new ProjectDto());

        service.updateProject(1L, updateDto(6L));

        assertThat(existing.getTeam()).isSameAs(newTeam);
    }

    @Test
    void updateProject_nullTeamId_leavesTeamUnchanged() {
        Team currentTeam = team(5L);
        Project existing = project(1L, currentTeam);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).then(returnsFirstArg());
        when(mapper.projectToProjectDto(any(Project.class))).thenReturn(new ProjectDto());

        service.updateProject(1L, updateDto(null));

        assertThat(existing.getTeam()).isSameAs(currentTeam);
        assertThat(existing.getName()).isEqualTo("New name");
        verify(teamRepository, never()).findById(any());
    }
}
