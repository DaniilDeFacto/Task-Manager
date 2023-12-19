package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.EntityGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

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
public class TaskStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private EntityGenerator entityGenerator;

    private TaskStatus testTaskStatus;

    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        testTaskStatus = entityGenerator.generateTaskStatus();
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/task_statuses")
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var request = get("/api/task_statuses/" + testTaskStatus.getId())
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                a -> a.node("name").isEqualTo(testTaskStatus.getName()),
                a -> a.node("slag").isEqualTo(testTaskStatus.getSlag())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("name", testTaskStatus.getName());
        data.put("slag", testTaskStatus.getSlag());
        var request = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                a -> a.node("name").isEqualTo(testTaskStatus.getName()),
                a -> a.node("slag").isEqualTo(testTaskStatus.getSlag())
        );
        var taskStatus = taskStatusRepository.findBySlag(testTaskStatus.getSlag()).get();
        assertThat(taskStatus.getName()).isEqualTo(testTaskStatus.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var data = new HashMap<>();
        data.put("name", "otherName");
        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                a -> a.node("name").isEqualTo("otherName")
        );
        var user = taskStatusRepository.findById(testTaskStatus.getId()).get();
        assertThat(user.getName()).isEqualTo("otherName");
    }

    @Test
    public void testDelete() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var request = delete("/api/task_statuses/" + testTaskStatus.getId())
                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(taskStatusRepository.findById(testTaskStatus.getId())).isEmpty();
    }
}
