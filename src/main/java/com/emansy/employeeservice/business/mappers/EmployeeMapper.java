package com.emansy.employeeservice.business.mappers;

import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import com.emansy.employeeservice.business.repository.model.EventAttendedEntity;
import com.emansy.employeeservice.model.EmployeeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Mapper(componentModel = "spring", uses = {JobTitleMapper.class, OfficeMapper.class})
public interface EmployeeMapper {

    @Mapping(source = "jobTitleDto", target = "jobTitleEntity")
    @Mapping(source = "officeDto", target = "officeEntity")
    @Mapping(source = "eventIds", target = "eventAttendedEntities", qualifiedByName = "eventIdsToEventAttendedEntities")
    EmployeeEntity dtoToEntity(EmployeeDto employeeDto);

    @Mapping(source = "jobTitleEntity", target = "jobTitleDto")
    @Mapping(source = "officeEntity", target = "officeDto")
    @Mapping(source = "eventAttendedEntities", target = "eventIds", qualifiedByName = "eventAttendedEntitiesToEventIds")
    EmployeeDto entityToDto(EmployeeEntity employeeEntity);

    @Named("eventIdsToEventAttendedEntities")
    default List<EventAttendedEntity> dtoToEntity(List<Long> eventIds) {
        return new ArrayList<>();
    }

    @Named("eventAttendedEntitiesToEventIds")
    default List<Long> entityToDto(List<EventAttendedEntity> eventAttendedEntities) {
        List<Long> eventIds = new ArrayList<>();
        if (isNotEmpty(eventAttendedEntities)) {
            eventAttendedEntities.forEach(eventAttendedEntity -> eventIds.add(eventAttendedEntity.getEventId()));
        }
        return eventIds;
    }
}
