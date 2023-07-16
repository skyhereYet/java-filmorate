package spring.web.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import spring.web.controller.FilmController;
import spring.web.model.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final NamedParameterJdbcOperations jdbcOperations;

    public UserDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public User save(User user) {
        String sqlQuery = "insert into USERS (EMAIL, LOGIN, NAME, BIRTHDAY) "
                + "values (:email, :login, :name, :birthday)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource mapQuery = getMapQuery(user);
        jdbcOperations.update(sqlQuery, mapQuery, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey().intValue()));
        log.info("User add: " + user.toString());
        return user;
    }

    private MapSqlParameterSource getMapQuery(User user) {
        MapSqlParameterSource mapToReturn = new MapSqlParameterSource();
        mapToReturn.addValue("email", user.getEmail());
        mapToReturn.addValue("login", user.getLogin());
        mapToReturn.addValue("name", user.getName());
        mapToReturn.addValue("birthday", user.getBirthday());
        return mapToReturn;
    }

    @Override
    public void update(User user) {

    }

    @Override
    public Optional<User> findUserById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> userExist(User user) {
        String sqlQuery = "select USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                "from USERS " +
                "where EMAIL = :email and LOGIN = :login and NAME = :name and BIRTHDAY = :birthday";
        MapSqlParameterSource mapQuery = getMapQuery(user);
        jdbcOperations.query(sqlQuery, mapQuery);
        user.setId(Objects.requireNonNull(keyHolder.getKey().intValue()));
        log.info("User add: " + user.toString());
        return user;
        return Optional.empty();
    }

    @Override
    public List<User> getUsersStorage() {
        return null;
    }

    @Override
    public Optional<User> addFriend(int idUser, int idFriend) {
        return Optional.empty();
    }

    @Override
    public Optional<User> deleteFriend(int idUser, int idFriend) {
        return Optional.empty();
    }
}
