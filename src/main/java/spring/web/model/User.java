package spring.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
@Builder
public class User {
    private int id;
    private final List<Integer> friends = new ArrayList<>();
    @NotNull
    @Email
    @NotEmpty
    private final String email;
    @NotEmpty
    @NotNull
    @NotBlank
    private final String login;
    private String name;
    @NotNull
    @PastOrPresent
    private final LocalDate birthday;

    public void addFriend(int idFriend) {
        friends.add(idFriend);
    }

    public void deleteFriend(int idFriend) {
        friends.remove(friends.indexOf(idFriend));
    }
}