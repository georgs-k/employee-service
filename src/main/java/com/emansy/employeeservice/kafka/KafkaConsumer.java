package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.business.service.EmployeeService;
import com.emansy.employeeservice.model.AttendeeIdsDto;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventDto;
import com.emansy.employeeservice.model.EventIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Log4j2
@RequiredArgsConstructor
@Service
public class KafkaConsumer {

    private final EmployeeService employeeService;

    @KafkaListener(topics = "employees-request", groupId = "employee-group")
    @SendTo
    public Message<Set<EmployeeDto>> handleEmployeesRequest(ConsumerRecord<String, EventIdDto> consumerRecord) {
        Set<EmployeeDto> payload;
        Long eventId = consumerRecord.value().getId();
        if (consumerRecord.value().getWhetherAttendingOrNonAttendingEmployeesAreRequested()) {
            log.info("Request for employees attending event with id {} is received", eventId);
            payload = employeeService.findAttendingEmployees(eventId);
        } else {
            log.info("Request for employees not attending event with id {} is received", eventId);
            payload = employeeService.findNonAttendingEmployees(eventId);
        }
        return MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.CORRELATION_ID, consumerRecord.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value())
                .build();
    }

    @KafkaListener(topics = "attendance-request", groupId = "employee-group")
    public void handleAttendanceRequest(AttendeeIdsDto attendeeIdsDto) {
        Set<Long> employeeIds = attendeeIdsDto.getEmployeeIds();
        EventDto eventDto = attendeeIdsDto.getEventDto();
        if (attendeeIdsDto.getWhetherToAttendOrToUnattend()) {

            // TO DO: employeeService.attend(employeeIds, eventDto);

            return;
        }
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
