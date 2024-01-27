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
import java.util.*;

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
        Optional<Film> f = filmDbStorage.filmExist(film);
        if (filmDbStorage.filmExist(film).isPresent()) {
            log.info("The movie exists in the storage" + film.getName());
            throw new FilmExistException("The movie exists in the storage: " + film);
        } else {
            return filmDbStorage.save(film);
        }
    }

    public Film updateOrThrow(Film film) {
        if (filmDbStorage.findFilmById(film.getId()).isPresent()) {
            return filmDbStorage.update(film);
        }  else {
            log.info("The movie not exists in the storage: " + film);
            throw new FilmExistException("The movie not exists in the storage: " + film);
        }
    }

    public List<Film> getFilmsList() {
        log.info("Return film storage list");
        return filmDbStorage.getFilmsStorage();
    }

    public void addLikeOrThrow(int idFilm, int idUser) {
        Optional<Film> filmO = filmDbStorage.findFilmById(idFilm);
        Optional<User> userO = Optional.ofNullable(userService.getUserByIdOrThrow(idUser));
        if (filmDbStorage.checkLikes(idFilm, idUser)) {
            throw new FilmExistException("Film already liked user with ID - " + idUser);
        }
        if (filmO.isPresent() && userO.isPresent()) {
            filmDbStorage.addLike(idFilm, idUser);
            log.info("The film (ID - " + idFilm + ") was liked by the user ID - " + idUser);
        } else {
            throw new FilmExistException("The film with ID -" + idFilm + " not found");
        }
    }

    public void deleteLikeOrThrow(int idFilm, int idUser) {
        Optional<Film> filmO = filmDbStorage.findFilmById(idFilm);
        Optional<User> userO = Optional.ofNullable(userService.getUserByIdOrThrow(idUser));
        if (!filmDbStorage.checkLikes(idFilm, idUser)) {
            throw new FilmExistException("The film (ID - " + idFilm + ") was not liked by the user ID - " + idUser);
        }
        if (filmO.isPresent() && userO.isPresent()) {
            filmDbStorage.deleteLike(idFilm, idUser);
            log.info("The film (ID - " + idFilm + ") was disliked by the user ID - " + idUser);
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
        log.info("Return popular film, count = " + count);
        return filmDbStorage.getFilmPopular(count);
    }
}