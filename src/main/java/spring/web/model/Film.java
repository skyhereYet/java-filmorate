package spring.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
public class Film {
    private int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private int rate;
    @JsonSerialize(using = MyDurationSerializer.class)
    private final Duration duration;
}

