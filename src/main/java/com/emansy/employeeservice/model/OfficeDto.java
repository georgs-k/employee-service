package com.emansy.employeeservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@ApiModel(value = "Model of office data ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class OfficeDto {

    @ApiModelProperty(value = "Unique id of an office")
    @Positive(message = "A positive integer number is required")
    @NotEmpty(message = "Required")
    private Long id;

    @ApiModelProperty(value = "Name of an office")
    private String name;

    @ApiModelProperty(value = "Street address of an office")
    private String streetAddress;

    @ApiModelProperty(value = "City of an office")
    private String city;

    @ApiModelProperty(value = "Country of an office")
    @NotBlank(message = "Required")
    private CountryDto countryDto;
}
