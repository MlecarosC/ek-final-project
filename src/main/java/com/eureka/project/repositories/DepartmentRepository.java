package com.eureka.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.eureka.project.models.DepartmentModel;

public interface DepartmentRepository extends JpaRepository<DepartmentModel, Integer> {
}
