package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class UserUpdateDTO {
    @Email
    private JsonNullable<String> email;

    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;

    @Size(min = 3)
    private JsonNullable<String> password;
}
