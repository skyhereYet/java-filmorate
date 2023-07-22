package spring.web.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import spring.web.controller.FilmController;
import spring.web.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        user.setId(keyHolder.getKey().intValue());
        log.info("User add: " + user);
        return user;
    }

    @Override
    public User update(User user) {
        final String sqlQuery = "update USERS set EMAIL = :email, LOGIN = :login, NAME = :name, BIRTHDAY = :birthday " +
                "where users_id = :users_id";
        MapSqlParameterSource mapQuery = getMapQuery(user);
        jdbcOperations.update(sqlQuery, mapQuery);
        log.info("User update: " + user.toString());
        return user;
    }

    @Override
    public Optional<User> findUserById(int id) {
        final String sqlQuery = "select * from USERS where USERS_ID = :users_id";
        final List<User> userExistList = jdbcOperations.query(sqlQuery, Map.of("users_id", id), new UserRowMapper());
        return userExistList.stream().findAny();
    }

    @Override
    public Optional<User> userExist(User user) {
        final String sqlQuery = "select * from USERS " +
                "where LOGIN = :login AND EMAIL = :email AND NAME = :name AND BIRTHDAY = :birthday";
        final List<User> userExistList = jdbcOperations.query(sqlQuery, Map.of(
                "login", user.getLogin(),
                "email", user.getEmail(),
                "name", user.getName(),
                "birthday", user.getBirthday()),
                new UserRowMapper());
        return userExistList.stream().findAny();
    }

    @Override
    public List<User> getUsersStorage() {
        return jdbcOperations.query("select * from USERS", new UserRowMapper());
    }

    @Override
    public void addFriend(int idUser, int idFriend) {
        String sqlQuery = "insert into FRIENDS (USERS_ID, FRIEND_ID) values (:users_id, :friend_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource mapQuery = new MapSqlParameterSource();
        mapQuery.addValue("users_id", idUser);
        mapQuery.addValue("friend_id", idFriend);
        jdbcOperations.update(sqlQuery, mapQuery, keyHolder);
        log.info("Friend add (users_id = " + idUser + " friend_id = " + idFriend);
    }

    @Override
    public void deleteFriend(int idUser, int idFriend) {
        String sqlQuery = "delete from FRIENDS where USERS_ID = :users_id and FRIEND_ID = :friend_id";
        MapSqlParameterSource mapQuery = new MapSqlParameterSource();
        mapQuery.addValue("users_id", idUser);
        mapQuery.addValue("friend_id", idFriend);
        jdbcOperations.update(sqlQuery, mapQuery);
        log.info("Friend delete (users_id = " + idUser + " friend_id = " + idFriend);
    }

    @Override
    public List<User> getFriendsByUserId(int idUser) {
        final String sqlQuery = "select * from USERS where USERS_ID IN (" +
                "select FRIEND_ID from FRIENDS where USERS_ID = :users_id)";
        return jdbcOperations.query(sqlQuery, Map.of("users_id", idUser),
                new UserRowMapper());
    }

    @Override
    public List<User> getCommonFriends(int idUser, int idFriend) {
        final String sqlQuery = "select * from USERS where USERS_ID in " +
                                        "(select U.FRIEND_ID from FRIENDS as U " +
                                        "inner join FRIENDS as F on U.FRIEND_ID = F.FRIEND_ID " +
                                        "where U.USERS_ID = :users_id and F.USERS_ID = :friend_id)";
        return jdbcOperations.query(sqlQuery,
                Map.of("users_id", idUser, "friend_id", idFriend),
                new UserRowMapper());
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getInt("USERS_ID"),
                    rs.getString("EMAIL"),
                    rs.getString("LOGIN"),
                    rs.getString("NAME"),
                    rs.getDate("BIRTHDAY").toLocalDate()
            );
        }
    }

    private MapSqlParameterSource getMapQuery(User user) {
        MapSqlParameterSource mapToReturn = new MapSqlParameterSource();
        mapToReturn.addValue("email", user.getEmail());
        mapToReturn.addValue("login", user.getLogin());
        mapToReturn.addValue("name", user.getName());
        mapToReturn.addValue("birthday", user.getBirthday());
        mapToReturn.addValue("users_id", user.getId());
        return mapToReturn;
    }
}