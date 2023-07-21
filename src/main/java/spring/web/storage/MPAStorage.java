package spring.web.storage;

import spring.web.model.MPA;
import java.util.List;
import java.util.Optional;

public interface MPAStorage {
    Optional<MPA> getByIdMPA(int idMPA);

    List<MPA> getAllMPA();
}