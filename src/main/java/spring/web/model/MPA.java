package spring.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@Builder
public class MPA {
    @NotNull
    @Positive
    private final int id;
    @NotNull
    private final String name;
}