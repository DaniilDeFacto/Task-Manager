package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private User testUser;

    @BeforeEach
    private void setUp() {
        testUser = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password(3, 10))
                .ignore(Select.field(User::getUpdatedAt))
                .ignore(Select.field(User::getCreatedAt))
                .create();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        userRepository.save(testUser);
        var result = mockMvc.perform(get("/api/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                a -> a.node("email").isEqualTo(testUser.getEmail()),
                a -> a.node("firstName").isEqualTo(testUser.getFirstName()),
                a -> a.node("lastName").isEqualTo(testUser.getLastName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("email", testUser.getEmail());
        data.put("firstName", testUser.getFirstName());
        data.put("lastName", testUser.getLastName());
        data.put("password", testUser.getEmail());
        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                a -> a.node("email").isEqualTo(testUser.getEmail()),
                a -> a.node("firstName").isEqualTo(testUser.getFirstName()),
                a -> a.node("lastName").isEqualTo(testUser.getLastName())
        );
        var user = userRepository.findByEmail(testUser.getEmail()).get();
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(testUser.getLastName());
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(testUser);
        var data = new HashMap<>();
        data.put("email", "otheremail@mail.ru");
        data.put("password", "otherpas");
        var request = put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                a -> a.node("email").isEqualTo("otheremail@mail.ru")
        );
        var user = userRepository.findById(testUser.getId()).get();
        assertThat(user.getEmail()).isEqualTo("otheremail@mail.ru");
        assertThat(encoder.matches("otherpas", user.getPassword())).isTrue();
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(testUser);
        mockMvc.perform(delete("/api/users/" + testUser.getId()))
                .andExpect(status().isNoContent());
        var user = userRepository.findByEmail(testUser.getEmail());
        assertThat(user).isEmpty();
    }
}
