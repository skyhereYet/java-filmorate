package spring.web.storage;

import spring.web.model.Genre;
import java.util.List;
import java.util.Optional;

public interface GenresStorage {
    Optional<Genre> getGenreById(int idGenre);
    List<Genre> getAllGenres();
}
