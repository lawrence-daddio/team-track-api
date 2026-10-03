package com.lawrence.daddio.TeamTrack.mapper;

import com.lawrence.daddio.TeamTrack.dto.ProjectDto;
import com.lawrence.daddio.TeamTrack.entity.Project;
import com.lawrence.daddio.TeamTrack.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProjectMapper {

    @Mapping(target = "team", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Project projectDtoToProject(ProjectDto projectDto);

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "taskIds", source = "tasks")
    ProjectDto projectToProjectDto(Project project);

    List<ProjectDto> projectsToProjectDtos(List<Project> projects);

    List<Project> projectsToProjects(List<ProjectDto> projectDtos);

    default Long taskIdToId(Task task) {
        return task.getId();
    }
}
