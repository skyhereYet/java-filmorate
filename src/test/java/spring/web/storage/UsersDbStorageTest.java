package spring.web.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import spring.web.model.User;
import java.time.LocalDate;
import java.util.List;

@JdbcTest
@Import(UserDbStorage.class)
class UsersDbStorageTest {
    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    public void preparation() {
        final User user = User.builder()
                .email("first@user.com")
                .login("first")
                .name("FirstName")
                .birthday(LocalDate.of(2021, 03, 14))
                .build();
        userStorage.save(user);
        User userDB = userStorage.findUserById(1).get();
        Assertions.assertEquals(user, userDB);
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
        userStorage.addFriend(1, 2);
        Assertions.assertEquals(userStorage.getFriendsByUserId(1).get(0), user2);
        userStorage.addFriend(2, 1);
        Assertions.assertEquals(userStorage.getFriendsByUserId(2).get(0).getId(), 1);
        userStorage.addFriend(3, 1);
        Assertions.assertEquals(userStorage.getFriendsByUserId(2).get(0).getId(), 1);
        List<User> commonFriends = userStorage.getCommonFriends(2, 3);
        Assertions.assertEquals(commonFriends.get(0).getId(), 1);
    }
}