package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TaskDTO {
    private Long id;

    private String title;

    private Integer index;

    private String content;

    private String status;

    @JsonProperty("assignee_id")
    private Integer assigneeId;

    private LocalDate createdAt;
}
