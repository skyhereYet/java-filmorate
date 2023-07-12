package spring.web.storage;

import spring.web.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    void save(Film film);

    void update(Film film);

    void delete(Film film);

    Optional<Film> findFilmById(int id);

    Optional<Film> filmExist(Film film);

    List<Film> getFilmsStorage();
}
