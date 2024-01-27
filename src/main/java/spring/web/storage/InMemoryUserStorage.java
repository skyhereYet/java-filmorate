package spring.web.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import spring.web.controller.FilmController;
import spring.web.model.User;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> usersStorage = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @Override
    public User save(User user) {
        usersStorage.put(user.getId(), user);
        log.info("User add: " + user.toString());
        return user;
    }

    @Override
    public User update(User user) {
        usersStorage.put(user.getId(), user);
        log.info("User update: " + user.toString());
        return user;
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
    public void addFriend(int idUser, int idFriend) {
        try {
            usersStorage.get(idUser).addFriend(idFriend);
            log.info("Friend with id " + idFriend + " added to user id = : " + idUser);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void deleteFriend(int idUser, int idFriend) {
        try {
            usersStorage.get(idUser).deleteFriend(idFriend);
            log.info("Friend with id = " + idFriend + " removed from user's friends id = " + idUser);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<User> getFriendsByUserId(int idUser) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(int idUser, int idFriend) {
        return null;
    }
}
