package hexlet.code.dto.labels;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class LabelUpdateDTO {
    @Size(min = 3, max = 1000)
    @Column(unique = true)
    private JsonNullable<String> name;
}
