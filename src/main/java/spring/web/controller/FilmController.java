package spring.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import spring.web.model.Film;
import spring.web.exception.ValidationException;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FilmController {
    private int id = 1;
    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST request");
        validate(film);
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Film add: " + film.toString());
        return film;
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT request");
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Invalid id film. Request id = " + film.getId());
        }
        validate(film);
        films.put(film.getId(), film);
        log.info("Film update: " + film.toString());
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("GET request");
        return new ArrayList<>(films.values());
    }

    private void validate(Film film) {
        if (film.getDescription().length() > 200) {
            log.debug("Description more than 200 characters");
            throw new ValidationException("Description more 200 characters");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Release date earlier than 28.12.1895");
            throw new ValidationException("Release date earlier 28.12.1895");
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            log.debug("Film duration is negative or zero");
            throw new ValidationException("Film duration is negative or zero");
        }
    }
}
