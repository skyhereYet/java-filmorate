package spring.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring.web.model.Film;
import spring.web.service.FilmService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    @Autowired
    private FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST request. Create film: " + film);
        filmService.createOrThrow(film);
        log.info("Film add: " + film.toString());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT request");
        filmService.updateOrThrow(film);
        log.info("Film update: " + film.toString());
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("GET request");
        return filmService.getFilmsList();
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable("id") int idFilm,
                                @PathVariable("userId") int idUser) {
        filmService.addLikeOrThrow(idFilm, idUser);
        log.info("The film (ID - " + idFilm + ") was liked by the user ID - " + idUser);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable("id") int idFilm,
                                @PathVariable("userId") int idUser) {
        filmService.deleteLikeOrThrow(idFilm, idUser);
        log.info("The film (ID - " + idFilm + ") was disliked by the user ID - " + idUser);
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("GET request - get user by ID - " + id);
        return filmService.getFilmByIdOrThrow(id);
    }

    @GetMapping(value = "/popular")
    public List<Film> getFilmPopular(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("GET request - get popular film (count - " + count + ")");
        return filmService.getFilmPopular(count);
    }
}