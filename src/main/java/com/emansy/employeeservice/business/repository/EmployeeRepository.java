package com.emansy.employeeservice.business.repository;

import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    List<EmployeeEntity> findAllByEventIdEntitiesIdIn(List<Long> eventIds);

    List<EmployeeEntity> findAllByEventIdEntitiesIdNotIn(List<Long> eventIds);
}
