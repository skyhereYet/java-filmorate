package spring.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
public class Genre implements Comparable<Genre> {
    @NotNull
    @Positive
    private final int id;
    @NotNull
    private final String name;

    @Override
    public int compareTo(Genre o) {
            if (this.id != o.getId()) {
                return this.id - o.getId();
            }
            return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return id == genre.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}