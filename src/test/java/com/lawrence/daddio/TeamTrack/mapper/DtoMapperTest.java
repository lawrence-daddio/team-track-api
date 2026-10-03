package com.lawrence.daddio.TeamTrack.mapper;

import com.lawrence.daddio.TeamTrack.dto.ProjectDto;
import com.lawrence.daddio.TeamTrack.dto.TaskDto;
import com.lawrence.daddio.TeamTrack.dto.TeamMembershipDto;
import com.lawrence.daddio.TeamTrack.entity.Comment;
import com.lawrence.daddio.TeamTrack.entity.Employee;
import com.lawrence.daddio.TeamTrack.entity.Project;
import com.lawrence.daddio.TeamTrack.entity.Task;
import com.lawrence.daddio.TeamTrack.entity.Team;
import com.lawrence.daddio.TeamTrack.entity.TeamMembership;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoMapperTest {

    private final ProjectMapper projectMapper = new ProjectMapperImpl();
    private final TaskMapper taskMapper = new TaskMapperImpl();
    private final TeamMembershipMapper membershipMapper = new TeamMembershipMapperImpl();

    private static Team team(long id) {
        Team team = new Team();
        team.setId(id);
        return team;
    }

    private static Employee employee(long id) {
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }

    @Test
    void projectToDtoFlattensRelationsToIds() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Apollo");
        project.setTeam(team(7L));
        Task task = new Task();
        task.setId(50L);
        project.getTasks().add(task);

        ProjectDto dto = projectMapper.projectToProjectDto(project);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Apollo");
        assertThat(dto.getTeamId()).isEqualTo(7L);
        assertThat(dto.getTaskIds()).containsExactly(50L);
    }

    @Test
    void projectWithoutTeamMapsToNullTeamId() {
        Project project = new Project();
        project.setName("Orphan");

        assertThat(projectMapper.projectToProjectDto(project).getTeamId()).isNull();
    }

    @Test
    void projectDtoToEntityLeavesRelationsForServiceToResolve() {
        ProjectDto dto = new ProjectDto();
        dto.setId(1L);
        dto.setName("Apollo");
        dto.setTeamId(7L);
        dto.setTaskIds(java.util.List.of(50L));

        Project project = projectMapper.projectDtoToProject(dto);

        assertThat(project.getName()).isEqualTo("Apollo");
        assertThat(project.getTeam()).isNull();
        assertThat(project.getTasks()).isEmpty();
    }

    @Test
    void taskToDtoFlattensRelationsToIds() {
        Project project = new Project();
        project.setId(1L);
        Task task = new Task();
        task.setId(2L);
        task.setTitle("Write docs");
        task.setProject(project);
        task.setEmployee(employee(3L));
        Comment comment = new Comment();
        comment.setId(8L);
        task.getComments().add(comment);

        TaskDto dto = taskMapper.TaskToTaskDto(task);

        assertThat(dto.getProjectId()).isEqualTo(1L);
        assertThat(dto.getEmployeeId()).isEqualTo(3L);
        assertThat(dto.getCommentIds()).containsExactly(8L);
        assertThat(dto.getTitle()).isEqualTo("Write docs");
    }

    @Test
    void taskWithoutAssigneeMapsToNullEmployeeId() {
        Task task = new Task();
        task.setTitle("Unassigned");

        TaskDto dto = taskMapper.TaskToTaskDto(task);

        assertThat(dto.getEmployeeId()).isNull();
        assertThat(dto.getProjectId()).isNull();
    }

    @Test
    void taskDtoToEntityLeavesRelationsForServiceToResolve() {
        TaskDto dto = new TaskDto();
        dto.setTitle("Write docs");
        dto.setProjectId(1L);
        dto.setEmployeeId(3L);

        Task task = taskMapper.TaskDtoToTask(dto);

        assertThat(task.getTitle()).isEqualTo("Write docs");
        assertThat(task.getProject()).isNull();
        assertThat(task.getEmployee()).isNull();
    }

    @Test
    void membershipToDtoFlattensRelationsToIds() {
        TeamMembership membership = new TeamMembership();
        membership.setId(4L);
        membership.setRole("LEAD");
        membership.setEmployee(employee(3L));
        membership.setTeam(team(1L));

        TeamMembershipDto dto = membershipMapper.TeamMembershipToTeamMembershipDto(membership);

        assertThat(dto.getRole()).isEqualTo("LEAD");
        assertThat(dto.getEmployeeId()).isEqualTo(3L);
        assertThat(dto.getTeamId()).isEqualTo(1L);
    }

    @Test
    void membershipDtoToEntityLeavesRelationsForServiceToResolve() {
        TeamMembershipDto dto = new TeamMembershipDto();
        dto.setRole("DEV");
        dto.setEmployeeId(3L);
        dto.setTeamId(1L);

        TeamMembership membership = membershipMapper.TeamMembershipDtoToTeamMembership(dto);

        assertThat(membership.getRole()).isEqualTo("DEV");
        assertThat(membership.getEmployee()).isNull();
        assertThat(membership.getTeam()).isNull();
    }
}
