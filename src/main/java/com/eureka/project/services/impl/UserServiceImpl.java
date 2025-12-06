package com.eureka.project.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eureka.project.dto.UserRequestDTO;
import com.eureka.project.dto.UsersByCategoriesDTO;
import com.eureka.project.exceptions.DataException;
import com.eureka.project.exceptions.UniqueEmailException;
import com.eureka.project.models.DepartmentModel;
import com.eureka.project.models.UserModel;
import com.eureka.project.repositories.DepartmentRepository;
import com.eureka.project.repositories.UserRepository;
import com.eureka.project.services.UserService;


@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, DepartmentRepository departmentRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
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
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new UniqueEmailException("Email existente");
            }

            logger.info("Guardando usuario: {}", user.getName());

            DepartmentModel department = departmentRepository.findById(user.getDepartmentId())
                .orElseThrow(() -> new DataException("Departamento no encontrado con ID: " + user.getDepartmentId()));

            UserModel userModel = modelMapper.map(user, UserModel.class);
            userModel.setDepartment(department);
   
            UserModel savedUser = userRepository.save(userModel);
            UserRequestDTO response = modelMapper.map(savedUser, UserRequestDTO.class);
            response.setDepartmentId(savedUser.getDepartment().getId());

            logger.info("Usuario guardado con éxito: {}", response.getName());
            return response;

        } catch (Exception e) {
            logger.error("Error al guardar usuario: {}", e.getMessage(), e);
            throw new DataException("Error al guardar usuario");
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
