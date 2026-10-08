package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.dto.EmployeeDto;
import com.lawrence.daddio.TeamTrack.dto.update.EmployeeUpdateDto;
import com.lawrence.daddio.TeamTrack.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@Slf4j
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/{email}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable("email") String email) {

        log.info("getting employee by email {}", email);
        EmployeeDto employeeDto = service.getEmployees(email);

        if (employeeDto == null) {
            log.debug("Employee not found for email {}", email);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(employeeDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getEmployees() {

        log.info("getting all employees");
        List<EmployeeDto> employeeDtos = service.getEmployees();

        if (employeeDtos == null) {
            log.debug("Employee not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(employeeDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {

        log.info("creating employee {}", employeeDto);
        EmployeeDto createdEmployeeDto = service.createEmployee(employeeDto);

        return new ResponseEntity<>(createdEmployeeDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeUpdateDto employeeUpdateDto) {

        log.info("updating employee {}", employeeUpdateDto);
        EmployeeDto updateEmployeeDto = service.updateEmployee(id, employeeUpdateDto);
        return new ResponseEntity<>(updateEmployeeDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {

        log.info("deleting employee with id {}", id);
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }


}
