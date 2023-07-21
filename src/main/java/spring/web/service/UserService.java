package spring.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import spring.web.controller.FilmController;
import spring.web.exception.UserExistException;
import spring.web.exception.ValidationException;
import spring.web.model.User;
import spring.web.storage.UserStorage;
import java.util.*;

@Service
public class UserService {
    @Qualifier("userDbStorage")
    private UserStorage userDbStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userDbStorage = userStorage;
    }

    public User createOrThrow(User user) {
        validateOrThrow(user);
        Optional<User> userOptional = userDbStorage.userExist(user);
        if (userOptional.isPresent()) {
            log.info("The user exists in the storage: " + userOptional.get());
            throw new UserExistException("The user exist in the storage: " + userOptional.get());
        } else {
            userDbStorage.save(user);
        }
        return user;
    }

    public User updateOrThrow(User user) {
        validateOrThrow(user);
        Optional<User> userOptional = userDbStorage.findUserById(user.getId());
        if (userOptional.isPresent()) {
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

    public void addFriendByIdOrThrow(int idUser, int idFriend) {
        if (userDbStorage.findUserById(idUser).isEmpty()
                || userDbStorage.findUserById(idFriend).isEmpty()) {
            throw new UserExistException("User or friend not found. ID user - " + idUser + ". ID friend - " + idFriend);
        }
        if (getUserFriends(idUser).contains(idFriend) || getUserFriends(idFriend).contains(idUser)) {
            throw new UserExistException("Method addFriendByIdOrThrow. Friend already added to user friends. " +
                    "ID user - " + idUser + ". ID friend - " + idFriend);
        }
        try {
            userDbStorage.addFriend(idUser, idFriend);
            log.info("Friend add (users_id = " + idUser + " friend_id = " + idFriend);
        } catch (Exception e) {
            throw new UserExistException("Unexpected error when adding a friend: " + e.getMessage());
        }
    }

    public void deleteFriendByIdOrThrow(int idUser, int idFriend) {
        if (userDbStorage.findUserById(idUser).isEmpty()
                || userDbStorage.findUserById(idFriend).isEmpty()) {
            throw new UserExistException("User or friend not found. ID user - " + idUser + ". ID friend - " + idFriend);
        }

        try {
            userDbStorage.deleteFriend(idUser, idFriend);
            log.info("Friend delete (users_id = " + idUser + " friend_id = " + idFriend);
            //userDbStorage.deleteFriend(idFriend, idUser);
            //log.info("Friend delete (users_id = " + idFriend + " friend_id = " + idUser);
        } catch (Exception e) {
            throw new UserExistException("Unexpected error when deleting a friend: " + e.getMessage());
        }
    }

    public List<User> getUserFriends(int idUser) {
            return userDbStorage.getFriendsByUserId(idUser);
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
        return userDbStorage.getCommonFriends(idUser, otherId);
    }
}