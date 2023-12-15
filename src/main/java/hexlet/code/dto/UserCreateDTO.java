package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreateDTO {
    @Email
    private String email;

    private String firstName;

    private String lastName;

    @Size(min = 3)
    private String password;
}
