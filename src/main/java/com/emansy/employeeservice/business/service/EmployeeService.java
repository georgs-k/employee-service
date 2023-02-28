package com.emansy.employeeservice.business.service;

import com.emansy.employeeservice.model.EmployeeDto;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    List<EmployeeDto> findAll();

    Optional<EmployeeDto> findById(Long id);

    EmployeeDto save(EmployeeDto employeeDto);

    EmployeeDto update(EmployeeDto employeeDto);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<EmployeeDto> findInvitedEmployees(List<Long> eventIds);
}
