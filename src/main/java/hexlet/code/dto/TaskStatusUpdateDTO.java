package hexlet.code.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class TaskStatusUpdateDTO {
    @NotBlank
    @Column(unique = true)
    private JsonNullable<String> name;

    @NotBlank
    @Column(unique = true)
    private JsonNullable<String> slag;
}
