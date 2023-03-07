package com.emansy.employeeservice.business.service;

import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeService {

    List<EmployeeDto> findAll();

    Optional<EmployeeDto> findById(Long id);

    EmployeeDto save(EmployeeDto employeeDto);

    EmployeeDto update(EmployeeDto employeeDto);

    void deleteById(Long id);

    boolean existsById(Long id);

    Set<EmployeeDto> findAttendingEmployees(Long eventId);

    Set<EmployeeDto> findNonAttendingEmployees(Long eventId);

    void unattendEvent(Set<Long> employeeIds, EventDto eventDto);

    void unattendAndDeleteEvent(EventDto eventDto);
}
