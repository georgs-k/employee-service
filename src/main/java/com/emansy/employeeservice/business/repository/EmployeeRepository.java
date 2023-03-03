package com.emansy.employeeservice.business.repository;

import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    Set<EmployeeEntity> findAllByIdIn(Set<Long> employeeIds);

    Set<EmployeeEntity> findAllByEventIdEntitiesIdIn(Set<Long> eventIds);
}
