package spring.web.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import spring.web.controller.FilmController;
import spring.web.exception.GenreExistException;
import spring.web.model.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmDbStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final NamedParameterJdbcOperations jdbcOperations;

    public FilmDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Film save(Film film) {
        //валидировать жанры с БД
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            validateGenresInDb(film);
        }
        //записать фильм в БД (без жанров)
        String sqlQuery = "insert into FILMS (NAME, DESCRIPTION, RELEASE_DATE, RATE, DURATION, MPA_RATE_ID) "
                + "values (:name, :description, :release_date, :rate, :duration, :mpa_rate_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource mapQuery = getMapQuery(film);
        jdbcOperations.update(sqlQuery, mapQuery, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        log.info("Film add: " + film);
        //вносим фильм в список жанров
        addFilmGenresToDb(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        //валидировать жанры с БД
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            validateGenresInDb(film);
        }
        //обновить фильм
        String sqlQuery = "update FILMS set NAME = :name, DESCRIPTION = :description, RELEASE_DATE = :release_date, " +
                "RATE = :rate, DURATION = :duration, MPA_RATE_ID = :mpa_rate_id " +
                "where film_id = :film_id";
        MapSqlParameterSource mapQuery = getMapQuery(film);
        jdbcOperations.update(sqlQuery, mapQuery);
        log.info("Film update: " + film);
        //удалить жанры в которых был замечен этот фильм
        sqlQuery = "delete from FILM_GENRE where FILM_ID = :film_id";
        mapQuery = new MapSqlParameterSource();
        mapQuery.addValue("film_id", film.getId());
        jdbcOperations.update(sqlQuery, mapQuery);
        log.info("Film delete from FILM_GENRE table film_id = " + film.getId());
        //вносим фильм в список жанров
        addFilmGenresToDb(film);
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
        String sqlQuery = "select * from FILMS as F " +
                                "left join MPA_RATE as MR on F.MPA_RATE_ID = MR.MPA_RATE_ID " +

                                "where FILM_ID = :film_id";
        final List<Film> filmExistList = jdbcOperations.query(sqlQuery, Map.of("film_id", id),
                new FilmRowMapperWithoutGenres());
        if (filmExistList.isEmpty()) {
            return Optional.empty();
        }
        Set<Genre> s = getGenresByFilm(id);
        //заполнить список жанров и лайков
        filmExistList.get(0).setGenres(getGenresByFilm(id));
        return Optional.of(filmExistList.get(0));
    }

    @Override
    public Optional<Film> filmExist(Film film) {
        final String sqlQuery = "select FILM_ID from FILMS as F " +
                "left join MPA_RATE as MR on F.MPA_RATE_ID = MR.MPA_RATE_ID " +
                "where NAME = :name AND RELEASE_DATE = :release_date";
        final List<Film> filmExistList = jdbcOperations.query(sqlQuery, Map.of(
                        "name", film.getName(),
                        "release_date", film.getReleaseDate()),
                new FilmRowMapperWithoutGenres());
        return filmExistList.stream().findAny();
    }

    @Override
    public List<Film> getFilmsStorage() {
        final String sqlQuery = "select * " +
                "from FILMS as F " +
                "left join MPA_RATE as MR on F.MPA_RATE_ID = MR.MPA_RATE_ID";
        final List<Film> filmExistList = jdbcOperations.query(sqlQuery, new FilmRowMapperWithoutGenres());
        getGenresByFilmList(filmExistList);
        return filmExistList;
    }

    @Override
    public boolean checkLikes(int idFilm, int idUser) {
        final String sqlQuery = "select * from film_likes where FILM_ID = :film_id AND USERS_ID = :users_id";
        final List<Integer> likesExistList = jdbcOperations.query(sqlQuery, Map.of(
                        "film_id", idFilm,
                        "users_id", idUser),
                            new RowMapper<>() {
                                @Override
                                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    return rs.getInt(1);
                                }
                            });
        if (likesExistList.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void addLike(int idFilm, int idUser) {
        String sqlQuery;
        sqlQuery = "insert into FILM_LIKES (FILM_ID, USERS_ID) VALUES (:film_id, :users_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource mapQuery = new MapSqlParameterSource();
        mapQuery.addValue("film_id", idFilm);
        mapQuery.addValue("users_id", idUser);
        jdbcOperations.update(sqlQuery, mapQuery, keyHolder);
    }

    @Override
    public void deleteLike(int idFilm, int idUser) {
        String sqlQuery;
        sqlQuery = "delete from FILM_LIKES where FILM_ID = :film_id and USERS_ID = :users_id";
        MapSqlParameterSource mapQuery = new MapSqlParameterSource();
        mapQuery.addValue("film_id", idFilm);
        mapQuery.addValue("users_id", idUser);
        jdbcOperations.update(sqlQuery, mapQuery);
    }

    @Override
    public List<Film> getFilmPopular(int count) {
        final String sqlQuery = "SELECT F.*, LIKETABLE.LIKES, MR.* " +
                "FROM FILMS as F " +
                "left join MPA_RATE as MR on F.MPA_RATE_ID = MR.MPA_RATE_ID " +
                "LEFT JOIN (SELECT FILM_ID, COUNT(USERS_ID) as LIKES from FILM_LIKES " +
                "             GROUP BY FILM_ID) AS LIKETABLE on F.FILM_ID = LIKETABLE.FILM_ID " +
                "ORDER BY LIKETABLE.LIKES DESC " +
                "LIMIT :count";
        MapSqlParameterSource mapQuery = new MapSqlParameterSource();
        mapQuery.addValue("count", count);
        final List<Film> filmExistList = jdbcOperations.query(sqlQuery, mapQuery, new FilmRowMapperWithoutGenres());
        getGenresByFilmList(filmExistList);
        return filmExistList;
    }

    private void addFilmGenresToDb(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        String sqlQuery = "insert into FILM_GENRE (FILM_ID, GENRE_ID) VALUES (:film_id, :genre_id)";
        Map<String, Object>[] arrayGenres = new HashMap[film.getGenres().size()];
        int arrayCount = 0;
        for (Genre genre : film.getGenres()) {
            Map<String, Object> map = new HashMap<>();
            map.put("film_id", film.getId());
            map.put("genre_id", genre.getId());
            arrayGenres[arrayCount] = map;
            arrayCount++;
        }
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(arrayGenres);
        jdbcOperations.batchUpdate(sqlQuery, batch);
    }

    private void validateGenresInDb(Film film) {
        String sqlQuery = "select GENRE_ID from GENRES";
        final List<Integer> genresList = jdbcOperations.query(sqlQuery, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("GENRE_ID");
            }
        });
        Boolean checkGenresFilm = film.getGenres()
                                            .stream()
                                            .map(g -> g.getId())
                                            .allMatch(genresList::contains);
        if (!checkGenresFilm) {
            throw new GenreExistException("Film contains genre not exist");
        }
    }

    public TreeSet<Genre> getGenresByFilm(int idFilm) {
        String sqlQuery = "select G.GENRE_ID, G.NAME from GENRES as G " +
                "left join FILM_GENRE as FG on G.GENRE_ID = FG.GENRE_ID " +
                "where FILM_ID = :film_id " +
                "order by G.GENRE_ID";
        final TreeSet<Genre> genresList = new TreeSet<>(jdbcOperations.query(sqlQuery,
                Map.of("film_id", idFilm), new GenresDbStorage.GenreRowMapper()));
        log.info(genresList.toString());
        genresList.stream().sorted();
        log.info(genresList.toString());
        return genresList;
    }

    public List<Film> getGenresByFilmList(List<Film> filmList) {
        String sqlQuery = "select G.GENRE_ID, G.NAME, FG.FILM_ID from GENRES as G " +
                "left join FILM_GENRE as FG on G.GENRE_ID = FG.GENRE_ID " +
                "where FILM_ID in ( :film_id ) " +
                "order by FG.FILM_ID";
        List<Integer> filmListId = new ArrayList<>();
        for (Film film : filmList) {
            filmListId.add(film.getId());
        }
        final List<FilmGenreDTO> genresList = jdbcOperations.query(sqlQuery,
                Map.of("film_id", filmListId), new FilmGenresRowMapper());
        Map<Integer, Film> mapFilmId = new HashMap<>();
        for (Film film : filmList) {
            mapFilmId.put(film.getId(), film);
        }
        for (FilmGenreDTO filmGenreDTO : genresList) {
            Film film = mapFilmId.get(filmGenreDTO.getId());
            film.addGenre(filmGenreDTO.getGenres());
            mapFilmId.put(filmGenreDTO.getId(), film);
        }
        log.info(genresList.toString());
        return new ArrayList<>(mapFilmId.values());
    }

    private MapSqlParameterSource getMapQuery(Film film) {
        MapSqlParameterSource mapToReturn = new MapSqlParameterSource();
        mapToReturn.addValue("name", film.getName());
        mapToReturn.addValue("description", film.getDescription());
        mapToReturn.addValue("release_date", film.getReleaseDate());
        mapToReturn.addValue("rate", film.getRate());
        mapToReturn.addValue("duration", film.getDuration());
        mapToReturn.addValue("mpa_rate_id", film.getMpa().getId());
        mapToReturn.addValue("film_id", film.getId());
        return mapToReturn;
    }

    private static class FilmRowMapperWithoutGenres implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Film(rs.getInt("FILM_ID"),
                    rs.getString("NAME"),
                    rs.getString("DESCRIPTION"),
                    rs.getDate("RELEASE_DATE").toLocalDate(),
                    rs.getInt("RATE"),
                    rs.getInt("DURATION"),
                    new MPA(rs.getInt("MPA_RATE_ID"),
                            rs.getString("MPA_NAME")),
                    new TreeSet<>()
            );
        }
    }

    private static class FilmGenresRowMapper implements RowMapper<FilmGenreDTO>  {
        @Override
        public FilmGenreDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmGenreDTO(rs.getInt("Film_ID"),
                            new Genre(rs.getInt("GENRE_ID"),
                                    rs.getString("NAME")));
        }
    }
}