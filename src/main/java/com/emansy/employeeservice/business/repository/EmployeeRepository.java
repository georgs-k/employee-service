package com.emansy.employeeservice.business.repository;

import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    Set<EmployeeEntity> findAllByEventIdEntitiesIdIn(List<Long> eventIds);
}
