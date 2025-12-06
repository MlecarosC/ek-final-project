package com.eureka.project.exceptions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eureka.project.controllers.UserController;
import com.eureka.project.dto.UserRequestDTO;
import com.eureka.project.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Debe manejar DataException y retornar 500")
    void handleDataException_Returns500() throws Exception {
        // Arrange
        when(userService.getUsersByCategories())
                .thenThrow(new DataException("Error al obtener usuarios por categorias"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/by-categories"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Error al obtener usuarios por categorias"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Debe manejar UniqueEmailException y retornar 409")
    void handleUniqueEmailException_Returns409() throws Exception {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Test");
        dto.setEmail("test@example.com");
        dto.setDepartmentId(1);

        when(userService.save(any(UserRequestDTO.class)))
                .thenThrow(new UniqueEmailException("Email existente"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("Email existente"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Debe manejar DepartmentNotFound y retornar 404")
    void handleDepartmentNotFound_Returns404() throws Exception {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Test");
        dto.setEmail("test@example.com");
        dto.setDepartmentId(999);

        when(userService.save(any(UserRequestDTO.class)))
                .thenThrow(new DepartmentNotFound("Departamento no encontrado con ID: 999"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Departamento no encontrado con ID: 999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException y retornar 400 con errores de validación")
    void handleValidationException_Returns400() throws Exception {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName(""); // Nombre vacío - inválido
        dto.setEmail("invalid-email"); // Email inválido
        dto.setDepartmentId(null); // DepartmentId null - inválido

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors").exists())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.departmentId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
