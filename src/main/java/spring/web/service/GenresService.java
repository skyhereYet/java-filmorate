package spring.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spring.web.controller.FilmController;
import spring.web.exception.GenreExistException;
import spring.web.model.Genre;
import spring.web.storage.GenresStorage;
import java.util.List;
import java.util.Optional;

@Service
public class GenresService {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private GenresStorage genresStorage;

    public GenresService (GenresStorage genresStorage) {
        this.genresStorage = genresStorage;
    }

    public Genre getGenreByIdOrThrow(int id) {
        Optional<Genre> genreO = genresStorage.getGenreById(id);
        if (genreO.isPresent()) {
            log.info("Return genre - " + genreO.get());
            return genreO.get();
        } else {
            throw new GenreExistException("Genre with id = " + id + " not exist");
        }
    }

    public List<Genre> getAllGenres() {
        log.info("Return genres storage list");
        return genresStorage.getAllGenres();
    }
}