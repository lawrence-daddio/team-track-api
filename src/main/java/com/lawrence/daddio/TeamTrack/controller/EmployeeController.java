package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.dto.EmployeeDto;
import com.lawrence.daddio.TeamTrack.entity.Employee;
import com.lawrence.daddio.TeamTrack.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@Slf4j
public class EmployeeController {

    private EmployeeService service;

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
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employee) {

        log.info("creating employee {}", employee);
        EmployeeDto createdEmployeeDto = service.createEmployee(employee);

        return new ResponseEntity<>(createdEmployeeDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") long id) {

        log.info("deleting employee with id {}", id);
        boolean deleted = service.deleteEmployee(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }


}
