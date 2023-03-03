package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.business.service.EmployeeService;
import com.emansy.employeeservice.model.AttendeeIdsDto;
import com.emansy.employeeservice.model.EmployeeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class EmployeeKafkaConsumer {

    private final EmployeeService employeeService;

    @KafkaListener(topics = "attending_employees_request", groupId = "employee_group")
    @SendTo("attending_employees_response")
    public List<EmployeeDto> handleAttendingEmployeesRequest(List<Integer> integerEventIds) {
        List<Long> eventIds = integerEventIds.stream().map(Long::valueOf).collect(Collectors.toList());
        log.info("Request for employees attending events with ids {} is received", eventIds);
        return employeeService.findAttendingEmployees(eventIds);
    }

    @KafkaListener(topics = "non_attending_employees_request", groupId = "employee_group")
    @SendTo("non_attending_employees_response")
    public List<EmployeeDto> handleNonAttendingEmployeesRequest(List<Integer> integerEventIds) {
        List<Long> eventIds = integerEventIds.stream().map(Long::valueOf).collect(Collectors.toList());
        log.info("Request for employees not attending events with ids {} is received", eventIds);
        return employeeService.findNonAttendingEmployees(eventIds);
    }

    @KafkaListener(topics = "unattend_request", groupId = "employee_group")
    public void handleUnattendRequest(AttendeeIdsDto attendeeIdsDto) {
        log.info("Request for cancelling employees' with ids {} attendances of event with id {} is received",
                attendeeIdsDto.getAttendeeIds(), attendeeIdsDto.getEventId());
        employeeService.unattend(attendeeIdsDto.getAttendeeIds(), attendeeIdsDto.getEventId());
    }
}
