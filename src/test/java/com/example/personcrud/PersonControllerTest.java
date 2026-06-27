package com.example.personcrud;

import com.example.personcrud.controller.PersonController;
import com.example.personcrud.exception.GlobalExceptionHandler;
import com.example.personcrud.exception.PersonNotFoundException;
import com.example.personcrud.model.Person;
import com.example.personcrud.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
@Import(GlobalExceptionHandler.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returns200WithList() throws Exception {
        List<Person> persons = List.of(
                new Person(1L, "Ana", "López", 30, "ana@example.com")
        );
        when(service.findAll()).thenReturn(persons);

        mockMvc.perform(get("/api/personas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Ana"));
    }

    @Test
    void getById_existingId_returns200() throws Exception {
        Person person = new Person(1L, "Ana", "López", 30, "ana@example.com");
        when(service.findById(1L)).thenReturn(person);

        mockMvc.perform(get("/api/personas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellido").value("López"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(service.findById(99L)).thenThrow(new PersonNotFoundException(99L));

        mockMvc.perform(get("/api/personas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void create_validBody_returns201() throws Exception {
        Person input = new Person(null, "Ana", "López", 30, "ana@example.com");
        Person saved = new Person(1L, "Ana", "López", 30, "ana@example.com");
        when(service.create(any(Person.class))).thenReturn(saved);

        mockMvc.perform(post("/api/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_invalidBody_returns400() throws Exception {
        Person invalid = new Person(null, "", "", -1, "not-an-email");

        mockMvc.perform(post("/api/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores").exists());
    }

    @Test
    void update_existingId_returns200() throws Exception {
        Person updated = new Person(1L, "María", "Pérez", 28, "maria@example.com");
        when(service.update(eq(1L), any(Person.class))).thenReturn(updated);

        mockMvc.perform(put("/api/personas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("María"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/personas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new PersonNotFoundException(99L)).when(service).delete(99L);

        mockMvc.perform(delete("/api/personas/99"))
                .andExpect(status().isNotFound());
    }
}
