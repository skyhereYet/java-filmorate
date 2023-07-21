package spring.web.storage;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import spring.web.model.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class GenresDbStorage implements GenresStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public GenresDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Optional<Genre> getGenreById(int idGenre) {
        final String sqlQuery = "select * from GENRES where GENRE_ID = :genre_rate_id";
        final List<Genre> genreExistList = jdbcOperations.query(sqlQuery, Map.of("genre_rate_id", idGenre),
                new GenreRowMapper());
        if (genreExistList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(genreExistList.get(0));
    }

    @Override
    public List<Genre> getAllGenres() {
        final String sqlQuery = "select * from GENRES";
        final List<Genre> genreExistList = jdbcOperations.query(sqlQuery, new GenreRowMapper());
        return genreExistList;
    }

     public static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(rs.getInt("GENRE_ID"),
                            rs.getString("NAME")
            );
        }
    }
}
