package com.emansy.employeeservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class AttendeeIdsDto {

    private Boolean whetherToAttendOrToUnattend;

    private Set<Long> employeeIds;

    private EventDto eventDto;
}
