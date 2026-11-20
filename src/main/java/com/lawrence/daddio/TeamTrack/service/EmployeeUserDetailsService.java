package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.repo.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Tells Spring Security how to look up a user: the "username" is the employee's email, and the
 * {@code Employee} entity itself implements {@code UserDetails}. Registering this bean also replaces
 * Spring Boot's default in-memory user.
 */
@Service
public class EmployeeUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public EmployeeUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /** @throws UsernameNotFoundException if no employee has this email (surfaces as a failed login) */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No employee with email " + email));
    }
}
