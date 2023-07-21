package spring.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.web.model.MPA;
import spring.web.service.MPAService;
import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MPAController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    @Autowired
    private MPAService mpaService;

    @GetMapping
    public List<MPA> getMpa() {
        log.info("GET request get all MPA");
        return mpaService.getAllMpa();
    }

    @GetMapping(value = "/{id}")
    public MPA getMpaById(@PathVariable int id) {
        log.info("GET request - get MPA by ID - " + id);
        return mpaService.getMpaBiIdOrThrow(id);
    }
}