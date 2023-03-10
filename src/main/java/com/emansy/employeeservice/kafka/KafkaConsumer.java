package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.business.service.EmployeeService;
import com.emansy.employeeservice.model.AttendeeIdsDto;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventDto;
import com.emansy.employeeservice.model.EventIdDto;
import com.emansy.employeeservice.model.EventIdsWithinDatesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final EmployeeService employeeService;

    // temporary - a stub for event-service:
    @KafkaListener(topics = "events-request", groupId = "event-group")
    @SendTo
    public Message<Set<EventDto>> handleEventRequestStub(ConsumerRecord<String, EventIdsWithinDatesDto> consumerRecord) {
        Set<Long> eventIds = consumerRecord.value().getIds();
        String fromDate = consumerRecord.value().getFromDate();
        String thruDate = consumerRecord.value().getThruDate();
        log.info("Request for events with ids {}, scheduled within dates {} and {}, is received",
                eventIds, fromDate, thruDate);
        Set<EventDto> payload = eventIds.stream()
                .map(eventId -> new EventDto(
                        eventId, "Event", "Details", "2022-12-12", "09:00:00", "18:00:00"))
                .collect(Collectors.toSet());
        return MessageBuilder.withPayload(payload).build();
    }

    @KafkaListener(topics = "employees-request", groupId = "employee-group")
    @SendTo
    public Message<Set<EmployeeDto>> handleEmployeesRequest(ConsumerRecord<String, EventIdDto> consumerRecord) {
        Long eventId = consumerRecord.value().getId();
        if (consumerRecord.value().getWhetherAttendingOrNonAttendingEmployeesAreRequested()) {
            log.info("Request for employees attending event with id {} is received", eventId);
            return MessageBuilder.withPayload(employeeService.findAttendingEmployees(eventId)).build();
        }
        log.info("Request for employees not attending event with id {} is received", eventId);
        return MessageBuilder.withPayload(employeeService.findNonAttendingEmployees(eventId)).build();
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
