package spring.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private FilmStorage inMemoryFilmStorage;
    @Autowired
    private UserService userService;
    private int id = 1;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public Film createOrThrow(Film film) {
        if (inMemoryFilmStorage.filmExist(film).isPresent()) {
            log.info("The movie exists in the storage" + film.getName());
            throw new FilmExistException("The movie exists in the storage: " + film);
        } else {
            film.setId(id++);
            inMemoryFilmStorage.save(film);
        }
        return film;
    }

    public Film updateOrThrow(Film film) {
        if (inMemoryFilmStorage.filmExist(film).isPresent()) {
            inMemoryFilmStorage.update(film);
            return film;
        }  else {
            log.info("The movie not exists in the storage: " + film);
            throw new FilmExistException("The movie not exists in the storage: " + film);
        }
    }

    public List<Film> getFilmsList() {
        log.info("Return film storage list");
        return inMemoryFilmStorage.getFilmsStorage();
    }

    public Film addLikeOrThrow(int idFilm, int idUser) {
        Optional<Film> filmO = inMemoryFilmStorage.findFilmById(idFilm);
        Optional<User> userO = Optional.ofNullable(userService.getUserByIdOrThrow(idUser));
        if (filmO.isPresent() && userO.isPresent()) {
            if (filmO.get().getLikes().contains(idUser)) {
                throw new FilmExistException("Film already liked user with ID - " + idUser);
            }
            filmO.get().addLike(idUser);
            log.info("The film (ID - " + idFilm + ") was liked by the user ID - " + idUser);
            return filmO.get();
        } else {
            throw new FilmExistException("Film or user not found");
        }
    }

    public Film deleteLikeOrThrow(int idFilm, int idUser) {
        Optional<Film> filmO = inMemoryFilmStorage.findFilmById(idFilm);
        Optional<User> userO = Optional.ofNullable(userService.getUserByIdOrThrow(idUser));
        if (filmO.isPresent() && userO.isPresent()) {
            if (filmO.get().getLikes().contains(idUser)) {
                filmO.get().deleteLike(idUser);
                log.info("The user (ID - " + idUser + ") removed like from the film (ID -" + idFilm);
                return filmO.get();
            }
            throw new FilmExistException("The film (ID - " + idFilm + ") was not liked by the user ID - " + idUser);
        } else {
            throw new FilmExistException("Film or user not found");
        }
    }

    public Film getFilmByIdOrThrow(int id) {
        Optional<Film> filmO = inMemoryFilmStorage.findFilmById(id);
        if (filmO.isPresent()) {
            log.info("Return user - " + filmO.get());
            return filmO.get();
        } else {
            throw new FilmExistException("The film with id = " + id + " not exist");
        }
    }

    public List<Film> getFilmPopular(int count) {
        List<Film> list = inMemoryFilmStorage.getFilmsStorage().stream()
                .sorted((f0, f1) -> Integer.compare(f1.getLikes().size(), f0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
        return list;
    }
}
