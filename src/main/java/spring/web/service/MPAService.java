package spring.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spring.web.controller.FilmController;
import spring.web.exception.MPAExistException;
import spring.web.model.MPA;
import spring.web.storage.MPAStorage;
import java.util.List;
import java.util.Optional;

@Service
public class MPAService {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private MPAStorage mpaStorage;

    public MPAService(MPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public MPA getMpaBiIdOrThrow(int id) {
        Optional<MPA> mpaO = mpaStorage.getByIdMPA(id);
        if (mpaO.isPresent()) {
            log.info("Return MPA - " + mpaO.get());
            return mpaO.get();
        } else {
            throw new MPAExistException("MPA with id = " + id + " not exist");
        }
    }

    public List<MPA> getAllMpa() {
        log.info("Return MPA storage list");
        return mpaStorage.getAllMPA();
    }
}