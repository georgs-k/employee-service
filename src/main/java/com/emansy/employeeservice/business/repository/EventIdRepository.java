package com.emansy.employeeservice.business.repository;

import com.emansy.employeeservice.business.repository.model.EventIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventIdRepository extends JpaRepository<EventIdEntity, Long> {
}
