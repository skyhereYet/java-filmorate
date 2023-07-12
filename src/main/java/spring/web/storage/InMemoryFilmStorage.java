package spring.web.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import spring.web.controller.FilmController;
import spring.web.model.Film;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmsStorage = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @Override
    public void save(Film film) {
        filmsStorage.put(film.getId(), film);
        log.info("Film add: " + film.toString());
    }

    @Override
    public void update(Film film) {
        filmsStorage.put(film.getId(), film);
        log.info("Film update: " + film.toString());
    }

    @Override
    public void delete(Film film) {
        filmsStorage.remove(film.getId());
        log.info("Film deleted: " + film.toString());
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        if (filmsStorage.containsKey(id)) {
            return Optional.of(filmsStorage.get(id));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Film> filmExist(Film film) {
        if (filmsStorage.containsValue(film)) {
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public List<Film> getFilmsStorage() {
        return new ArrayList<>(filmsStorage.values());
    }
}
