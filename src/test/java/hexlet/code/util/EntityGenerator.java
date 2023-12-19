package hexlet.code.util;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EntityGenerator {

    @Autowired
    private Faker faker;

    @Bean
    public User generateUser() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password(3, 16))
                .ignore(Select.field(User::getUpdatedAt))
                .ignore(Select.field(User::getCreatedAt))
                .create();
    }

    @Bean
    public TaskStatus generateTaskStatus() {
        return Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> faker.lorem().word())
                .supply(Select.field(TaskStatus::getSlag), () -> faker.lorem().word())
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .create();
    }
}
