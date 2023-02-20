package com.emansy.employeeservice.business.mappers;

import com.emansy.employeeservice.business.repository.model.OfficeEntity;
import com.emansy.employeeservice.model.OfficeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CountryMapper.class)
public interface OfficeMapper {

    @Mapping(source = "countryDto", target = "countryEntity")
    OfficeEntity dtoToEntity(OfficeDto officeDto);

    @Mapping(source = "countryEntity", target = "countryDto")
    OfficeDto entityToDto(OfficeEntity officeEntity);
}
