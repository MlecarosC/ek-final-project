package com.eureka.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eureka.project.dto.UsersByCategoriesDTO;
import com.eureka.project.models.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Integer> {
    @Query("SELECT new com.eureka.project.dto.UsersByCategoriesDTO(" +
           "d.id, d.name, COUNT(u)) " +
           "FROM User u " +
           "JOIN u.department d " +
           "GROUP BY d.id, d.name " +
           "ORDER BY d.id")
    List<UsersByCategoriesDTO> getUsersByCategories();
}
