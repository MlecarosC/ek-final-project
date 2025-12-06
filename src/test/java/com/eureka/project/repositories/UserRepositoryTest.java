package com.eureka.project.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.eureka.project.dto.UsersByCategoriesDTO;
import com.eureka.project.models.DepartmentModel;
import com.eureka.project.models.UserModel;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private DepartmentModel department1;
    private DepartmentModel department2;

    @BeforeEach
    void setUp() {
        // Crear departamentos
        department1 = new DepartmentModel();
        department1.setName("Ventas");
        department1 = entityManager.persistAndFlush(department1);

        department2 = new DepartmentModel();
        department2.setName("Recursos Humanos");
        department2 = entityManager.persistAndFlush(department2);

        // Crear usuarios para department1
        for (int i = 1; i <= 3; i++) {
            UserModel user = new UserModel();
            user.setName("Usuario " + i);
            user.setEmail("user" + i + "@example.com");
            user.setDepartment(department1);
            entityManager.persistAndFlush(user);
        }

        // Crear usuarios para department2
        for (int i = 4; i <= 6; i++) {
            UserModel user = new UserModel();
            user.setName("Usuario " + i);
            user.setEmail("user" + i + "@example.com");
            user.setDepartment(department2);
            entityManager.persistAndFlush(user);
        }

        entityManager.clear();
    }

    @Test
    @DisplayName("Debe agrupar usuarios por departamento correctamente")
    void getUsersByCategories_GroupsCorrectly() {
        List<UsersByCategoriesDTO> result = userRepository.getUsersByCategories();

        assertNotNull(result);
        assertEquals(2, result.size());

        UsersByCategoriesDTO dept1 = result.get(0);
        assertEquals("Ventas", dept1.getDepartmentName());
        assertEquals(3L, dept1.getUserCount());

        UsersByCategoriesDTO dept2 = result.get(1);
        assertEquals("Recursos Humanos", dept2.getDepartmentName());
        assertEquals(3L, dept2.getUserCount());
    }

    @Test
    @DisplayName("Debe ordenar resultados por departmentId")
    void getUsersByCategories_OrdersByDepartmentId() {
        List<UsersByCategoriesDTO> result = userRepository.getUsersByCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getDepartmentId() < result.get(1).getDepartmentId());
    }

    @Test
    @DisplayName("Debe retornar true cuando el email existe")
    void existsByEmail_ReturnsTrue() {
        boolean exists = userRepository.existsByEmail("user1@example.com");
        assertTrue(exists);
    }

    @Test
    @DisplayName("Debe retornar false cuando el email no existe")
    void existsByEmail_ReturnsFalse() {
        boolean exists = userRepository.existsByEmail("noexiste@example.com");
        assertFalse(exists);
    }

    @Test
    @DisplayName("Debe guardar usuario correctamente")
    void save_PersistsUser() {
        UserModel newUser = new UserModel();
        newUser.setName("Nuevo Usuario");
        newUser.setEmail("nuevo@example.com");
        newUser.setDepartment(department1);

        UserModel saved = userRepository.save(newUser);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(saved.getId());
        UserModel found = entityManager.find(UserModel.class, saved.getId());
        assertNotNull(found);
        assertEquals("Nuevo Usuario", found.getName());
        assertEquals("nuevo@example.com", found.getEmail());
        assertEquals(department1.getId(), found.getDepartment().getId());
    }

    @Test
    @DisplayName("Debe actualizar conteo cuando se agrega usuario")
    void getUsersByCategories_UpdatesCountWhenUserAdded() {
        List<UsersByCategoriesDTO> before = userRepository.getUsersByCategories();
        long initialCount = before.get(0).getUserCount();

        UserModel newUser = new UserModel();
        newUser.setName("Test User");
        newUser.setEmail("test@example.com");
        newUser.setDepartment(department1);
        userRepository.save(newUser);
        entityManager.flush();
        entityManager.clear();

        List<UsersByCategoriesDTO> after = userRepository.getUsersByCategories();
        assertEquals(initialCount + 1, after.get(0).getUserCount());
    }
}
