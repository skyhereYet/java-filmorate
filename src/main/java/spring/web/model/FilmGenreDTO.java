package spring.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FilmGenreDTO {
    private int id;
    private Genre genres;
}
