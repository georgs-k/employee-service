package com.emansy.employeeservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@ApiModel(value = "Model of employee's job title data ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class JobTitleDto {

    @ApiModelProperty(value = "Unique id of a job title")
    @Positive(message = "A positive integer number is required")
    @NotEmpty(message = "Required")
    private Long id;

    @ApiModelProperty(value = "Name of a job title")
    private String name;
}
