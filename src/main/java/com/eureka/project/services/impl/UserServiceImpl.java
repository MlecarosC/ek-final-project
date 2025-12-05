package com.eureka.project.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Service;

import com.eureka.project.dto.UserRequestDTO;
import com.eureka.project.dto.UsersByCategoriesDTO;
import com.eureka.project.exceptions.DataException;
import com.eureka.project.models.UserModel;
import com.eureka.project.repositories.UserRepository;
import com.eureka.project.services.UserService;


@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<UsersByCategoriesDTO> getUsersByCategories() {
        try {
            logger.info("Obteniendo usuarios por categorías");
            return userRepository.getUsersByCategories();
        } catch (Exception e) {
            logger.error("Error al obtener usuarios por categorías: {}", e.getMessage(), e);
            throw new DataException("Error al obtener usuarios por categorias");
        }
    }

    @Override
    public UserRequestDTO save(UserRequestDTO user) {
        try {
            logger.info("Guardando usuario: {}", user.getName());
            UserModel userModel = modelMapper.map(user, UserModel.class);
            return modelMapper.map(userRepository.save(userModel), UserRequestDTO.class);
        } catch (Exception e) {
            logger.error("Error al guardar usuario: {}", e.getMessage(), e);
            throw new DataException("Error al guardar usuario");
        }
    }
}
