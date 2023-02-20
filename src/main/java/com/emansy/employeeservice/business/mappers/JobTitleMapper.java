package com.emansy.employeeservice.business.mappers;

import com.emansy.employeeservice.business.repository.model.JobTitleEntity;
import com.emansy.employeeservice.model.JobTitleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobTitleMapper {

    JobTitleEntity dtoToEntity(JobTitleDto jobTitleDto);

    JobTitleDto entityToDto(JobTitleEntity jobTitleEntity);
}
