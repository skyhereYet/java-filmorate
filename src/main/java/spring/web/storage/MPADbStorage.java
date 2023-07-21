package spring.web.storage;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import spring.web.model.MPA;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class MPADbStorage implements MPAStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public MPADbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Optional<MPA> getByIdMPA(int idMPA) {
        final String sqlQuery = "select * from MPA_RATE where MPA_RATE_ID = :mpa_rate_id";
        final List<MPA> mpaExistList = jdbcOperations.query(sqlQuery, Map.of("mpa_rate_id", idMPA),
                new MPARowMapper());
        if (mpaExistList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mpaExistList.get(0));
    }

    @Override
    public List<MPA> getAllMPA() {
        final String sqlQuery = "select * from MPA_RATE";
        final List<MPA> mpaExistList = jdbcOperations.query(sqlQuery, new MPARowMapper());
        return mpaExistList;
    }

    private static class MPARowMapper implements RowMapper<MPA> {
        @Override
        public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MPA(rs.getInt("MPA_RATE_ID"),
                            rs.getString("MPA_NAME")
            );
        }
    }
}