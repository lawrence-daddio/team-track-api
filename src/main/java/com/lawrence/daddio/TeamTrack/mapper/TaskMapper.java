package com.lawrence.daddio.TeamTrack.mapper;

import com.lawrence.daddio.TeamTrack.dto.TaskDto;
import com.lawrence.daddio.TeamTrack.entity.Comment;
import com.lawrence.daddio.TeamTrack.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TaskMapper {

    @Mapping(target = "createdTs", ignore = true)
    @Mapping(target = "updateTs", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Task TaskDtoToTask(TaskDto taskDto);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "commentIds", source = "comments")
    TaskDto TaskToTaskDto(Task task);

    List<TaskDto> TaskToTaskDtoList(List<Task> tasks);

    List<Task> TaskDtoToTaskList(List<TaskDto> taskDtos);

    default Long commentToId(Comment comment) {
        return comment.getId();
    }
}
