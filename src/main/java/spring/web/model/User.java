package spring.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
@Builder
public class User {
    private int id;
    @NonNull
    @Email
    @NotEmpty
    private final String email;
    @NotEmpty
    @NonNull
    private final String login;
    private String name;
    @NonNull
    private final LocalDate birthday;
}
