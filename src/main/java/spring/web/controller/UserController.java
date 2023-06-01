package spring.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import spring.web.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
class UserController {
    private int id = 1;
    private HashMap<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) {
        log.info("POST request");
        validate(user);
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) {
        log.info("PUT request");
        validate(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("User with this id not found");
        }
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("GET request");
        return new ArrayList<>(users.values());
    }

    private void validate(User user) {
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.debug("Login empty or contains space");
            throw new ValidationException("Login empty or contains space");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Birthday from future");
            throw new ValidationException("Birthday from future");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
