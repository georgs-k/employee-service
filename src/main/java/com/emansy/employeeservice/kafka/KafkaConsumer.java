package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.business.service.EmployeeService;
import com.emansy.employeeservice.model.AttendeeIdsDto;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventDto;
import com.emansy.employeeservice.model.EventIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.Set;

@Log4j2
@RequiredArgsConstructor
@Service
public class KafkaConsumer {

    private final EmployeeService employeeService;

    @KafkaListener(topics = "attending_employees_request", groupId = "employee_group")
    @SendTo("attending_employees_response")
    public Set<EmployeeDto> handleAttendingEmployeesRequest(EventIdDto eventIdDto) {
        log.info("Request for employees attending event with id {} is received", eventIdDto.getId());
        return employeeService.findAttendingEmployees(eventIdDto.getId());
    }

    @KafkaListener(topics = "non_attending_employees_request", groupId = "employee_group")
    @SendTo("non_attending_employees_response")
    public Set<EmployeeDto> handleNonAttendingEmployeesRequest(EventIdDto eventIdDto) {
        log.info("Request for employees not attending event with id {} is received", eventIdDto.getId());
        return employeeService.findNonAttendingEmployees(eventIdDto.getId());
    }

    @KafkaListener(topics = "unattend_request", groupId = "employee_group")
    public void handleUnattendRequest(AttendeeIdsDto attendeeIdsDto) {
        Set<Long> employeeIds = attendeeIdsDto.getEmployeeIds();
        EventDto eventDto = attendeeIdsDto.getEventDto();
        if (employeeIds.isEmpty()) {
            log.info("Request for cancelling all employees' attendance of event with id {} is received",
                    eventDto.getId());
            employeeService.unattendAndDeleteEvent(eventDto);
            return;
        }
        log.info("Request for cancelling employees' with ids {} attendance of event with id {} is received",
                employeeIds, eventDto.getId());
        employeeService.unattendEvent(employeeIds, eventDto);
    }
}
