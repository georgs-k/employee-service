package com.emansy.employeeservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.Set;

@ApiModel(value = "Model of employee data ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class EmployeeDto {

    @ApiModelProperty(value = "Unique id of an employee")
    @Positive(message = "A positive integer number is required")
    private Long id;

    @ApiModelProperty(value = "First name(s) of an employee")
    @NotBlank(message = "Required")
    private String firstName;

    @ApiModelProperty(value = "Last name of an employee")
    @NotBlank(message = "Required")
    private String lastName;

    @ApiModelProperty(value = "Email of an employee")
    @Email(message = "A valid email address is required")
    @NotBlank(message = "Required")
    private String email;

    @ApiModelProperty(value = "Phone number of an employee")
    @Pattern(regexp = "^[^A-Za-z]+$", message = "A valid phone number is required, no letters accepted")
    @NotBlank(message = "Required")
    private String phone;

    @ApiModelProperty(value = "Job title of an employee")
    @NotNull(message = "Required")
    private JobTitleDto jobTitleDto;

    @ApiModelProperty(value = "Office of an employee")
    @NotNull(message = "Required")
    private OfficeDto officeDto;

    @ApiModelProperty(value = "Start time of an employee's working day")
    @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "Required time format: hh:mm:ss")
    @NotBlank(message = "Required")
    private String workingStartTime;

    @ApiModelProperty(value = "End time of an employee's working day")
    @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "Required time format: hh:mm:ss")
    @NotBlank(message = "Required")
    private String workingEndTime;

    @ApiModelProperty(value = "Event ids attended by an employee")
    private Set<Long> eventIds;
}
