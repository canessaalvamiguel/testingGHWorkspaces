package com.example.personcrud.exception;

public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException(Long id) {
        super("Persona con id " + id + " no encontrada");
    }
}
