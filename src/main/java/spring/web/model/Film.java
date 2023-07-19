package spring.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import spring.web.utils.DurationPositiveChecker;
import spring.web.utils.DurationSerializer;
import spring.web.utils.ReleaseDateChecker;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
@Builder
public class Film {
    private int id;
    @NotNull
    @NotEmpty
    private final String name;
    @Size(max = 200, message = "Description more 200 characters")
    private final String description;
    @ReleaseDateChecker
    private final LocalDate releaseDate;
    private int rate;
    @NotNull
    @Positive
    private final Integer duration;
    private final Integer mpaRate;
    private final List<Integer> likes = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(name, film.name) && Objects.equals(releaseDate, film.releaseDate);
    }

    public void addLike(int userID) {
         likes.add(userID);
    }

    public void deleteLike(int userID) {
        likes.remove(likes.indexOf(userID));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, releaseDate);
    }
}