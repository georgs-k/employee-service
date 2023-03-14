package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.business.service.EmployeeService;
import com.emansy.employeeservice.model.AttendeeIdsDto;
import com.emansy.employeeservice.model.EmployeesDto;
import com.emansy.employeeservice.model.EventDto;
import com.emansy.employeeservice.model.EventIdDto;
import com.emansy.employeeservice.model.EventIdsWithinDatesDto;
import com.emansy.employeeservice.model.EventsDto;
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
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final EmployeeService employeeService;

    // temporary - a stub for event-service:
    @KafkaListener(topics = "events-request", groupId = "event-group")
    @SendTo
    public Message<EventsDto> handleEventRequestStub(ConsumerRecord<String, EventIdsWithinDatesDto> consumerRecord) {
        Set<Long> eventIds = consumerRecord.value().getIds();
        String fromDate = consumerRecord.value().getFromDate();
        String thruDate = consumerRecord.value().getThruDate();
        EventsDto payload = new EventsDto(eventIds.stream()
                .map(eventId -> new EventDto(
                        eventId, "Event", "Details", "2023-04-14", "10:00:00", "11:00:00"))
                .collect(Collectors.toSet()));
        return MessageBuilder.withPayload(payload).build();
    }
    // temporary end

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
    public Message<EventDto> handleAttendanceRequest(AttendeeIdsDto attendeeIdsDto) throws ExecutionException, InterruptedException {
        Set<Long> employeeIds = attendeeIdsDto.getEmployeeIds();
        EventDto eventDto = attendeeIdsDto.getEventDto();
        if (attendeeIdsDto.getWhetherToAttendOrToUnattend()) {
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
