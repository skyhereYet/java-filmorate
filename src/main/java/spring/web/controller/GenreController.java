package spring.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.web.model.Genre;
import spring.web.service.GenresService;
import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    @Autowired
    private GenresService genresService;

    @GetMapping
    public List<Genre> getGenre() {
        log.info("GET request get all genres");
        return genresService.getAllGenres();
    }

    @GetMapping(value = "/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.info("GET request - get genre by ID - " + id);
        return genresService.getGenreByIdOrThrow(id);
    }
}