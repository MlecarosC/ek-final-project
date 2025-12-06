package com.eureka.project.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eureka.project.dto.UserRequestDTO;
import com.eureka.project.dto.UsersByCategoriesDTO;
import com.eureka.project.exceptions.DataException;
import com.eureka.project.exceptions.DepartmentNotFound;
import com.eureka.project.exceptions.UniqueEmailException;
import com.eureka.project.models.DepartmentModel;
import com.eureka.project.models.UserModel;
import com.eureka.project.repositories.DepartmentRepository;
import com.eureka.project.repositories.UserRepository;
import com.eureka.project.services.UserService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;


@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public UserServiceImpl(UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<UsersByCategoriesDTO> getUsersByCategories() {
        try {
            logger.info("Obteniendo usuarios por categorías");
            entityManager.clear();
            return userRepository.getUsersByCategories();
        } catch (Exception e) {
            logger.error("Error al obtener usuarios por categorías: {}", e.getMessage(), e);
            throw new DataException("Error al obtener usuarios por categorias");
        }
    }

    @Override
    @Transactional
    public UserRequestDTO save(UserRequestDTO user) {
        try {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new UniqueEmailException("Email existente");
            }

            logger.info("Guardando usuario: {}", user.getName());

            DepartmentModel department = departmentRepository.findById(user.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFound("Departamento no encontrado con ID: " + user.getDepartmentId()));

            UserModel userModel = new UserModel();
            userModel.setName(user.getName());
            userModel.setEmail(user.getEmail());
            userModel.setDepartment(department);
   
            UserModel savedUser = userRepository.save(userModel);
            userRepository.flush();
            
            UserRequestDTO response = new UserRequestDTO();
            response.setName(savedUser.getName());
            response.setEmail(savedUser.getEmail());
            response.setDepartmentId(savedUser.getDepartment().getId());

            logger.info("Usuario guardado exitosamente con ID: {}", savedUser.getId());
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
