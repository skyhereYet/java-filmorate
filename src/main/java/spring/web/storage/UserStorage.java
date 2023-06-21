package spring.web.storage;

import spring.web.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    void save(User user);

    void update(User user);

    Optional<User> findUserById(int id);

    Optional<User> userExist(User user);

    List<User> getUsersStorage();

    Optional<User> addFriend(int idUser, int idFriend);

    Optional<User> deleteFriend(int idUser, int idFriend);
}
