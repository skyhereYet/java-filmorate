package spring.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.web.controller.FilmController;
import spring.web.exception.UserExistException;
import spring.web.exception.ValidationException;
import spring.web.model.User;
import spring.web.storage.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserStorage userDbStorage;
    private int id = 1;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public User createOrThrow(User user) {
        validateOrThrow(user);
        if (userDbStorage.userExist(user).isPresent()) {
            log.info("The user exists in the storage: " + user);
            throw new UserExistException("The user exist in the storage: " + user);
        } else {
            userDbStorage.save(user);
        }
        return user;
    }

    public User updateOrThrow(User user) {
        validateOrThrow(user);
        if (userDbStorage.userExist(user).isPresent()) {
            userDbStorage.update(user);
            return user;
        }  else {
            log.info("User not exists in the storage: " + user);
            throw new UserExistException("The user not exist in the storage: " + user);
        }
    }

    public List<User> getUserList() {
        log.info("Return user storage list");
        return userDbStorage.getUsersStorage();
    }

    public User getUserByIdOrThrow(int id) {
        Optional<User> userO = userDbStorage.findUserById(id);
        if (userO.isPresent()) {
            log.info("Return user - " + userO.get());
            return userO.get();
        } else {
            throw new UserExistException("User with id = " + id + " not exist");
        }
    }

    public User addFriendByIdOrThrow(int idUser, int idFriend) {
        if (userDbStorage.findUserById(idUser).isEmpty()
                || userDbStorage.findUserById(idFriend).isEmpty()) {
            throw new UserExistException("User or friend not found. ID user - " + idUser + ". ID friend - " + idFriend);
        }
        if (getUserFriends(idUser).contains(idFriend) || getUserFriends(idFriend).contains(idUser)) {
            throw new UserExistException("Method addFriendByIdOrThrow. Friend already added to user friends. " +
                    "ID user - " + idUser + ". ID friend - " + idFriend);
        }
        Optional<User> userO = userDbStorage.addFriend(idUser, idFriend);
        Optional<User> userFriendO = userDbStorage.addFriend(idFriend, idUser);
        if (userO.isPresent() && userFriendO.isPresent()) {
            log.info("User id = " + idFriend + " was add to friend list user id = " + idUser);
            log.info("User id = " + idUser + " was add to friend list user id = " + idFriend);
            return userO.get();
        } else {
            throw new UserExistException("Unexpected error when adding a friend");
        }
    }

    public User deleteFriendByIdOrThrow(int idUser, int idFriend) {
        if (userDbStorage.findUserById(idUser).isEmpty()
                || userDbStorage.findUserById(idFriend).isEmpty()) {
            throw new UserExistException("User or friend not found. ID user - " + idUser + ". ID friend - " + idFriend);
        }
        Optional<User> userO = userDbStorage.deleteFriend(idUser, idFriend);
        Optional<User> userFriendO = userDbStorage.deleteFriend(idFriend, idUser);
        if (userO.isPresent() && userFriendO.isPresent()) {
            log.info("Friend with id = " + idFriend + " removed from user's friends id = " + idUser);
            log.info("Friend with id = " + idUser + " removed from user's friends id = " + idFriend);
            return userO.get();
        } else {
            throw new UserExistException("Unexpected error when deleting a friend");
        }
    }

    public List<User> getUserFriends(int idUser) {
        Optional<User> userO = userDbStorage.findUserById(idUser);
        if (userO.isPresent()) {
            return userO.get().getFriends().stream()
                            .filter(idU -> userDbStorage.findUserById(idU).isPresent())
                            .map(idU -> userDbStorage.findUserById(idU).get())
                            .collect(Collectors.toList());
        } else {
            throw new UserExistException("User not found");
        }
    }

    private void validateOrThrow(User user) {
        if (user.getLogin().contains(" ")) {
            log.debug("Login contains space");
            throw new ValidationException("Login contains space");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    public List<User> getCommonFriends(int idUser, int otherId) {
        Optional<User> userO = userDbStorage.findUserById(idUser);
        Optional<User> otherUserO = userDbStorage.findUserById(otherId);
        if (userO.isPresent() && otherUserO.isPresent()) {
            return userO.get().getFriends().stream()
                    .filter(otherUserO.get().getFriends()::contains)
                    .filter(idU -> userDbStorage.findUserById(idU).isPresent())
                    .map(idU -> userDbStorage.findUserById(idU).get())
                    .collect(Collectors.toList());
        } else {
            throw new UserExistException("Someone user not exist");
        }
    }
}
