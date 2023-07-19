package spring.web.storage;

import spring.web.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User save(User user);

    User update(User user);

    Optional<User> findUserById(int id);

    Optional<User> userExist(User user);

    List<User> getUsersStorage();

    void addFriend(int idUser, int idFriend);

    void deleteFriend(int idUser, int idFriend);

    List<User> getFriendsByUserId(int idUser);

    List<User> getCommonFriends(int idUser, int idFriend);
}
