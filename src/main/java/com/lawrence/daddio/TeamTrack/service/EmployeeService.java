package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.EmployeeDto;
import com.lawrence.daddio.TeamTrack.entity.Employee;
import com.lawrence.daddio.TeamTrack.entity.TeamMembership;
import com.lawrence.daddio.TeamTrack.mapper.EmployeeMapper;
import com.lawrence.daddio.TeamTrack.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;
    private TaskRepository taskRepository;
    private CommentRepository commentRepository;
    private TeamMembershipRepository teamMembershipRepository;
    private EmployeeMapper mapper;

    public EmployeeService(EmployeeRepository employeeRepository, TaskRepository taskRepository, CommentRepository commentRepository,
                           TeamMembershipRepository teamMembershipRepository, EmployeeMapper mapper) {
        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.mapper = mapper;
    }

    public EmployeeDto getEmployees(String email) {
        Optional<Employee> employee = employeeRepository.findByEmail(email);

        if (employee.isPresent()) {
            return mapper.employeeToEmployeeDto(employee.get());
        }
        return null;
    }

    public List<EmployeeDto> getEmployees() {
        List<Employee> employees  = employeeRepository.findAll();

        if (employees != null && !employees.isEmpty()){
            return mapper.employeeToEmployeeDtos(employees);
        }

        return null;
    }

    public EmployeeDto createEmployee(@RequestBody EmployeeDto employeeDto) {

        Employee newEmployee = mapper.employeeDtoToEmployee(employeeDto);
        newEmployee.setComments(commentRepository.findAll());
        newEmployee.setTasks(taskRepository.findAll());
        newEmployee.setTeamMemberships(teamMembershipRepository.findAll());
        Employee createdEmployee = employeeRepository.save(newEmployee);
        return mapper.employeeToEmployeeDto(createdEmployee);
    }

    public boolean deleteEmployee(long id) {
        if (!employeeRepository.existsById(id)) {
            return false;
        }
        employeeRepository.deleteById(id);
        return true;
    }
}
