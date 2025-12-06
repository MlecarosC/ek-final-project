package com.eureka.project.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eureka.project.dto.UserRequestDTO;
import com.eureka.project.dto.UsersByCategoriesDTO;
import com.eureka.project.exceptions.DataException;
import com.eureka.project.exceptions.DepartmentNotFound;
import com.eureka.project.exceptions.UniqueEmailException;
import com.eureka.project.models.DepartmentModel;
import com.eureka.project.models.UserModel;
import com.eureka.project.repositories.DepartmentRepository;
import com.eureka.project.repositories.UserRepository;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserServiceImpl userService;

    private DepartmentModel departmentModel;
    private UserModel userModel;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        departmentModel = new DepartmentModel();
        departmentModel.setId(1);
        departmentModel.setName("Ventas");

        userModel = new UserModel();
        userModel.setId(1);
        userModel.setName("Juan Pérez");
        userModel.setEmail("juan.perez@example.com");
        userModel.setDepartment(departmentModel);

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Juan Pérez");
        userRequestDTO.setEmail("juan.perez@example.com");
        userRequestDTO.setDepartmentId(1);
    }

    @Test
    @DisplayName("Debe retornar lista de usuarios por categorías exitosamente")
    void getUsersByCategories_Success() {
        UsersByCategoriesDTO dto1 = UsersByCategoriesDTO.builder()
                .departmentId(1)
                .departmentName("Ventas")
                .userCount(17L)
                .build();

        UsersByCategoriesDTO dto2 = UsersByCategoriesDTO.builder()
                .departmentId(2)
                .departmentName("Recursos Humanos")
                .userCount(25L)
                .build();

        List<UsersByCategoriesDTO> expectedList = Arrays.asList(dto1, dto2);

        doNothing().when(entityManager).clear();
        when(userRepository.getUsersByCategories()).thenReturn(expectedList);

        List<UsersByCategoriesDTO> result = userService.getUsersByCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Ventas", result.get(0).getDepartmentName());
        assertEquals(17L, result.get(0).getUserCount());
        
        verify(entityManager, times(1)).clear();
        verify(userRepository, times(1)).getUsersByCategories();
    }

    @Test
    @DisplayName("Debe lanzar DataException cuando falla la consulta")
    void getUsersByCategories_ThrowsDataException() {
        doNothing().when(entityManager).clear();
        when(userRepository.getUsersByCategories())
                .thenThrow(new RuntimeException("Database error"));

        DataException exception = assertThrows(DataException.class, () -> {
            userService.getUsersByCategories();
        });

        assertEquals("Error al obtener usuarios por categorias", exception.getMessage());
        verify(entityManager, times(1)).clear();
        verify(userRepository, times(1)).getUsersByCategories();
    }

    @Test
    @DisplayName("Debe guardar usuario exitosamente")
    void save_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(anyInt())).thenReturn(Optional.of(departmentModel));
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);
        doNothing().when(userRepository).flush();

        UserRequestDTO result = userService.save(userRequestDTO);

        assertNotNull(result);
        assertEquals("Juan Pérez", result.getName());
        assertEquals("juan.perez@example.com", result.getEmail());
        assertEquals(1, result.getDepartmentId());

        verify(userRepository).existsByEmail("juan.perez@example.com");
        verify(departmentRepository).findById(1);
        verify(userRepository).save(any(UserModel.class));
        verify(userRepository).flush();
    }

    @Test
    @DisplayName("Debe lanzar UniqueEmailException cuando el email ya existe")
    void save_ThrowsUniqueEmailException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        UniqueEmailException exception = assertThrows(UniqueEmailException.class, () -> {
            userService.save(userRequestDTO);
        });

        assertEquals("Email existente", exception.getMessage());
        verify(userRepository).existsByEmail("juan.perez@example.com");
        verify(departmentRepository, never()).findById(anyInt());
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Debe lanzar DepartmentNotFound cuando el departamento no existe")
    void save_ThrowsDepartmentNotFound() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(anyInt())).thenReturn(Optional.empty());

        DepartmentNotFound exception = assertThrows(DepartmentNotFound.class, () -> {
            userService.save(userRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Departamento no encontrado"));
        verify(userRepository).existsByEmail("juan.perez@example.com");
        verify(departmentRepository).findById(1);
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Debe manejar excepción al guardar usuario")
    void save_HandlesException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(anyInt())).thenReturn(Optional.of(departmentModel));
        when(userRepository.save(any(UserModel.class)))
                .thenThrow(new RuntimeException("Database error"));

        DataException exception = assertThrows(DataException.class, () -> {
            userService.save(userRequestDTO);
        });

        assertEquals("Error al guardar usuario", exception.getMessage());
    }

    @Test
    @DisplayName("Debe retornar true cuando el email existe")
    void existsByEmail_ReturnsTrue() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("Debe retornar false cuando el email no existe")
    void existsByEmail_ReturnsFalse() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        boolean result = userService.existsByEmail("test@example.com");

        assertFalse(result);
        verify(userRepository).existsByEmail("test@example.com");
    }
}
