package com.emansy.employeeservice.business.repository;

import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    List<EmployeeEntity> findAllByOrderByLastName();

    Set<EmployeeEntity> findAllByIdIn(List<Long> employeeIds);

    Set<EmployeeEntity> findAllByEventIdEntitiesIdIn(List<Long> eventIds);
}
