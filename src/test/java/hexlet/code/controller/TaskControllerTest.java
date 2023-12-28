package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.EntityGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityGenerator entityGenerator;

    private User testUser;

    private TaskStatus testTaskStatus;

    private Label testLabel;

    private Task testTask;

    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        testUser = entityGenerator.generateUser();
        userRepository.save(testUser);
        testTaskStatus = entityGenerator.generateTaskStatus();
        taskStatusRepository.save(testTaskStatus);
        testLabel = entityGenerator.generateLabel();
        labelRepository.save(testLabel);
        testTask = entityGenerator.generateTask();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.setLabels(Set.of(testLabel));

        var anotherUser = entityGenerator.generateUser();
        userRepository.save(anotherUser);
        var anotherTaskStatus = entityGenerator.generateTaskStatus();
        taskStatusRepository.save(anotherTaskStatus);
        var anotherLabel = entityGenerator.generateLabel();
        labelRepository.save(anotherLabel);
        var anotherTask = entityGenerator.generateTask();
        anotherTask.setAssignee(anotherUser);
        anotherTask.setTaskStatus(anotherTaskStatus);
        anotherTask.setLabels(Set.of(anotherLabel));
        taskRepository.save(anotherTask);

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @AfterEach
    public void clean() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/tasks")
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize(1);
    }

    @Test
    public void testIndexWithParams() throws Exception {
        taskRepository.save(testTask);
        var request = get("/api/tasks?"
                + "titleCont=" + testTask.getName().substring(2)
                + "&assigneeId=" + testUser.getId()
                + "&status=" + testTaskStatus.getSlug()
                + "&labelId=" + testLabel.getId())
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize(1).allSatisfy(element ->
                assertThatJson(element)
                        .and(a -> a.node("title").isEqualTo(testTask.getName()))
                        .and(a -> a.node("assignee_id").isEqualTo(testUser.getId()))
                        .and(a -> a.node("status").isEqualTo(testTaskStatus.getSlug()))
                        .and(a -> a.node("taskLabelIds").isEqualTo(Set.of(testLabel.getId())))
        );
    }

    @Test
    public void testShow() throws Exception {
        taskRepository.save(testTask);
        var request = get("/api/tasks/" + testTask.getId())
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                a -> a.node("id").isEqualTo(testTask.getId()),
                a -> a.node("title").isEqualTo(testTask.getName()),
                a -> a.node("index").isEqualTo(testTask.getIndex()),
                a -> a.node("content").isEqualTo(testTask.getDescription()),
                a -> a.node("status").isEqualTo(testTaskStatus.getSlug()),
                a -> a.node("assignee_id").isEqualTo(testUser.getId()),
                a -> a.node("createdAt").isEqualTo(testTask.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),
                a -> a.node("taskLabelIds").isEqualTo(Set.of(testLabel.getId()))
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("title", testTask.getName());
        data.put("index", testTask.getIndex());
        data.put("content", testTask.getDescription());
        data.put("status", testTaskStatus.getSlug());
        data.put("assignee_id", testUser.getId());
        data.put("taskLabelIds", Set.of(testLabel.getId()));
        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                a -> a.node("title").isEqualTo(testTask.getName()),
                a -> a.node("index").isEqualTo(testTask.getIndex()),
                a -> a.node("content").isEqualTo(testTask.getDescription()),
                a -> a.node("status").isEqualTo(testTaskStatus.getSlug()),
                a -> a.node("assignee_id").isEqualTo(testUser.getId()),
                a -> a.node("taskLabelIds").isEqualTo(Set.of(testLabel.getId()))
        );
        var task = taskRepository.findByName(testTask.getName()).get();
        assertThat(task.getIndex()).isEqualTo(testTask.getIndex());
        assertThat(task.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(task.getTaskStatus()).isEqualTo(testTaskStatus);
        assertThat(task.getAssignee()).isEqualTo(testUser);
//        assertThat(task.getLabels()).isEqualTo(Set.of(testLabel));
    }

    @Test
    public void testUpdate() throws Exception {
        taskRepository.save(testTask);
        var data = new HashMap<>();
        data.put("title", "New title");
        data.put("index", 2023);
        data.put("content", "New content");
        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                a -> a.node("title").isEqualTo("New title"),
                a -> a.node("index").isEqualTo(2023),
                a -> a.node("content").isEqualTo("New content")
        );
        var task = taskRepository.findById(testTask.getId()).get();
        assertThat(task.getName()).isEqualTo("New title");
        assertThat(task.getIndex()).isEqualTo(2023);
        assertThat(task.getDescription()).isEqualTo("New content");
    }

    @Test
    public void testDelete() throws Exception {
        taskRepository.save(testTask);
        var repositorySize = taskRepository.findAll().size();
        var request = delete("/api/tasks/" + testTask.getId())
                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
        assertThat(taskRepository.findAll().size()).isEqualTo(repositorySize - 1);
    }
}
