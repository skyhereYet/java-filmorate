package spring.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import spring.web.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class FilmController {
    private int id = 1;
    private HashMap<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @PostMapping("/films")
    public Film create(@RequestBody Film film) {
        log.info("POST request");
        validate(film);
        film.setId(id++);
        films.put(film.getId(), film);
        System.out.println(film.toString());
        return film;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        log.info("PUT request");
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Invalid id film");
        }
        validate(film);
        System.out.println(film.toString());
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("GET request");
        return new ArrayList<>(films.values());
    }

    private void validate(Film film) {
        if (film.getName().isEmpty()) {
            log.debug("Name is empty");
            throw new ValidationException("Name is empty");
        }
        if (film.getDescription().length() >= 200) {
            log.debug("Description more than 200 characters");
            throw new ValidationException("Description more than 200 characters");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Release date earlier than 28.12.1895");
            throw new ValidationException("Release date earlier than 28.12.1895");
        }
        if (film.getDuration().isNegative()) {
            log.debug("Film duration is negative");
            throw new ValidationException("Film duration is negative");
        }
    }
}
