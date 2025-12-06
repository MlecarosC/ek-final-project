package com.eureka.project.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eureka.project.dto.UserRequestDTO;
import com.eureka.project.dto.UsersByCategoriesDTO;
import com.eureka.project.exceptions.DepartmentNotFound;
import com.eureka.project.exceptions.UniqueEmailException;
import com.eureka.project.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserRequestDTO userRequestDTO;
    private List<UsersByCategoriesDTO> categoriesList;

    @BeforeEach
    void setUp() {
        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Juan Pérez");
        userRequestDTO.setEmail("juan.perez@example.com");
        userRequestDTO.setDepartmentId(1);

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

        categoriesList = Arrays.asList(dto1, dto2);
    }

    // ==================== Tests para GET /by-categories ====================

    @Test
    @DisplayName("GET /by-categories - Debe retornar 200 OK con lista de usuarios")
    void getUsersByCategories_ReturnsOk() throws Exception {
        // Arrange
        when(userService.getUsersByCategories()).thenReturn(categoriesList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/by-categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].departmentId").value(1))
                .andExpect(jsonPath("$[0].departmentName").value("Ventas"))
                .andExpect(jsonPath("$[0].userCount").value(17))
                .andExpect(jsonPath("$[1].departmentId").value(2))
                .andExpect(jsonPath("$[1].departmentName").value("Recursos Humanos"))
                .andExpect(jsonPath("$[1].userCount").value(25));

        verify(userService).getUsersByCategories();
    }

    @Test
    @DisplayName("GET /by-categories - Debe retornar lista vacía cuando no hay usuarios")
    void getUsersByCategories_ReturnsEmptyList() throws Exception {
        // Arrange
        when(userService.getUsersByCategories()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/by-categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService).getUsersByCategories();
    }

    // ==================== Tests para POST /create ====================

    @Test
    @DisplayName("POST /create - Debe crear usuario exitosamente y retornar 201 Created")
    void save_ReturnsCreated() throws Exception {
        // Arrange
        when(userService.save(any(UserRequestDTO.class))).thenReturn(userRequestDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.departmentId").value(1));

        verify(userService).save(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /create - Debe retornar 400 cuando el nombre está vacío")
    void save_ReturnsBadRequest_WhenNameIsEmpty() throws Exception {
        // Arrange
        userRequestDTO.setName("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.name").exists());

        verify(userService, never()).save(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /create - Debe retornar 400 cuando el email es inválido")
    void save_ReturnsBadRequest_WhenEmailIsInvalid() throws Exception {
        // Arrange
        userRequestDTO.setEmail("email-invalido");

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.email").exists());

        verify(userService, never()).save(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /create - Debe retornar 400 cuando departmentId es null")
    void save_ReturnsBadRequest_WhenDepartmentIdIsNull() throws Exception {
        // Arrange
        userRequestDTO.setDepartmentId(null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.departmentId").exists());

        verify(userService, never()).save(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /create - Debe retornar 409 cuando el email ya existe")
    void save_ReturnsConflict_WhenEmailExists() throws Exception {
        // Arrange
        when(userService.save(any(UserRequestDTO.class)))
                .thenThrow(new UniqueEmailException("Email existente"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("Email existente"));

        verify(userService).save(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /create - Debe retornar 404 cuando el departamento no existe")
    void save_ReturnsNotFound_WhenDepartmentNotExists() throws Exception {
        // Arrange
        when(userService.save(any(UserRequestDTO.class)))
                .thenThrow(new DepartmentNotFound("Departamento no encontrado con ID: 99"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Departamento no encontrado con ID: 99"));

        verify(userService).save(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /create - Debe retornar 400 cuando el nombre excede 50 caracteres")
    void save_ReturnsBadRequest_WhenNameExceedsMaxLength() throws Exception {
        // Arrange
        userRequestDTO.setName("A".repeat(51));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.name").exists());

        verify(userService, never()).save(any(UserRequestDTO.class));
    }
}
