package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate createdAt;
}
