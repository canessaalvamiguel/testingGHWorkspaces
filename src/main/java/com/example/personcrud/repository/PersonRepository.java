package com.example.personcrud.repository;

import com.example.personcrud.model.Person;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PersonRepository {

    private final Map<Long, Person> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public List<Person> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Person> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Person save(Person person) {
        if (person.getId() == null) {
            person.setId(idSequence.getAndIncrement());
        }
        store.put(person.getId(), person);
        return person;
    }

    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
