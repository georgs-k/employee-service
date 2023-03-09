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
public class EventIdsWithinDatesDto {

    Set<Long> eventIds;

    String fromDate;

    String thruDate;
}
