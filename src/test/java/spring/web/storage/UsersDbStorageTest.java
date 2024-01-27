package spring.web.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import spring.web.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@JdbcTest
@Import(UserDbStorage.class)
class UsersDbStorageTest {
    @Autowired
    private UserDbStorage userStorage;

    @Test
    public void findUser() {
        final User user = User.builder()
                .email("second@user.com")
                .login("second")
                .name("FirstName")
                .birthday(LocalDate.of(2021, 03, 14))
                .build();
        User newUser = userStorage.save(user);
        System.out.println(newUser);
        User userDB = userStorage.findUserById(newUser.getId()).get();
        Assertions.assertEquals(user, userDB);
    }

    @Test
    public void updateUser() {
        final User user = User.builder()
                .id(1)
                .email("update@user.com")
                .login("Update")
                .name("FirstName")
                .birthday(LocalDate.of(2021, 03, 14))
                .build();
        userStorage.update(user);
        User userDB = userStorage.findUserById(1).get();
        Assertions.assertEquals(user, userDB);
    }

    @Test
    public void getUsersStorage() {
        final User user = User.builder()
                .email("second@user.com")
                .login("second")
                .name("FirstName")
                .birthday(LocalDate.of(2021, 03, 14))
                .build();
        userStorage.save(user);
        List<User> usersList = userStorage.getUsersStorage();
        Assertions.assertTrue(usersList.size() == 2);
        Assertions.assertEquals(user, usersList.get(1));
    }

    @Test
    public void addDeleteFriend() {
        final User user = User.builder()
                .email("second@user.com")
                .login("second")
                .name("FirstName")
                .birthday(LocalDate.of(2021, 03, 14))
                .build();
        userStorage.save(user);
        List<Integer> usersId = userStorage.getUsersStorage().stream().map(u -> u.getId()).collect(Collectors.toList());
        userStorage.addFriend(usersId.get(0), usersId.get(1));
        Assertions.assertEquals(userStorage.getFriendsByUserId(usersId.get(0)).get(0), user);
        Assertions.assertTrue(userStorage.getFriendsByUserId(usersId.get(1)).isEmpty());
        userStorage.addFriend(usersId.get(1), usersId.get(0));
        Assertions.assertEquals(userStorage.getFriendsByUserId(usersId.get(0)).get(0), user);
        Assertions.assertEquals(userStorage.getFriendsByUserId(usersId.get(1)).get(0).getId(), usersId.get(0));
        userStorage.deleteFriend(usersId.get(0), usersId.get(1));
        Assertions.assertEquals(userStorage.getFriendsByUserId(usersId.get(1)).get(0).getId(), usersId.get(0));
        Assertions.assertTrue(userStorage.getFriendsByUserId(usersId.get(0)).isEmpty());
        userStorage.deleteFriend(usersId.get(1), usersId.get(0));
        Assertions.assertTrue(userStorage.getFriendsByUserId(usersId.get(0)).isEmpty());
        Assertions.assertTrue(userStorage.getFriendsByUserId(usersId.get(1)).isEmpty());
    }

    @Test
    public void getCommonFriend() {
        final User user2 = User.builder()
                .email("second@user.com")
                .login("second")
                .name("FirstName")
                .birthday(LocalDate.of(2021, 03, 14))
                .build();
        userStorage.save(user2);
        final User user3 = User.builder()
                .email("second@user.com")
                .login("second")
                .name("FirstName")
                .birthday(LocalDate.of(2021, 03, 14))
                .build();
        userStorage.save(user3);
        List<Integer> usersId = userStorage.getUsersStorage().stream().map(u -> u.getId()).collect(Collectors.toList());
        userStorage.addFriend(usersId.get(0), usersId.get(1));
        Assertions.assertEquals(userStorage.getFriendsByUserId(usersId.get(0)).get(0), user2);
        userStorage.addFriend(usersId.get(1), usersId.get(0));
        Assertions.assertEquals(userStorage.getFriendsByUserId(usersId.get(1)).get(0).getId(), usersId.get(0));
        userStorage.addFriend(usersId.get(2), usersId.get(0));
        Assertions.assertEquals(userStorage.getFriendsByUserId(usersId.get(1)).get(0).getId(), usersId.get(0));
        List<User> commonFriends = userStorage.getCommonFriends(usersId.get(1), usersId.get(2));
        Assertions.assertEquals(commonFriends.get(0).getId(), usersId.get(0));
    }
}