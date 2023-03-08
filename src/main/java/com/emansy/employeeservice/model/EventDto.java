package com.emansy.employeeservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@ApiModel(value = "Model of event data ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class EventDto {

    @ApiModelProperty(value = "Unique id of an event")
    @Positive(message = "A positive integer number is required")
    private Long id;

    @ApiModelProperty(value = "Title of an event")
    @NotBlank(message = "Required")
    private String title;

    @ApiModelProperty(value = "Details of an event")
    @NotBlank(message = "Required")
    private String details;

    @ApiModelProperty(value = "Date of an event")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Required date format: yyyy-MM-dd")
    @NotBlank(message = "Required")
    private String date;

    @ApiModelProperty(value = "Start time of an event")
    @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "Required time format: hh:mm:ss")
    @NotBlank(message = "Required")
    private String startTime;

    @ApiModelProperty(value = "End time of an event")
    @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "Required time format: hh:mm:ss")
    @NotBlank(message = "Required")
    private String endTime;
}
