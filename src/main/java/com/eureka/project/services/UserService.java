package com.eureka.project.services;

import java.util.List;

import com.eureka.project.dto.UserRequestDTO;
import com.eureka.project.dto.UsersByCategoriesDTO;

public interface UserService {
    List<UsersByCategoriesDTO> getUsersByCategories();
    UserRequestDTO save(UserRequestDTO user);
}
