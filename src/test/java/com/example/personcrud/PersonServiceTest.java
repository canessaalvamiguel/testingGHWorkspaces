package com.example.personcrud;

import com.example.personcrud.exception.PersonNotFoundException;
import com.example.personcrud.model.Person;
import com.example.personcrud.repository.PersonRepository;
import com.example.personcrud.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonServiceTest {

    private PersonRepository repository;
    private PersonService service;

    @BeforeEach
    void setUp() {
        repository = mock(PersonRepository.class);
        service = new PersonService(repository);
    }

    @Test
    void findAll_returnsAllPersons() {
        List<Person> persons = List.of(
                new Person(1L, "Ana", "López", 30, "ana@example.com"),
                new Person(2L, "Juan", "García", 25, "juan@example.com")
        );
        when(repository.findAll()).thenReturn(persons);

        List<Person> result = service.findAll();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void findById_existingId_returnsPerson() {
        Person person = new Person(1L, "Ana", "López", 30, "ana@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(person));

        Person result = service.findById(1L);

        assertEquals("Ana", result.getNombre());
    }

    @Test
    void findById_notFound_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void create_assignsNullId_andSaves() {
        Person input = new Person(5L, "Ana", "López", 30, "ana@example.com");
        Person saved = new Person(1L, "Ana", "López", 30, "ana@example.com");
        when(repository.save(any(Person.class))).thenReturn(saved);

        Person result = service.create(input);

        assertNull(input.getId());
        assertEquals(1L, result.getId());
        verify(repository).save(input);
    }

    @Test
    void update_existingId_updatesFields() {
        Person existing = new Person(1L, "Ana", "López", 30, "ana@example.com");
        Person updated = new Person(null, "María", "Pérez", 28, "maria@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        Person result = service.update(1L, updated);

        assertEquals("María", result.getNombre());
        assertEquals("Pérez", result.getApellido());
        assertEquals(28, result.getEdad());
        assertEquals("maria@example.com", result.getEmail());
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(PersonNotFoundException.class, () -> service.delete(99L));
    }
}
