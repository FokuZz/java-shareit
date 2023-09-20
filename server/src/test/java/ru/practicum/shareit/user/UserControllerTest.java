package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String URL = "/users";
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserService service;
    @Autowired
    private MockMvc mvc;
    private UserDto userDto;
    private UserDto.UserDtoBuilder userDtoBuilder;

    @BeforeEach
    void setup() {
        userDtoBuilder = UserDto.builder()
                .name("name")
                .email("e@mail.ru");
    }

    @Test
    void testGetListEmpty() throws Exception {
        when(service.getList()).thenReturn(new ArrayList<>());
        this.mvc
                .perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetListOneUser() throws Exception {
        userDto = userDtoBuilder.id(1L).build();
        when(service.getList()).thenReturn(List.of(userDto));
        mvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail()), String.class));
    }

    @Test
    void testGetByIdOk() throws Exception {
        userDto = userDtoBuilder.id(1L).build();
        String json = objectMapper.writeValueAsString(userDto);

        when(service.getUserById(1)).thenReturn(userDto);
        mvc.perform(get(URL + "/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void testGetByIdNotFound() throws Exception {

        when(service.getUserById(1))
                .thenThrow(new NotFoundException(String.format("Пользователь с id %d не найден", 1)));
        mvc.perform(get(URL + "/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("Пользователь с id 1 не найден")));
    }

    @Test
    void testPatchEmail() throws Exception {
        String json = "{\"email\": \"patched@mail.ru\"}";
        userDto = UserDto.builder().email("patched@mail.ru").build();
        UserDto userDtoUpdated = userDtoBuilder.email("patched@mail.ru").build();
        String jsonPatched = objectMapper.writeValueAsString(userDtoUpdated);
        when(service.updateUser(1L, userDto)).thenReturn(userDtoUpdated);
        mvc.perform(patch(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonPatched));
    }

    @Test
    void testPatchName() throws Exception {
        String json = "{\"name\": \"namePatched\"}";
        userDto = UserDto.builder().name("namePatched").build();
        UserDto userDtoUpdated = userDtoBuilder.id(1L).name("namePatched").build();
        String jsonPatched = objectMapper.writeValueAsString(userDtoUpdated);
        when(service.updateUser(1L, userDto)).thenReturn(userDtoUpdated);
        mvc.perform(patch(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(jsonPatched));
    }

    @Test
    void testAddStandard() throws Exception {
        userDto = userDtoBuilder.build();
        UserDto userDtoAdded = userDtoBuilder.id(1L).build();

        String json = objectMapper.writeValueAsString(userDto);
        String jsonAdded = objectMapper.writeValueAsString(userDtoAdded);

        when(service.create(userDto)).thenReturn(userDtoAdded);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void testAddFailEmptyName() throws Exception {
        userDto = userDtoBuilder.name("").build();
        String json = objectMapper.writeValueAsString(userDto);

        doThrow(new ValidationException("Поле name не может быть пустым"))
                .when(service)
                .create(userDto);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Поле name не может быть пустым")));
    }

    @Test
    void testAddFailEmptyEmail() throws Exception {
        userDto = userDtoBuilder.name("name").email("").build();
        String json = objectMapper.writeValueAsString(userDto);

        doThrow(new ValidationException("Поле email не может быть пустым"))
                .when(service)
                .create(userDto);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Поле email не может быть пустым")));
    }

    @Test
    void testAddFailNotEmail() throws Exception {
        userDto = userDtoBuilder.name("").build();
        String json = objectMapper.writeValueAsString(userDto);

        doThrow(new ValidationException("Поле name не может быть пустым"))
                .when(service)
                .create(userDto);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Поле name не может быть пустым")));
    }


    @Test
    void testDeleteStandard() throws Exception {
        this.mvc.perform(delete(URL + "/1"))
                .andDo(print())
                .andExpect(status().isOk());

    }
}