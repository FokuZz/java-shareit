package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final String URL = "/users";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserClient client;

    @Autowired
    private MockMvc mvc;

    @Test
    void testGetUsers() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .name("name")
                .email("email@mail.ru")
                .build();
        when(client.getAll()).thenReturn(ResponseEntity.of(Optional.of(userDto)));
        this.mvc
                .perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void testGetUserId() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .name("name")
                .email("email@mail.ru")
                .build();
        when(client.getId(eq(2L))).thenReturn(ResponseEntity.of(Optional.of(userDto)));
        this.mvc
                .perform(get(URL + "/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void testPatchUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .name("name")
                .email("email@mail.ru")
                .build();
        String json = mapper.writeValueAsString(userDto);
        when(client.patch(eq(2L), eq(userDto))).thenReturn(ResponseEntity.of(Optional.of(userDto)));
        this.mvc
                .perform(patch(URL + "/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void testPostUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .name("name")
                .email("email@mail.ru")
                .build();
        String json = mapper.writeValueAsString(userDto);
        when(client.post(eq(userDto))).thenReturn(ResponseEntity.of(Optional.of(userDto)));
        this.mvc
                .perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void testDeleteItem() throws Exception {
        this.mvc
                .perform(delete(URL + "/2"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}