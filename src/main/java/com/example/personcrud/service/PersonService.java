package com.example.personcrud.service;

import com.example.personcrud.exception.PersonNotFoundException;
import com.example.personcrud.model.Person;
import com.example.personcrud.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public List<Person> findAll() {
        return repository.findAll();
    }

    public Person findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    public Person create(Person person) {
        person.setId(null);
        return repository.save(person);
    }

    public Person update(Long id, Person updated) {
        Person existing = findById(id);
        existing.setNombre(updated.getNombre());
        existing.setApellido(updated.getApellido());
        existing.setEdad(updated.getEdad());
        existing.setEmail(updated.getEmail());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new PersonNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
