package spring.web.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import spring.web.controller.FilmController;
import spring.web.model.User;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage{
    private final Map<Integer, User> usersStorage = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @Override
    public void save(User user) {
        usersStorage.put(user.getId(), user);
        log.info("User add: " + user.toString());
    }

    @Override
    public void update(User user) {
        usersStorage.put(user.getId(), user);
        log.info("User update: " + user.toString());
    }

    @Override
    public Optional<User> findUserById(int id) {
        if (usersStorage.containsKey(id)) {
            return Optional.of(usersStorage.get(id));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> userExist(User user) {
        if (usersStorage.containsKey(user.getId())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public List<User> getUsersStorage() {
        return new ArrayList<>(usersStorage.values());
    }

    @Override
    public Optional<User> addFriend(int idUser, int idFriend) {
        try {
            usersStorage.get(idUser).addFriend(idFriend);
            log.info("Friend with id " + idFriend + " added to user id = : " + idUser);
            return Optional.of(usersStorage.get(idUser));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> deleteFriend(int idUser, int idFriend) {
        try {
            usersStorage.get(idUser).deleteFriend(idFriend);
            log.info("Friend with id = " + idFriend + " removed from user's friends id = " + idUser);
            return Optional.of(usersStorage.get(idUser));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }
}
