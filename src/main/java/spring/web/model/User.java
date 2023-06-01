package spring.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
@AllArgsConstructor(onConstructor_={@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
public class User {
    private int id;
    @Email
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
}
