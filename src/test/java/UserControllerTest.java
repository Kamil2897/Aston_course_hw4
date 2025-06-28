import org.example.controller.UserController;
import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.example.exception.ValidationExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ValidationExceptionHandler exceptionHandler = new ValidationExceptionHandler();
    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(exceptionHandler).build();
    }

    @Test
    void createUser_WhenEmailIsUnique() throws Exception {
        String requestBody = """
                {
                "name": "Test",
                "email": "test@gmail.com",
                "age": 21
                }
                """;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test");
        userDTO.setEmail("test@gmail.com");
        userDTO.setAge(21);
        userDTO.setCreatedAt(LocalDateTime.of(2025, 6, 7, 12, 0));
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);
        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDTO.getId()))
                .andExpect(jsonPath("$.name").value(userDTO.getName()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()))
                .andExpect(jsonPath("$.age").value(userDTO.getAge()))
                .andExpect(jsonPath("$.createdAt").exists());
        verify(userService, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    void createUser_WhenEmailIsNotUnique() throws Exception {
        String email = "test@gmail.com";
        String requestBody = """
                {
                "name": "Test",
                "email": "%s",
                "age": 21
                }
                """.formatted(email);
        when(userService.createUser(any(UserDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Ошибка при попытке создания пользователя: введенный вами email " + email + " уже занят!"));
        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
        verify(userService, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    void createUser_WhenUsersAgeIsNegative() throws Exception {
        int age = -21;
        String requestBody = """
                {
                "name": "Test",
                "email": "test@gmail.com",
                "age": %d
                }
                """.formatted(age);
        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.age").value("Возраст не может быть отрицательным"));
    }

    @Test
    void createUser_WhenUsersAgeIsTooHigh() throws Exception {
        int age = 121;
        String requestBody = """
                {
                "name": "Test",
                "email": "test@gmail.com",
                "age": %d
                }
                """.formatted(age);
        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.age").value("Возраст не может превышать 120"));
    }

    @Test
    void getUser_WhenUserExists() throws Exception {
        Long id = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setName("Test");
        userDTO.setEmail("test@gmail.com");
        userDTO.setAge(21);
        userDTO.setCreatedAt(LocalDateTime.of(2025, 6, 9, 12, 0));
        when(userService.getUserById(id)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDTO.getId()))
                .andExpect(jsonPath("$.name").value(userDTO.getName()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()))
                .andExpect(jsonPath("$.age").value(userDTO.getAge()))
                .andExpect(jsonPath("$.createdAt").exists());
        verify(userService, times(1)).getUserById(id);
    }

    @Test
    void getUser_WhenUserIsNotExists() throws Exception {
        Long id = 1L;
        when(userService.getUserById(id))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при попытке получения данных: пользователя с ID " + id + " не существует!"));
        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).getUserById(id);
    }

    @Test
    void updateUser_WhenUserIsExists() throws Exception {
        Long id = 1L;
        String requestBody = """
                {
                "id": %d,
                "name": "Test",
                "email": "test@gmail.com",
                "age": 21
                }
                """.formatted(id);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setName("Test");
        userDTO.setEmail("test@gmail.com");
        userDTO.setAge(21);
        userDTO.setCreatedAt(LocalDateTime.of(2025, 6, 9, 12, 0));
        when(userService.updateUser(eq(id), any(UserDTO.class))).thenReturn(userDTO);
        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDTO.getId()))
                .andExpect(jsonPath("$.name").value(userDTO.getName()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()))
                .andExpect(jsonPath("$.age").value(userDTO.getAge()))
                .andExpect(jsonPath("$.createdAt").exists());
        verify(userService, times(1)).updateUser(eq(id), any(UserDTO.class));
    }

    @Test
    void updateUser_WhenUserIsNotExists() throws Exception {
        Long id = 1L;
        String requestBody = """
                {
                "name": "Test",
                "email": "test@gmail.com",
                "age": 21
                }
                """;
        when(userService.updateUser(eq(id), any(UserDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при попытке обновления данных: пользователя с ID " + id + " не существует!"));
        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).updateUser(eq(id), any(UserDTO.class));
    }

    @Test
    void updateUser_WhenUsersAgeIsNegative() throws Exception {
        Long id = 1L;
        int age = -21;
        String requestBody = """
                {
                "name": "Test",
                "email": "test@gmail.com",
                "age": %d
                }
                """.formatted(age);
        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.age").value("Возраст не может быть отрицательным"));
    }

    @Test
    void updateUser_WhenUsersAgeIsTooHigh() throws Exception {
        Long id = 1L;
        int age = 151;
        String requestBody = """
                {
                "name": "Test",
                "email": "test@gmail.com",
                "age": %d
                }
                """.formatted(age);
        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.age").value("Возраст не может превышать 120"));
    }

    @Test
    void deleteUser_WhenUserExists() throws Exception {
        Long id = 1L;
        doNothing().when(userService).deleteUser(id);
        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());
        verify(userService, times(1)).deleteUser(id);
    }

    @Test
    void deleteUser_WhenUserIsNotExists() throws Exception {
        Long id = 1L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при попытке удаления: пользователя с ID " + id + " не существует!")).when(userService).deleteUser(id);
        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).deleteUser(id);
    }
}
