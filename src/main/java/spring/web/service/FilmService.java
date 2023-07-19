package spring.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import spring.web.controller.FilmController;
import spring.web.exception.FilmExistException;
import spring.web.model.Film;
import spring.web.model.User;
import spring.web.storage.FilmStorage;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Qualifier("filmDbStorage")
    private FilmStorage filmDbStorage;
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmDbStorage = filmStorage;
    }

    @Autowired
    private UserService userService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public Film createOrThrow(Film film) {
        if (filmDbStorage.filmExist(film).isPresent()) {
            log.info("The movie exists in the storage" + film.getName());
            throw new FilmExistException("The movie exists in the storage: " + film);
        } else {
            filmDbStorage.save(film);
        }
        return film;
    }

    public Film updateOrThrow(Film film) {
        if (filmDbStorage.findFilmById(film.getId()).isPresent()) {
            filmDbStorage.update(film);
            return film;
        }  else {
            log.info("The movie not exists in the storage: " + film);
            throw new FilmExistException("The movie not exists in the storage: " + film);
        }
    }

    public List<Film> getFilmsList() {
        log.info("Return film storage list");
        return filmDbStorage.getFilmsStorage();
    }

    public Film addLikeOrThrow(int idFilm, int idUser) {
        Optional<Film> filmO = filmDbStorage.findFilmById(idFilm);
        Optional<User> userO = Optional.ofNullable(userService.getUserByIdOrThrow(idUser));
        if (filmO.isPresent() && userO.isPresent()) {
            if (filmO.get().getLikes().contains(idUser)) {
                throw new FilmExistException("Film already liked user with ID - " + idUser);
            }
            filmO.get().addLike(idUser);
            log.info("The film (ID - " + idFilm + ") was liked by the user ID - " + idUser);
            return filmO.get();
        } else {
            throw new FilmExistException("The film with ID -" + idFilm + " not found");
        }
    }

    public Film deleteLikeOrThrow(int idFilm, int idUser) {
        Optional<Film> filmO = filmDbStorage.findFilmById(idFilm);
        Optional<User> userO = Optional.ofNullable(userService.getUserByIdOrThrow(idUser));
        if (filmO.isPresent() && userO.isPresent()) {
            if (filmO.get().getLikes().contains(idUser)) {
                filmO.get().deleteLike(idUser);
                log.info("The user (ID - " + idUser + ") removed like from the film (ID -" + idFilm);
                return filmO.get();
            }
            throw new FilmExistException("The film (ID - " + idFilm + ") was not liked by the user ID - " + idUser);
        } else {
            throw new FilmExistException("The film with ID -" + idFilm + " not found");
        }
    }

    public Film getFilmByIdOrThrow(int id) {
        Optional<Film> filmO = filmDbStorage.findFilmById(id);
        if (filmO.isPresent()) {
            log.info("Return user - " + filmO.get());
            return filmO.get();
        } else {
            throw new FilmExistException("The film with id = " + id + " not exist");
        }
    }

    public List<Film> getFilmPopular(int count) {
        return filmDbStorage.getFilmsStorage().stream()
                .sorted((f0, f1) -> Integer.compare(f1.getLikes().size(), f0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
