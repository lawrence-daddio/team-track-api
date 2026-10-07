package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.EmployeeDto;
import com.lawrence.daddio.TeamTrack.dto.EmployeeUpdateDto;
import com.lawrence.daddio.TeamTrack.entity.Employee;
import com.lawrence.daddio.TeamTrack.mapper.EmployeeMapper;
import com.lawrence.daddio.TeamTrack.repo.CommentRepository;
import com.lawrence.daddio.TeamTrack.repo.EmployeeRepository;
import com.lawrence.daddio.TeamTrack.repo.TaskRepository;
import com.lawrence.daddio.TeamTrack.repo.TeamMembershipRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;
    private EmployeeMapper mapper;
    private PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper mapper,  PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public EmployeeDto getEmployees(String email) {
        Optional<Employee> employee = employeeRepository.findByEmail(email);

        if (employee.isPresent()){
            return mapper.employeeToEmployeeDto(employee.get());
        }
        return null;
    }

    public List<EmployeeDto> getEmployees() {
        List<Employee> employees  = employeeRepository.findAll();

        if (!employees.isEmpty()){
            return mapper.employeeToEmployeeDtos(employees);
        }
        return Collections.emptyList();
    }

    public EmployeeDto createEmployee(@RequestBody EmployeeDto employeeDto) {
        employeeDto.setId(null); //let db handle it
        Employee newEmployee = mapper.employeeDtoToEmployee(employeeDto);

        if (employeeDto.getPassword() != null && !employeeDto.getPassword().isBlank()) {
            newEmployee.setPasswordHash(passwordEncoder.encode(employeeDto.getPassword()));
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty or null");
        }

        Employee createdEmployee = employeeRepository.save(newEmployee);
        return mapper.employeeToEmployeeDto(createdEmployee);
    }


    public EmployeeDto updateEmployee(Long id, EmployeeUpdateDto employeeUpdateDto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        employee.setEmail(employeeUpdateDto.getEmail());
        employee.setDisplayName(employeeUpdateDto.getDisplayName());

        if (employeeUpdateDto.getPassword() != null && !employeeUpdateDto.getPassword().isBlank()) {
            employee.setPasswordHash(passwordEncoder.encode(employeeUpdateDto.getPassword()));
        }

        return mapper.employeeToEmployeeDto(employeeRepository.save(employee));
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

}
