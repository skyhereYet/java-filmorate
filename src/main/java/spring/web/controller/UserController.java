package spring.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import spring.web.model.User;
import spring.web.service.UserService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("POST request");
        userService.createOrThrow(user);
        log.info("User add: " + user.toString());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("PUT request");
        userService.updateOrThrow(user);
        log.info("User update: " + user.toString());
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("GET request");
        return userService.getUserList();
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("GET request - get user by ID - " + id);
        return userService.getUserByIdOrThrow(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriendToUser(@PathVariable("id") int idUser,
                                @PathVariable("friendId") int idFriend) {
        userService.addFriendByIdOrThrow(idUser, idFriend);
        log.info("User id = " + idFriend + " was add to friend list user id = " + idUser);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriendFromUser(@PathVariable("id") int idUser,
                                   @PathVariable("friendId") int idFriend) {
        userService.deleteFriendByIdOrThrow(idUser, idFriend);
        log.info("Friend with id = " + idFriend + " removed from user's friends id = " + idUser);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getUserFriendsById(@PathVariable int id) {
        log.info("GET request - get user friends by ID - " + id);
        return userService.getUserFriends(id);
    }

    @GetMapping(value = "{id}/friends/common/{otherId}")
    public List<User> getUserFriendsById(@PathVariable("id") int idUser,
                                         @PathVariable("otherId") int otherId) {
        log.info("GET request - list of common friends with the user ID - " + idUser
                + " and friend with ID - " + otherId);
        return userService.getCommonFriends(idUser, otherId);
    }
}