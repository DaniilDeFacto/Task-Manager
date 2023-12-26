package hexlet.code.dto.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Setter
@Getter
public class TaskUpdateDTO {
    @NotBlank
    private JsonNullable<String> title;

    private JsonNullable<Integer> index;

    private JsonNullable<String> content;

    @NotNull
    private JsonNullable<String> status;

    @NotNull
    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    private JsonNullable<Set<Long>> taskLabelIds;
}
