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
import spring.web.model.Film;
import spring.web.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class FilmDbStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final NamedParameterJdbcOperations jdbcOperations;

    public FilmDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Film save(Film film) {
        String sqlQuery = "insert into FILMS (NAME, DESCRIPTION, RELEASE_DATE, RATE, DURATION, MPA_RATE_ID) "
                + "values (:name, :description, :release_date, :rate, :duration, :mpa_rate_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource mapQuery = getMapQuery(film);
        jdbcOperations.update(sqlQuery, mapQuery, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey().intValue()));
        log.info("Film add: " + film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update FILMS set NAME = :name, DESCRIPTION = :description, RELEASE_DATE = :release_date, " +
                "RATE = :rate, DURATION = :duration, MPA_RATE_ID = :mpa_rate_id " +
                "where film_id = :film_id";
        MapSqlParameterSource mapQuery = getMapQuery(film);
        jdbcOperations.update(sqlQuery, mapQuery);
        log.info("Film update: " + film);
        return film;
    }

    @Override
    public void delete(Film film) {
        String sqlQuery = "delete from FILMS where FILM_ID = :film_id";
        MapSqlParameterSource mapQuery = new MapSqlParameterSource();
        mapQuery.addValue("film_id", film.getId());
        jdbcOperations.update(sqlQuery, mapQuery);
        log.info("Film delete (film_id = " + film.getId());
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        final String sqlQuery = "select * from FILMS where FILM_ID = :film_id";
        final List<Film> filmExistList = jdbcOperations.query(sqlQuery, Map.of("film_id", id),
                new FilmRowMapper());
        if (filmExistList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(filmExistList.get(0));
    }

    @Override
    public Optional<Film> filmExist(Film film) {
        final String sqlQuery = "select * from FILMS " +
                "where NAME = :name AND RELEASE_DATE = :release_date";
        final List<Film> filmExistList = jdbcOperations.query(sqlQuery, Map.of(
                        "name", film.getName(),
                        "release_date", film.getReleaseDate()),
                new FilmRowMapper());
        if (filmExistList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(filmExistList.get(0));
        }
    }

    @Override
    public List<Film> getFilmsStorage() {
        final String sqlQuery = "select * from FILMS";
        final List<Film> filmExistList = jdbcOperations.query(sqlQuery, new FilmRowMapper());
        return filmExistList;
    }

    private MapSqlParameterSource getMapQuery(Film film) {
        MapSqlParameterSource mapToReturn = new MapSqlParameterSource();
        mapToReturn.addValue("name", film.getName());
        mapToReturn.addValue("description", film.getDescription());
        mapToReturn.addValue("release_date", film.getReleaseDate());
        mapToReturn.addValue("rate", film.getRate());
        mapToReturn.addValue("duration", film.getDuration());
        mapToReturn.addValue("mpa_rate_id", film.getMpaRate());
        mapToReturn.addValue("film_id", film.getId());
        return mapToReturn;
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Film(rs.getInt("FILM_ID"),
                    rs.getString("NAME"),
                    rs.getString("DESCRIPTION"),
                    rs.getDate("RELEASE_DATE").toLocalDate(),
                    rs.getInt("RATE"),
                    rs.getInt("DURATION"),
                    rs.getInt("MPA_RATE_ID")
            );
        }
    }
}
