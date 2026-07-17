package com.example.employeemanager.service;

import com.example.employeemanager.model.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee saveEmployee(Employee employee);
    Employee getEmployeeById(Long id);
    Employee updateEmployee(Employee employee);
    void deleteEmployeeById(Long id);
}
