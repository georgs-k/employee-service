package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.business.service.EmployeeService;
import com.emansy.employeeservice.model.AttendeeIdsDto;
import com.emansy.employeeservice.model.EmployeesDto;
import com.emansy.employeeservice.model.EventDto;
import com.emansy.employeeservice.model.EventIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@Log4j2
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final EmployeeService employeeService;

    @KafkaListener(topics = "employees-request", groupId = "employee-group")
    @SendTo
    public Message<EmployeesDto> handleEmployeesRequest(ConsumerRecord<String, EventIdDto> consumerRecord) {
        Long eventId = consumerRecord.value().getId();
        if (consumerRecord.value().getWhetherAttendingOrNonAttendingEmployeesAreRequested()) {
            log.info("Request for employees attending event with id {} is received", eventId);
            return MessageBuilder.withPayload(new EmployeesDto(employeeService.findAttendingEmployees(eventId))).build();
        }
        log.info("Request for employees not attending event with id {} is received", eventId);
        return MessageBuilder.withPayload(new EmployeesDto(employeeService.findNonAttendingEmployees(eventId))).build();
    }

    @KafkaListener(topics = "attendance-request", groupId = "employee-group")
    @SendTo
    public Message<EventDto> handleAttendanceRequest(ConsumerRecord<String, AttendeeIdsDto> consumerRecord)
            throws ExecutionException, InterruptedException {
        Set<Long> employeeIds = consumerRecord.value().getEmployeeIds();
        EventDto eventDto = consumerRecord.value().getEventDto();
        if (consumerRecord.value().getWhetherToAttendOrToUnattend()) {
            log.info("Request for employees' with ids {} attendance of event with id {} is received",
                    employeeIds, eventDto.getId());
            return MessageBuilder.withPayload(employeeService.attendEvent(employeeIds, eventDto)).build();
        }
        if (employeeIds.isEmpty()) {
            log.info("Request for cancelling all employees' attendance of event with id {} is received",
                    eventDto.getId());
            return MessageBuilder.withPayload(employeeService.unattendAndDeleteEvent(eventDto)).build();
        }
        log.info("Request for cancelling employees' with ids {} attendance of event with id {} is received",
                employeeIds, eventDto.getId());
        return MessageBuilder.withPayload(employeeService.unattendEvent(employeeIds, eventDto)).build();
    }
}
