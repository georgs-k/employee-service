package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.model.AttendeesDto;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventDto;
import com.emansy.employeeservice.model.EventIdsWithDatesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Log4j2
@RequiredArgsConstructor
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, AttendeesDto> attendeesKafkaTemplate;

//    private final ReplyingKafkaTemplate<String, EventIdsWithDatesDto, Set<EventDto>> eventsAttendedKafkaTemplate;

    public void sendUnattendNotification(Set<EmployeeDto> employeeDtos, EventDto eventDto) {
        attendeesKafkaTemplate.send("unattend_notification", new AttendeesDto(employeeDtos, eventDto));
        log.info("Unattend notification is sent to kafka topic: unattend_notification");
    }
}
