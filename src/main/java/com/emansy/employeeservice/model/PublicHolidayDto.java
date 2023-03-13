package com.emansy.employeeservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicHolidayDto {

    private String date;
}
