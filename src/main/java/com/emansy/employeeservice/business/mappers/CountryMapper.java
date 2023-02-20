package com.emansy.employeeservice.business.mappers;

import com.emansy.employeeservice.business.repository.model.CountryEntity;
import com.emansy.employeeservice.model.CountryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryEntity dtoToEntity(CountryDto countryDto);

    CountryDto entityToDto(CountryEntity countryEntity);
}
