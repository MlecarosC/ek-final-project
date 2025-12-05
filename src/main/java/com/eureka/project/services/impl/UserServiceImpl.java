package com.eureka.project.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eureka.project.dto.UsersByCategoriesDTO;
import com.eureka.project.exceptions.DataException;
import com.eureka.project.repositories.UserRepository;
import com.eureka.project.services.UserService;


@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UsersByCategoriesDTO> getUsersByCategories() {
        try {
            logger.error("Obteniendo usuarios por categorías");
            return userRepository.getUsersByCategories();
        } catch (Exception e) {
            logger.error("Error al obtener usuarios por categorías: {}", e.getMessage(), e);
            throw new DataException("Error al obtener usuarios por categorias");
        }
    }
}
