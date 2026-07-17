package com.example.employeemanager.service;

import com.example.employeemanager.model.Employee;
import com.example.employeemanager.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void saveEmployeePersistsAndReturnsEmployee() {
        Employee employee = new Employee(null, "Ada", "Lovelace", "ada@example.com", "Engineer");
        Employee savedEmployee = new Employee(1L, "Ada", "Lovelace", "ada@example.com", "Engineer");
        when(employeeRepository.save(employee)).thenReturn(savedEmployee);

        Employee result = employeeService.saveEmployee(employee);

        assertThat(result).isSameAs(savedEmployee);
        verify(employeeRepository).save(employee);
    }

    @Test
    void getAllEmployeesReturnsRepositoryEmployees() {
        List<Employee> employees = List.of(new Employee(1L, "Ada", "Lovelace", "ada@example.com", "Engineer"));
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertThat(result).containsExactlyElementsOf(employees);
        verify(employeeRepository).findAll();
    }

    @Test
    void deleteEmployeeByIdDeletesTheRequestedEmployee() {
        employeeService.deleteEmployeeById(42L);

        verify(employeeRepository).deleteById(42L);
    }

    @Test
    void getEmployeeByIdReturnsNullWhenEmployeeDoesNotExist() {
        when(employeeRepository.findById(404L)).thenReturn(Optional.empty());

        Employee result = employeeService.getEmployeeById(404L);

        assertThat(result).isNull();
        verify(employeeRepository).findById(404L);
    }
}
