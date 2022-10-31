package com.example.demo.refactor;

import java.time.LocalDate;

/**
 * @author itanton
 */
public class Film extends Event {
    public Film(Integer id, String name, LocalDate releaseDate) {
        super(id, name, releaseDate);
    }
}
