package spring.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import spring.web.utils.DurationSerializer;
import javax.validation.constraints.NotEmpty;
import java.time.Duration;
import java.time.LocalDate;

@Data
@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
@Builder
public class Film {
    private int id;
    @NonNull
    @NotEmpty
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private int rate;
    @JsonSerialize(using = DurationSerializer.class)
    private final Duration duration;
}

