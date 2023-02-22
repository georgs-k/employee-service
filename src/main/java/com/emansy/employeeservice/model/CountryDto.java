package com.emansy.employeeservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@ApiModel(value = "Model of country data ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class CountryDto {

    @ApiModelProperty(value = "Two letter code of a country")
    @Pattern(regexp = "^[A-Z]{2}$", message = "A valid country code is required, two uppercase letters")
    @NotBlank(message = "Required")
    private String code;

    @ApiModelProperty(value = "Name of a country")
    @NotBlank(message = "Required")
    private String name;
}
