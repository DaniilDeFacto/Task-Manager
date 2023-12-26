package hexlet.code.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @ToString.Include
    private String name;

    @ToString.Include
    private Integer index;

    @ToString.Include
    private String description;

    @ToString.Include
    @ManyToOne(optional = false)
    private TaskStatus taskStatus;

    @ToString.Include
    @ManyToOne(optional = false)
    private User assignee;

    @ManyToMany(cascade = CascadeType.MERGE)
    private Set<Label> labels = new HashSet<>();

    @CreatedDate
    private LocalDate createdAt;
}
