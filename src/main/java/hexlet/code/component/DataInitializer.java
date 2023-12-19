package hexlet.code.component;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    private final CustomUserDetailsService userService;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            userService.createUser(generateAdmin());
        }
        for (var status : generateDefaultStatuses()) {
            if (taskStatusRepository.findBySlag(status.getSlag()).isEmpty()) {
                taskStatusRepository.save(status);
            }
        }
    }

    public static User generateAdmin() {
        var userData = new User();
        userData.setEmail("hexlet@example.com");
        userData.setPasswordDigest("qwerty");
        return userData;
    }

    public static List<TaskStatus> generateDefaultStatuses() {
        var draftStatus = getTaskStatus("Draft", "draft");
        var toReviewStatus = getTaskStatus("To review", "to_review");
        var toBeFixedStatus = getTaskStatus("To be fixed", "to_be_fixed");
        var toPublishStatus = getTaskStatus("To publish", "to_publish");
        var publishedStatus = getTaskStatus("Published", "published");
        return List.of(draftStatus, toReviewStatus, toBeFixedStatus, toPublishStatus, publishedStatus);
    }

    public static TaskStatus getTaskStatus(String name, String slug) {
        var taskStatus = new TaskStatus();
        taskStatus.setName(name);
        taskStatus.setSlag(slug);
        return taskStatus;
    }
}
