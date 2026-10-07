package com.lawrence.daddio.TeamTrack.mapper;

import com.lawrence.daddio.TeamTrack.dto.EmployeeDto;
import com.lawrence.daddio.TeamTrack.entity.Comment;
import com.lawrence.daddio.TeamTrack.entity.Employee;
import com.lawrence.daddio.TeamTrack.entity.Task;
import com.lawrence.daddio.TeamTrack.entity.TeamMembership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EmployeeMapper {

    @Mapping(target = "createdTs", ignore = true)
    @Mapping(target = "updateTs", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "teamMemberships", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    Employee employeeDtoToEmployee(EmployeeDto employeeDto);

    @Mapping(target = "taskIds", source = "tasks")
    @Mapping(target = "commentIds", source = "comments")
    @Mapping(target = "teamMembershipIds", source = "teamMemberships")
    @Mapping(target = "password",  ignore = true)
    EmployeeDto employeeToEmployeeDto(Employee employee);

    List<EmployeeDto> employeeToEmployeeDtos(List<Employee> employees);

    List<Employee> employeeDtoToEmployees(List<EmployeeDto> employeeDtos);

    default Long taskToId(Task task) {
        return task.getId();
    }

    default Long commentToId(Comment comment) {
        return comment.getId();
    }

    default Long teamMembershipToId(TeamMembership teamMembership) {
        return teamMembership.getId();
    }

}
