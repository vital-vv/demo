package com.example.demo.server.domain.repo;

import com.example.demo.server.domain.Employee;
import com.example.demo.server.model.EmployeeState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

  @Query("update Employee e set e.state = ?2 where e.id = ?1")
  @Modifying
  int setEmployeeStateById(Long id, EmployeeState state);
}
