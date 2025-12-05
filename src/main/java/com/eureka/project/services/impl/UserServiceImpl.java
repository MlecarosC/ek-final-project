package com.eureka.project.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eureka.project.dto.UsersByCategoriesDTO;
import com.eureka.project.repositories.UserRepository;
import com.eureka.project.services.UserService;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UsersByCategoriesDTO> getUsersByCategories() {
        // return this.getUsersByCategoriesMock();
        return userRepository.getUsersByCategories();
    }

    // private List<UsersByCategoriesDTO> getUsersByCategoriesMock() {
    //     final var users1 = UsersByCategoriesDTO.builder()
    //             .departmentid(1)
    //             .departmentName("Category A")
    //             .userCount(10)
    //             .build();
    //     final var users2 = UsersByCategoriesDTO.builder()
    //             .departmentid(2)
    //             .departmentName("Category B")
    //             .userCount(12)
    //             .build();
    //     final var users3 = UsersByCategoriesDTO.builder()
    //             .departmentid(3)
    //             .departmentName("Category C")
    //             .userCount(10)
    //             .build();

    //     return List.of(users1, users2, users3);
    // }
}
