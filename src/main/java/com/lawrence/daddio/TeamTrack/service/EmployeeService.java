package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.EmployeeDto;
import com.lawrence.daddio.TeamTrack.dto.EmployeeUpdateDto;
import com.lawrence.daddio.TeamTrack.entity.Employee;
import com.lawrence.daddio.TeamTrack.mapper.EmployeeMapper;
import com.lawrence.daddio.TeamTrack.repo.EmployeeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
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

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public EmployeeDto getEmployees(String email) {
        Optional<Employee> employee = employeeRepository.findByEmail(normalizeEmail(email));

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

    @Transactional
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        employeeDto.setId(null); //let db handle it
        employeeDto.setEmail(normalizeEmail(employeeDto.getEmail()));

        //prevent duplicate emails and enforces unique emails
        if(employeeRepository.findByEmail(employeeDto.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists, choose a unique email address");
        }

        Employee newEmployee = mapper.employeeDtoToEmployee(employeeDto);

        if (employeeDto.getPassword() != null && !employeeDto.getPassword().isBlank()) {
            newEmployee.setPasswordHash(passwordEncoder.encode(employeeDto.getPassword()));
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty or null");
        }

        Employee createdEmployee = saveAndFlush(newEmployee);
        return mapper.employeeToEmployeeDto(createdEmployee);
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeUpdateDto employeeUpdateDto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String email = normalizeEmail(employeeUpdateDto.getEmail());

        //changing email
        if(!employee.getEmail().equals(email)){
            if(employeeRepository.findByEmail(email).isPresent()){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists, choose a unique email address");
            }
        }
        //saves if email is unchanged or does not exist already in the database
        employee.setEmail(email);
        employee.setDisplayName(employeeUpdateDto.getDisplayName());

        if (employeeUpdateDto.getPassword() != null && !employeeUpdateDto.getPassword().isBlank()) {
            employee.setPasswordHash(passwordEncoder.encode(employeeUpdateDto.getPassword()));
        }

        return mapper.employeeToEmployeeDto(saveAndFlush(employee));
    }

    // Flush immediately so a unique-email violation from a concurrent request surfaces here as a 409
    private Employee saveAndFlush(Employee employee) {
        try {
            return employeeRepository.saveAndFlush(employee);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists, choose a unique email address");
        }
    }

    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

}
