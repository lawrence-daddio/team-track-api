package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.dto.EmployeeDto;
import com.lawrence.daddio.TeamTrack.dto.update.EmployeeUpdateDto;
import com.lawrence.daddio.TeamTrack.entity.Employee;
import com.lawrence.daddio.TeamTrack.mapper.EmployeeMapper;
import com.lawrence.daddio.TeamTrack.repo.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService service;

    private static EmployeeDto createDto(String email) {
        EmployeeDto dto = new EmployeeDto();
        dto.setEmail(email);
        dto.setPassword("pw");
        dto.setDisplayName("Ann");
        return dto;
    }

    private static EmployeeUpdateDto updateDto(String email) {
        EmployeeUpdateDto dto = new EmployeeUpdateDto();
        dto.setEmail(email);
        dto.setDisplayName("Ann");
        return dto;
    }

    private static Employee employeeWithEmail(String email) {
        Employee employee = new Employee();
        employee.setEmail(email);
        return employee;
    }

    private static void assertConflict(ResponseStatusException e) {
        assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    // ---- createEmployee ----

    @Test
    void createEmployee_duplicateEmail_conflict() {
        when(employeeRepository.findByEmail("ann@x.com")).thenReturn(Optional.of(employeeWithEmail("ann@x.com")));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.createEmployee(createDto("ann@x.com")));

        assertConflict(e);
        verify(employeeRepository, never()).saveAndFlush(any());
    }

    @Test
    void createEmployee_duplicateEmailDifferentCase_conflict() {
        // the lookup must use the trimmed, lowercased email
        when(employeeRepository.findByEmail("ann@x.com")).thenReturn(Optional.of(employeeWithEmail("ann@x.com")));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.createEmployee(createDto("  Ann@X.com ")));

        assertConflict(e);
        verify(employeeRepository, never()).saveAndFlush(any());
    }

    @Test
    void createEmployee_uniqueConstraintViolationOnSave_conflict() {
        // two requests both pass the findByEmail check; the DB constraint rejects the second
        when(employeeRepository.findByEmail("ann@x.com")).thenReturn(Optional.empty());
        when(mapper.employeeDtoToEmployee(any(EmployeeDto.class))).thenReturn(new Employee());
        when(passwordEncoder.encode("pw")).thenReturn("hash");
        when(employeeRepository.saveAndFlush(any(Employee.class))).thenThrow(new DataIntegrityViolationException("unique"));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.createEmployee(createDto("ann@x.com")));

        assertConflict(e);
    }

    // ---- updateEmployee ----

    @Test
    void updateEmployee_emailTakenByAnotherEmployee_conflict() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employeeWithEmail("ann@x.com")));
        when(employeeRepository.findByEmail("bob@x.com")).thenReturn(Optional.of(employeeWithEmail("bob@x.com")));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.updateEmployee(1L, updateDto("bob@x.com")));

        assertConflict(e);
        verify(employeeRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateEmployee_emailTakenDifferentCase_conflict() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employeeWithEmail("ann@x.com")));
        when(employeeRepository.findByEmail("bob@x.com")).thenReturn(Optional.of(employeeWithEmail("bob@x.com")));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.updateEmployee(1L, updateDto("BOB@x.com")));

        assertConflict(e);
        verify(employeeRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateEmployee_sameEmailDifferentCase_succeeds() {
        // keeping your own email (just typed in another case) is not a duplicate
        Employee existing = employeeWithEmail("ann@x.com");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.saveAndFlush(existing)).thenReturn(existing);
        when(mapper.employeeToEmployeeDto(existing)).thenReturn(new EmployeeDto());

        service.updateEmployee(1L, updateDto("ANN@x.com"));

        verify(employeeRepository, never()).findByEmail(any());
        assertThat(existing.getEmail()).isEqualTo("ann@x.com");
    }

    @Test
    void updateEmployee_uniqueConstraintViolationOnSave_conflict() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employeeWithEmail("ann@x.com")));
        when(employeeRepository.findByEmail("bob@x.com")).thenReturn(Optional.empty());
        when(employeeRepository.saveAndFlush(any(Employee.class))).thenThrow(new DataIntegrityViolationException("unique"));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.updateEmployee(1L, updateDto("bob@x.com")));

        assertConflict(e);
    }
}
