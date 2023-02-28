package com.emansy.employeeservice.business.mappers;

import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import com.emansy.employeeservice.business.repository.model.EventIdEntity;
import com.emansy.employeeservice.model.EmployeeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;

import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Mapper(componentModel = "spring", uses = {JobTitleMapper.class, OfficeMapper.class})
public interface EmployeeMapper {

    @Mapping(source = "jobTitleDto", target = "jobTitleEntity")
    @Mapping(source = "officeDto", target = "officeEntity")
    @Mapping(source = "eventIds", target = "eventIdEntities", qualifiedByName = "eventIdsToEventIdEntities")
    EmployeeEntity dtoToEntity(EmployeeDto employeeDto);

    @Mapping(source = "jobTitleEntity", target = "jobTitleDto")
    @Mapping(source = "officeEntity", target = "officeDto")
    @Mapping(source = "eventIdEntities", target = "eventIds", qualifiedByName = "eventIdEntitiesToEventIds")
    EmployeeDto entityToDto(EmployeeEntity employeeEntity);

    @Named("eventIdsToEventIdEntities")
    default Set<EventIdEntity> idsToEntities(Set<Long> ignoredEventIds) {
        return new HashSet<>();
    }

    @Named("eventIdEntitiesToEventIds")
    default Set<Long> entitiesToIds(Set<EventIdEntity> eventIdEntities) {
        Set<Long> eventIds = new HashSet<>();
        if (isNotEmpty(eventIdEntities)) {
            eventIdEntities.forEach(eventIdEntity -> eventIds.add(eventIdEntity.getId()));
        }
        return eventIds;
    }
}
